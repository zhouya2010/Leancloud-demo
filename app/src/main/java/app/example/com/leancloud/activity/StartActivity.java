package app.example.com.leancloud.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import app.example.com.leancloud.R;

import static app.example.com.leancloud.R.id.email;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartActivity extends AppCompatActivity implements OnPageChangeListener, View.OnClickListener {

    /**
     * ViewPager
     */
    private ViewPager viewPager;

    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    /**
     * 图片资源id
     */
    private int[] imgIdArray ;

    private List<View> viewList;//view数组

    private TextView mLoginTv;

    private TextView mRegisterTv;
    private AutoCompleteTextView mEmailView;
    private EditText mUserNameView;
    private EditText mPasswordView;
    private EditText mRepasswordView ;
    private EditText mPhoneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.viewGroup);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        mLoginTv = (TextView)findViewById(R.id.login_btn);
        mRegisterTv = (TextView)findViewById(R.id.register_btn);

        mLoginTv.setOnClickListener(this);
        mRegisterTv.setOnClickListener(this);

        imgIdArray = new int[] {R.drawable.item01, R.drawable.item02, R.drawable.item03};

        //将点点加入到ViewGroup中
        tips = new ImageView[imgIdArray.length];
        for(int i=0; i<tips.length; i++){
            ImageView imageView = new ImageView(this);

            //控制点间距离
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.setMargins(5, 0, 5, 0);
            imageView.setLayoutParams(ll);


            tips[i] = imageView;
            if(i == 0){
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            linearLayout.addView(imageView);
        }

        //将图片装载到数组中
        viewList = new ArrayList<>();// 将要分页显示的View装入数组中
        for(int i=0; i<imgIdArray.length; i++){
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imgIdArray[i]);
            viewList.add(imageView);
        }

        //设置Adapter
        viewPager.setAdapter(new MyAdapter());
        //设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(this);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                loginDialog();
                break;
            case R.id.register_btn:
                registerDialog();
                break;
            default:
                break;
        }
    }


    private  void  registerDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.activity_register, null);

        final AlertDialog ad = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        ad.show();

        TextView login_btn = (TextView) view.findViewById(R.id.register_Tx);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister(view, ad);
            }
        });
    }

    private void loginDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.activity_login, null);

        final AlertDialog ad = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        ad.show();

        TextView login_btn = (TextView) view.findViewById(R.id.login_Tx);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(view, ad);
            }
        });

    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setImageBackground(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 设置选中的tip的背景
     * @param selectItems
     */
    private void setImageBackground(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i == selectItems){
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(View view, final AlertDialog ad) {

        mEmailView = (AutoCompleteTextView) view.findViewById(email);

        mPasswordView = (EditText) view.findViewById(R.id.password);
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);

                AVUser.logInInBackground(email, password, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {

                        if (e != null) {
                            Log.d("StartActivity", e.toString());
                            if (e.getCode() == 211){
                                Toast.makeText(StartActivity.this,  R.string.user_name_not_exist, Toast.LENGTH_SHORT).show();
                            }
                            else if (e.getCode() == 210) {
                                Toast.makeText(StartActivity.this,  R.string.error_incorrect_password, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            ad.cancel();
                            Log.d("StartActivity", avUser.toString());
                            Log.d("StartActivity", avUser.getEmail());
                            Log.d("StartActivity", avUser.getObjectId());

                            SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("username",avUser.getUsername());
                            editor.putString("email", avUser.getEmail());
                            editor.putString("objectId", avUser.getObjectId());
                            editor.putString("phoneNumber", avUser.getMobilePhoneNumber());
                            editor.putString("password", password);
                            editor.apply();

                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("username", avUser.getUsername());
                            bundle.putString("email", avUser.getEmail());
                            intent.putExtras(bundle);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                    }
                });
        }
    }

    private void attemptRegister( View view, final AlertDialog ad) {

        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.register_email_tx);
        mPasswordView = (EditText) view.findViewById(R.id.register_password_tx);
        mRepasswordView = (EditText) view.findViewById(R.id.register_re_password_tx);
        mPhoneView = (EditText)view.findViewById(R.id.register_telephone_tx);
        mUserNameView =  (EditText) view.findViewById(R.id.register_username_tx);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mRepasswordView.setError(null);
        mPhoneView.setError(null);
        mUserNameView.setError(null);

        String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        String repassword = mRepasswordView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String username = mUserNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (phone.length() != 11) {
            mPhoneView.setError(getString(R.string.prompt_phone_error));
            focusView = mPhoneView;
            cancel = true;
        }

        if (!repassword.equals(password)) {
            mRepasswordView.setError(getString(R.string.prompt_repassword_not_match));
            focusView = mRepasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(repassword) || !isPasswordValid(repassword)) {
            mRepasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mRepasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            final AVUser user = new AVUser();// 新建 AVUser 对象实例
            user.setUsername(username);// 设置用户名
            user.setPassword(password);// 设置密码
            user.setEmail(email);// 设置邮箱
            user.setMobilePhoneNumber(phone);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        // 注册成功
                        Toast.makeText(StartActivity.this, "Register success!", Toast.LENGTH_SHORT).show();
                        ad.cancel();

                        SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("username",user.getUsername());
                        editor.putString("email", user.getEmail());
                        editor.putString("objectId", user.getObjectId());
                        editor.putString("phoneNumber", user.getMobilePhoneNumber());
                        editor.putString("password", password);
                        editor.apply();

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("username", user.getUsername());
                        bundle.putString("email", user.getEmail());
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);

                        finish();

                    } else {
                        // 失败的原因可能有多种，常见的是用户名已经存在。
                        Log.d("StartActivity", e.toString());
                        if (e.getCode() == 203) {
                            Toast.makeText(StartActivity.this, "user name has existed", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(StartActivity.this, "register error!\r\nmessage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
