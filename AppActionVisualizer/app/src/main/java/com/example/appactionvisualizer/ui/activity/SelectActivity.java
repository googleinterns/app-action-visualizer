package com.example.appactionvisualizer.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ParameterMapping;
import com.example.appactionvisualizer.ui.adapter.SelectRecyclerViewAdapter;

public class SelectActivity extends CustomActivity {
  private ParameterMapping parameterMapping;
  private String key;

  @Override
  void initData() {
    Intent intent = getIntent();
    parameterMapping = (ParameterMapping) intent.getSerializableExtra(Constant.PARAMETER_MAPPING);
    key = intent.getStringExtra(Constant.KEY);
  }

  public void finish(String identifier) {
    Intent intent = new Intent();
    intent.putExtra(Constant.KEY, key);
    intent.putExtra(Constant.IDENTIFIER, identifier);
    setResult(Activity.RESULT_OK, intent);
    finish();
  }

  @Override
  void initView() {
    super.initView();
    getSupportActionBar().setTitle(key);
    RecyclerView view = findViewById(R.id.rv_list);
    // Set the adapter
    if (view instanceof RecyclerView) {
      Context context = view.getContext();
      RecyclerView recyclerView = view;
      recyclerView.setLayoutManager(new LinearLayoutManager(context));
      recyclerView.setAdapter(new SelectRecyclerViewAdapter(parameterMapping.getKey2MapList().get(key), key, this));
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rv);
    initData();
    initView();
  }
}
