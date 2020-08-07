package com.example.appactionvisualizer.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppActionProtos;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.ui.adapter.ActionRecyclerViewAdapter;

import java.util.Objects;

public class DashboardActivity extends CustomActivity {

  private static final String TAG = "DashboardActivity";
  private String parameters;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);
    initData();
    initView();
  }


  @Override
  protected void initData() {
    int allFulfillmentSize = 0;
    int zeroParameter = 0, singleParameter = 0, multiParameter = 0;
    int isCoordinates = 0;
    int slice = 0;
    for(AppActionProtos.AppAction appAction : AppActionsGenerator.appActions) {
      for (AppActionProtos.Action action : appAction.getActionsList()) {
        allFulfillmentSize += action.getFulfillmentOptionCount();
        for (AppActionProtos.FulfillmentOption fulfillmentOption : action.getFulfillmentOptionList()) {
          if(fulfillmentOption.getFulfillmentMode() == AppActionProtos.FulfillmentOption.FulfillmentMode.SLICE) {
            slice++;
          }
          AppActionProtos.UrlTemplate urlTemplate = fulfillmentOption.getUrlTemplate();
          if (urlTemplate.getParameterMapCount() > 0) {
            if (urlTemplate.getParameterMapCount() > 1) {
              if (urlTemplate.getTemplate().contains("lat")) {
                isCoordinates++;
              }
              multiParameter++;
            } else {
              singleParameter++;
            }
          } else {
            zeroParameter++;
          }
        }
      }
    }
    Log.d(TAG,  "allsize = " + allFulfillmentSize);
    Log.d(TAG, "multiParameter = " + multiParameter + "...singleParameter = " + singleParameter + "...zeroParameter =" + zeroParameter);
    Log.d(TAG, "isCoordinates = " + isCoordinates);
    Log.d(TAG, "slice = " + slice);
  }

  @Override
  protected void initView() {
    super.initView();
    Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.dashboard));
    TextView apps = findViewById(R.id.apps);
    TextView deeplinks = findViewById(R.id.deeplinks);
    String text1 =
        "Common:  " +
        "18\n" +
        "Finance:  " +
        "7\n" +
        "Food And Drink:  "  +
        "8\n" +
        "Health and fitness:  " +
        "15\n" +
        "Transportation:  " +
        "20\n" +
        "All: " +
        "56\n";
    apps.setText(text1);
    String text2 =
        "None:  74\n" + "Single:  39\n" + "Multiple:  45\n" + "Coordinates:  22\n"+ "All:  157\n" ;
    deeplinks.setText(text2);

  }
}