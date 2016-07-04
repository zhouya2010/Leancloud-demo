package app.example.com.leancloud;

import android.app.ProgressDialog;
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
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

import app.example.com.leancloud.util.ToastUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationSource,
        AMapLocationListener, PoiSearch.OnPoiSearchListener, AMap.OnMarkerClickListener,AMap.InfoWindowAdapter {

    private TextView mUserNameView;
    private TextView mEmailView;
    private TextView cityTextView;
    private TextView searchTextView;
    private ImageView imageView;
    private LinearLayout searchLinearLayout;
    private LinearLayout myLocationBtn;

    private MapView mMapView = null;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MarkerOptions markerOption;
    private AMapLocation myAmapLocation;

    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private String keyWord = "";// 要输入的poi搜索关键字
    private String district = "";// 搜索区域

    private ProgressDialog progDialog = null;// 搜索时进度条

    public static final int REQUESTCODE_LONGIN_ACTIVITY = 0;
    public static final int REQUESTCODE_SEARCH_ACTIVITY = 1;

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

        cityTextView = (TextView) findViewById(R.id.city_text);
        searchLinearLayout = (LinearLayout) findViewById(R.id.search_btn);
        searchLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MySearchActivity.class);
//                Intent intent = new Intent(MainActivity.this, PoiKeywordSearchActivity.class);
                startActivityForResult(intent, REQUESTCODE_SEARCH_ACTIVITY);
            }
        });

        searchTextView = (TextView) findViewById(R.id.search_content_text);

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
                    startActivityForResult(intent, REQUESTCODE_LONGIN_ACTIVITY);
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

        myLocationBtn = (LinearLayout) findViewById(R.id.my_location_btn);
        myLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null && myAmapLocation != null) {
                    if (myAmapLocation.getErrorCode() == 0) {
//                        showMyLocation(myAmapLocation);
                        Toast.makeText(MainActivity.this, "click myLocationBtn", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
                .fromResource(R.drawable.ic_local_florist_black_24dp));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);

        markerOption = new MarkerOptions();
//        markerOption.position(Constants.XIAN);
//        markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
//        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_directions_car_black_24dp));
        aMap.addMarker(markerOption);

//        Marker marker2 = aMap.addMarker(markerOption);
//        marker2.showInfoWindow();
//        // marker旋转90度
//        marker2.setRotateAngle(90);
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
        if (resultCode == RESULT_OK && requestCode == REQUESTCODE_LONGIN_ACTIVITY) {
            mUserNameView.setText(data.getExtras().getString("username"));
            mEmailView.setText(data.getExtras().getString("email"));
            Picasso.with(MainActivity.this).load("http://ac-t6DfMHfs.clouddn.com/5407d31a915492a2.png").into(imageView);
        } else if (resultCode == RESULT_OK && requestCode == REQUESTCODE_SEARCH_ACTIVITY) {
            Log.d("MainActivity", data.getExtras().getString("content"));
            Log.d("MainActivity", data.getExtras().getString("city"));
            keyWord = data.getExtras().getString("content");
            district = data.getExtras().getString("city");
            if ("".equals(keyWord)) {
                ToastUtil.show(MainActivity.this, "请输入搜索关键字");
            } else {
                searchTextView.setText(keyWord);
                doSearchQuery();
            }
        }
    }

    protected void doSearchQuery() {
        showProgressDialog();// 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", district);
        query.setPageSize(5);
        query.setPageNum(currentPage);
        query.setCityLimit(true);

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();
        Log.e("MainActivity", "rCode:" + rCode);
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {

                if (result.getQuery().equals(query)) {
                    poiResult = result;

                    List<PoiItem> poiItems = poiResult.getPois();
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        Log.e("MainActivity", "suggestionCities");
                    } else {
                        ToastUtil.show(MainActivity.this,
                                R.string.no_result);
                    }

                } else {
                    Log.e("MainActivity", "equals query");
                }
            } else {
                Log.e("MainActivity", "result null");
            }
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                myAmapLocation = amapLocation;
                showMyLocation(amapLocation);
            }
            else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                cityTextView.setText("定位失败");
                Log.e("AmapErr", errText);
            }
        }
    }

    private void showMyLocation(AMapLocation amapLocation) {
        mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
        Log.d("MainActivity", amapLocation.getAddress());
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        cityTextView.setText(amapLocation.getCity());
                mlocationClient.onDestroy();
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

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.ic_directions_car_black_24dp));
        marker.showInfoWindow();
        Log.d("MainActivity", "onMarkerClick" + marker.getTitle());
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Log.e("MainActivity", "getInfoWindow");
        View view = getLayoutInflater().inflate(R.layout.marker_item, null);
        TextView title = (TextView) view.findViewById(R.id.marker_title);
        title.setText(marker.getTitle());
        TextView snippet = (TextView) view.findViewById(R.id.marker_snippet);
        snippet.setText(marker.getSnippet());
        TextView img = (TextView) view.findViewById(R.id.marker_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Img onClick");
            }
        });
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
