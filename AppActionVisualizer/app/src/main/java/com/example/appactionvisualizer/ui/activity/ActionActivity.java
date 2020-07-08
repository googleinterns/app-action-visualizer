package com.example.appactionvisualizer.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppAction;
import com.example.appactionvisualizer.ui.adapter.ActionRecyclerViewAdapter;


/**
 * Displays all the actions of an app using recyclerview
 */
public class ActionActivity extends CustomActivity {
  private AppAction appAction;

  @Override
  void initData() {
    Intent intent = getIntent();
    appAction = (AppAction) intent.getSerializableExtra(Constant.APP_NAME);
  }

  @Override
  void initView() {
    super.initView();
    getSupportActionBar().setTitle(appAction.getAppName());
    RecyclerView view = findViewById(R.id.rv_list);
    // Set the adapter
    if (view instanceof RecyclerView) {
      Context context = view.getContext();
      RecyclerView recyclerView = view;
      recyclerView.setLayoutManager(new LinearLayoutManager(context));
      recyclerView.setAdapter(new ActionRecyclerViewAdapter(appAction, this));
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