package com.example.appactionvisualizer.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.TestGenerator;
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
    FloatingActionButton fab = findViewById(R.id.fab);

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String url = "mytaxi://de.mytaxi.passenger/order?token=GOOGLE_MAPS&af_deeplink=true&af_reengagement_window=1h&campaign=global_120319_my_pa_in_0_gl_gl_-_an_mx_co_pr_af_-_ge_-_-_-_-_-&is_retargeting=true&media_source=google-maps&pickup_establishment=UCI&destination_establishment=COSTCO";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        MainActivity.this.startActivity(intent);
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
//    AppAction.parseData();
    TestGenerator.getInstance().readFromFile(this);
    initView();
  }
}