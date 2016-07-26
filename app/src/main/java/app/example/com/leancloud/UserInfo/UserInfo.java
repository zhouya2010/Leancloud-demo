package app.example.com.leancloud.UserInfo;

/**
 * Created by Administrator on 2016/7/25.
 */

public class UserInfo {

    private String mImageUrl;

    private String mUserName;

    private String mEmail;

    private String mTelephone;

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public void setTelephone(String mTelephone) {
        this.mTelephone = mTelephone;
    }
}
