package app.example.com.leancloud;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by Administrator on 2016/5/25.
 */

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "t6DfMHfszYL0cQzrTxyF0Oqa-gzGzoHsz", "0ithF6wFhqC7TVDRBiW4e0ew");
    }
}
