package app.example.com.leancloud.temp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.example.com.leancloud.R;


public class CardViewTestActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private HomeAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardview);

        initData();
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.HORIZONTAL));
//        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new HomeAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                Toast.makeText(CardViewTestActivity.this, " click img " + vh.getLayoutPosition(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initEvent() {
        Intent intent = new Intent();
    }

    protected void initData() {
        mDatas = new ArrayList<String>();
        mDatas.add(""+ 1 + " 赞同");
//        for (int i = 'A'; i < 'z'; i++) {
//            mDatas.add("" + (char) i);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                mAdapter.addData(1);
                break;
            case R.id.action_delete:
                mAdapter.removeData(1);
                break;
        }
        return true;
    }

    public class HomeAdapter extends RecyclerView.Adapter<MyViewHolder> {


        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    CardViewTestActivity.this).inflate(R.layout.cardview_route_details, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
//            holder.start_time_text.setText(mDatas.get(position));
            holder. start_time_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CardViewTestActivity.this, "click join", Toast.LENGTH_SHORT).show();
                }
            });

            Picasso.with(CardViewTestActivity.this).load(R.drawable.scenery1).transform(new CircleTransform()).resize(70,70).into(holder.head_img);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        public void addData(int position) {
            mDatas.add(position, ""+(mDatas.size() + 1) + " 赞同");
            notifyItemInserted(position);
        }

        public void removeData(int position) {
            mDatas.remove(position);
            notifyItemRemoved(position);
        }
    }
}
