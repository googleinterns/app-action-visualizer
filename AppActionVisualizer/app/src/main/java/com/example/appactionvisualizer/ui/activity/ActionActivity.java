package com.example.appactionvisualizer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.ui.adapter.ActionRecyclerViewAdapter;

import java.util.Objects;


/**
 * Displays all the actions of an app using recyclerview
 */
public class ActionActivity extends CustomActivity {
  private AppAction appAction;

  @Override
  protected void initData() {
    Intent intent = getIntent();
    appAction = (AppAction) intent.getSerializableExtra(Constant.APP_NAME);
  }

  @Override
  protected void initView() {
    super.initView();
    Objects.requireNonNull(getSupportActionBar()).setTitle(appAction.getPackageName());
    RecyclerView view = findViewById(R.id.list);
    // Set the adapter
    if (view != null) {
      Context context = view.getContext();
      view.setLayoutManager(new LinearLayoutManager(context));
      view.setAdapter(new ActionRecyclerViewAdapter(appAction, this));
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