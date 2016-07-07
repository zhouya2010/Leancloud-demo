package app.example.com.leancloud.temp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import app.example.com.leancloud.R;

/**
 * Created by Administrator on 2016/7/7.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView textView;

    public MyViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.id_num);
    }
}
