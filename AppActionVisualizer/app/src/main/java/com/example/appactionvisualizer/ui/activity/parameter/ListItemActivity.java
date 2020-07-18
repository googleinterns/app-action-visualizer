package com.example.appactionvisualizer.ui.activity.parameter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ParameterMapping;
import com.example.appactionvisualizer.ui.activity.CustomActivity;
import com.example.appactionvisualizer.ui.adapter.SelectRecyclerViewAdapter;

public class ListItemActivity extends CustomActivity {
  private ParameterMapping parameterMapping;
  private String key;

  @Override
  protected void initData() {
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
  protected void initView() {
    super.initView();
    getSupportActionBar().setTitle(key);
    RecyclerView view = findViewById(R.id.list);
    // Set the adapter
    if (view != null) {
      Context context = view.getContext();
      RecyclerView recyclerView = view;
      recyclerView.setLayoutManager(new LinearLayoutManager(context));
      recyclerView.setAdapter(new SelectRecyclerViewAdapter(parameterMapping.getKey2MapList().get(key), key, this));
      DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
          new LinearLayoutManager(ListItemActivity.this).getOrientation());
      recyclerView.addItemDecoration(dividerItemDecoration);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.recycler_view);
    initData();
    initView();
  }
}
