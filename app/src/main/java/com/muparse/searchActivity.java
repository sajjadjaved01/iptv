package com.muparse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class searchActivity extends AppCompatActivity {
    PlaylistAdapter mAdapter;
    ArrayAdapter mf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mf = new ArrayAdapter(this,R.layout.layout_search);
        ListView listView = (ListView) findViewById(R.id.list_view);
        EditText editText = (EditText) findViewById(R.id.myfilter);
        listView.setTextFilterEnabled(true);
        try{
        mAdapter = new PlaylistAdapter(this);
        listView.setAdapter((ListAdapter) mAdapter);}catch (Exception e) {Log.d("Google",""+e.toString());}


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
