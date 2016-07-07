package app.example.com.leancloud.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import app.example.com.leancloud.R;

/**
 * Created by Administrator on 2016/7/6.
 */

public class UserInfoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView mUserName;
    TextView mEmail;
    TextView mTelephone;
    ImageView mHeadImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_user_info);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.user_info));
        }

        mUserName = (TextView) findViewById(R.id.user_name_text);
        mEmail = (TextView) findViewById(R.id.email_text);
        mTelephone = (TextView) findViewById(R.id.telephone_text);
        mHeadImg = (ImageView)findViewById(R.id.head_img);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        Log.d("UserInfoActivity", "onRefresh");
        SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String objectId = sp.getString("objectId", "null");
        AVQuery<AVObject> avQuery = new AVQuery<>("_User");
        avQuery.getInBackground(objectId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                AVUser avUser = (AVUser)avObject;
                Log.d("UserInfoActivity", avUser.toString());
                Log.d("UserInfoActivity", avUser.getEmail());
                mUserName.setText(avUser.getUsername());
                mEmail.setText(avUser.getEmail());
                mTelephone.setText(avUser.getMobilePhoneNumber());
                try {
                    Picasso.with(UserInfoActivity.this).load(avUser.getJSONObject("headimage").getString("url")).into(mHeadImg);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
