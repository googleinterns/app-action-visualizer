package com.example.appactionvisualizer.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.Action;
import com.example.appactionvisualizer.databean.AppAction;
import com.example.appactionvisualizer.ui.adapter.ActionRecyclerViewAdapter;
import com.example.appactionvisualizer.ui.adapter.FulfillmentRecyclerViewAdapter;


/**
 * Displays all the fulfillment of an action using recyclerview
 */
public class FulfillmentActivity extends AppCompatActivity {

  private static final String TAG = "FulfillmentActivity";
  private Action action;
  private String appName;

  @Override
  public boolean onSupportNavigateUp(){
    finish();
    return true;
  }

  private void initData() {
    Intent intent = getIntent();
    action = (Action) intent.getSerializableExtra(Constant.ACTION);
    appName = intent.getStringExtra(Constant.APP_NAME);
  }

  private void initView() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(action.getIntentName().split("\\.")[2]);
    RecyclerView view = findViewById(R.id.rv_list);
    // Set the adapter
    if (view instanceof RecyclerView) {
      Context context = view.getContext();
      RecyclerView recyclerView = view;
      recyclerView.setLayoutManager(new LinearLayoutManager(context));
      recyclerView.setAdapter(new FulfillmentRecyclerViewAdapter(action, this));
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