package com.example.appactionvisualizer.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.Actions.AppAction;
import com.example.appactionvisualizer.ui.adapter.ActionRecyclerViewAdapter;


/**
 * Displays all the actions of an app using recyclerview
 */
public class ActionActivity extends AppCompatActivity {
  private AppAction appAction;

  @Override
  public boolean onSupportNavigateUp(){
    finish();
    return true;
  }

  private void initData() {
    Intent intent = getIntent();
    appAction = (AppAction) intent.getSerializableExtra(Constant.APP);
  }

  private void initView() {

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(appAction.getAppName());

    RecyclerView view = (RecyclerView) findViewById(R.id.rv_list);
    // Set the adapter
    if (view instanceof RecyclerView) {
      Context context = view.getContext();
      RecyclerView recyclerView = (RecyclerView) view;
      recyclerView.setLayoutManager(new LinearLayoutManager(context));
      recyclerView.setAdapter(new ActionRecyclerViewAdapter(appAction, this));
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_action);
    initData();
    initView();
  }
}