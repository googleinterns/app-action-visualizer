package com.example.appactionvisualizer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.ui.activity.dashboard.DashboardActivity;
import com.example.appactionvisualizer.ui.adapter.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

/**
 * The launcher page of the app
 * Use viewPager + fragment to display every type's apps list
 */
public class MainActivity extends AppCompatActivity {


  private void initView() {
    SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
    ViewPager viewPager = findViewById(R.id.view_pager);
    viewPager.setAdapter(sectionsPagerAdapter);
    TabLayout tabs = findViewById(R.id.tabs);
    tabs.setupWithViewPager(viewPager);
    addDashBoard();
  }

  private void addDashBoard() {
    FloatingActionButton fab = findViewById(R.id.fab);
    String url = "https://link.taxi.eu/app/products";
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        Utils.jumpToApp(MainActivity.this, url, "at.austrosoft.t4me.MB_BerlinTZBEU");
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    AppActionsGenerator.getInstance().readFromFile(MainActivity.this);
    initView();
  }
}