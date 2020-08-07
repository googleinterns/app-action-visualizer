package com.example.appactionvisualizer.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Custom activity interface
 * The activity has a back button on the action bar
 */
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

}
