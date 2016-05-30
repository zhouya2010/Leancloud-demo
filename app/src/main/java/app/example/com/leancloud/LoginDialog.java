package app.example.com.leancloud;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Administrator on 2016/5/27.
 */

public class LoginDialog extends Dialog {

    private Context context;

    public LoginDialog(Context context) {
        super(context);

        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.activity_login, null);

    }
}
