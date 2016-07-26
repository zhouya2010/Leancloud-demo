package app.example.com.leancloud.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;

import org.json.JSONException;

import app.example.com.leancloud.R;

/**
 * Created by Administrator on 2016/7/25.
 */

public class UserinfoPresenter implements UserInfoRequest{
    private UserInfoView userInfoView;
    private UserInfo userInfo;
    private Context context;

    public UserinfoPresenter(UserInfoView userInfoView, Context context) {
        this.userInfoView = userInfoView;
        userInfo = new UserInfo();
        this.context =context;
    }


    @Override
    public void getUserInfo() {
        userInfoView.showLoading();
        SharedPreferences sp = context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String objectId = sp.getString("objectId", "null");
        AVQuery<AVObject> avQuery = new AVQuery<>("_User");
        avQuery.getInBackground(objectId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                AVUser avUser = (AVUser)avObject;
                userInfoView.setUserName(avUser.getUsername());
                userInfoView.setEmail(avUser.getEmail());
                try {
                    userInfoView.setImageView(avUser.getJSONObject("headimage").getString("url"));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                userInfoView.setTelephone(avUser.getMobilePhoneNumber());
                userInfoView.hideLoading();
            }
        });
    }
}
