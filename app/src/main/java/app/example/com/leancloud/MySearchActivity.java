package app.example.com.leancloud;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AutoCompleteTextView;

/**
 * Created by Administrator on 2016/6/28.
 */

public class MySearchActivity extends AppCompatActivity implements TextWatcher {

    AutoCompleteTextView searchText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        searchText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d("MySearchActivity", "onTextChanged");
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
