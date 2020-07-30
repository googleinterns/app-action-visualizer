package com.example.appactionvisualizer.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public abstract class CustomActivity extends AppCompatActivity {

  @Override
  public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  protected abstract void initData();

  protected void initView() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null)
      actionBar.setDisplayHomeAsUpEnabled(true);
  }

  ;

}
