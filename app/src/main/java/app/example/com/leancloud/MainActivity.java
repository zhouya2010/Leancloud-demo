package app.example.com.leancloud;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationSource,
        AMapLocationListener {

    private TextView mUserNameView;
    private TextView mEmailView;
    private TextView cityTextView;
    private ImageView imageView;
    private LinearLayout searchLinearLayout;

    private MapView mMapView = null;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.d("MainActivity", "navigationView.getHeaderCount():" + navigationView.getHeaderCount());

        mUserNameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name_textView);
        mEmailView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.email_textView);
        imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);

        cityTextView = (TextView)findViewById(R.id.city_text);
        searchLinearLayout = (LinearLayout)findViewById(R.id.search_btn);
        searchLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "onclick", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, MySearchActivity.class);
                startActivityForResult(intent, 0);
            }
        });


        SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);

        String email = sp.getString("username", "null");
        String password = sp.getString("password", "null");

        AVUser.logInInBackground(email, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {

                if (e != null) {
                    if (e.getCode() == 211) {
                        Toast.makeText(MainActivity.this, R.string.user_name_not_exist, Toast.LENGTH_SHORT).show();
                    } else if (e.getCode() == 210) {
                        Toast.makeText(MainActivity.this, R.string.error_incorrect_password, Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
                    startActivityForResult(intent, 0);
                } else {
                    mUserNameView.setText(avUser.getUsername());
                    mEmailView.setText(avUser.getEmail());
                    try {
                        Picasso.with(MainActivity.this).load(avUser.getJSONObject("headimage").getString("url")).into(imageView);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        mapInit();

    }

    private void mapInit() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            UiSettings settings = aMap.getUiSettings();
//            settings.setZoomPosition(1);
            settings.setZoomControlsEnabled(false);
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_room_black_24dp));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Snackbar.make(mMapView, "Camera", Snackbar.LENGTH_SHORT).show();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        } else if (id == R.id.nav_gallery) {
            aMap.moveCamera(CameraUpdateFactory.zoomTo(5));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0) {
            mUserNameView.setText(data.getExtras().getString("username"));
            mEmailView.setText(data.getExtras().getString("email"));
            Picasso.with(MainActivity.this).load("http://ac-t6DfMHfs.clouddn.com/5407d31a915492a2.png").into(imageView);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                Log.d("MainActivity", amapLocation.getAddress());
                aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                cityTextView.setText(amapLocation.getCity());
                mlocationClient.onDestroy();
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                cityTextView.setText("定位失败");
                Log.e("AmapErr",errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        Log.e("MainActivity", "deactivate");

        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}
