package app.example.com.leancloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.example.com.leancloud.util.AMapUtil;

/**
 * Created by Administrator on 2016/6/28.
 */

public class MySearchActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener,
        Inputtips.InputtipsListener, AdapterView.OnItemClickListener {

    AutoCompleteTextView searchText;
    private String keyWord = "";// 要输入的poi搜索关键字
    private TextView cityTextView;
    private TextView cancelBtn;
    private ListView resultListView;

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    List<HashMap<String, String>> tipsResult;
    SimpleAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        searchText.addTextChangedListener(this);

        cancelBtn = (TextView) findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(this);

        cityTextView = (TextView) findViewById(R.id.city_text_view);
        resultListView = (ListView) findViewById(R.id.result_list);
        tipsResult = new ArrayList<HashMap<String, String>>();
        adapter = new SimpleAdapter(this,tipsResult,android.R.layout.simple_list_item_2 ,new String[] { "Content",
                "District" }, new int[] { android.R.id.text1, android.R.id.text2 });
        resultListView.setAdapter(adapter);
        resultListView.setOnItemClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, cityTextView.getText().toString());
            Inputtips inputTips = new Inputtips(MySearchActivity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch (id) {
            case R.id.cancel_btn:
                finish();
                break;
            default:break;
        }
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        int size = list.size();
        tipsResult.clear();
        for(Tip tip:list) {
            HashMap<String, String> tipHash = new HashMap<>();
            tipHash.put("Content", tip.getName());
            tipHash.put("District", tip.getDistrict());
            tipsResult.add(tipHash);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        searchText.setText(tipsResult.get(position).get("Content"));
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("content", tipsResult.get(position).get("Content"));
        bundle.putString("city", tipsResult.get(position).get("District"));
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
