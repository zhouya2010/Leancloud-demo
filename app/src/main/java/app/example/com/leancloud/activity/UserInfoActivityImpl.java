package app.example.com.leancloud.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import app.example.com.leancloud.R;
import app.example.com.leancloud.UserInfo.UserInfoView;
import app.example.com.leancloud.UserInfo.UserinfoPresenter;

/**
 * Created by Administrator on 2016/7/26.
 */

public class UserInfoActivityImpl extends AppCompatActivity implements UserInfoView, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mUserName;
    private TextView mEmail;
    private TextView mTelephone;
    private ImageView mHeadImg;

    private UserinfoPresenter userinfoPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        userinfoPresenter = new UserinfoPresenter(this, this);
        userinfoPresenter.getUserInfo();
    }

    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setImageView(String imageUrl) {
        Picasso.with(UserInfoActivityImpl.this).load(imageUrl).into(mHeadImg);
    }

    @Override
    public void setUserName(String userName) {
        mUserName.setText(userName);
    }

    @Override
    public void setEmail(String email) {
        mEmail.setText(email);
    }

    @Override
    public void setTelephone(String telephone) {
        mTelephone.setText(telephone);
    }

    @Override
    public void onRefresh() {
        userinfoPresenter.getUserInfo();
        Toast.makeText(this, "onRefresh", Toast.LENGTH_SHORT).show();
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
}
