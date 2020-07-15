package com.example.appactionvisualizer.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public abstract class CustomActivity extends AppCompatActivity {

  @Override
  public boolean onSupportNavigateUp(){
    finish();
    return true;
  }

  abstract void initData();

  void initView() {
    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
  };

}
