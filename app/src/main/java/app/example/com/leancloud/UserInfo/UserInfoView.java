package app.example.com.leancloud.UserInfo;

/**
 * Created by Administrator on 2016/7/25.
 */

public interface UserInfoView {

    void showLoading();
    void hideLoading();

    void setImageView(String imageUrl);
    void setUserName(String userName);
    void setEmail(String email);
    void setTelephone(String telephone);
}
