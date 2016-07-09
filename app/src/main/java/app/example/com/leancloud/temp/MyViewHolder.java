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
    ImageView content_img;
    TextView pub_time;
    TextView join_text;
    TextView gree_num;

    public MyViewHolder(View itemView) {
        super(itemView);
        pub_time = (TextView) itemView.findViewById(R.id.pub_time);
        join_text = (TextView) itemView.findViewById(R.id.jone_text);
        gree_num = (TextView) itemView.findViewById(R.id.gree_num);
        head_img = (ImageView) itemView.findViewById(R.id.head_img);
        content_img = (ImageView) itemView.findViewById(R.id.content_img);
//        join_text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("HomeAdapter", "click join ");
//            }
//        });
    }
}
