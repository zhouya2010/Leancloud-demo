package app.example.com.leancloud.temp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.example.com.leancloud.R;

/**
 * Created by Administrator on 2016/7/7.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView head_img;
    TextView start_point_text;
    TextView end_point_text;
    TextView start_time_text;
    TextView usr_mane_text;

    public MyViewHolder(View itemView) {
        super(itemView);
        start_point_text = (TextView) itemView.findViewById(R.id.start_point);
        end_point_text = (TextView) itemView.findViewById(R.id.end_point);
        start_time_text = (TextView) itemView.findViewById(R.id.start_time);
        usr_mane_text = (TextView) itemView.findViewById(R.id.user_name_text);
        head_img = (ImageView) itemView.findViewById(R.id.head_imageview);
    }
}
