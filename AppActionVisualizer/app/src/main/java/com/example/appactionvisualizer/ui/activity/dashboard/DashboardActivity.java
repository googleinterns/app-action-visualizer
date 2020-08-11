package com.example.appactionvisualizer.ui.activity.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.ui.activity.CustomActivity;

import java.util.List;
import java.util.Map;

import static com.example.appactionvisualizer.databean.AppActionsGenerator.type2appActionList;

public class DashboardActivity extends CustomActivity {

  private static final String TAG = "DashboardActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);
    initData();
    initView();
  }

  @Override
  protected void initData() {
    countApps();
    countFulfillmentOptions();
  }

  /** Count the number of apps of each category */
  private void countApps() {
    // Text to be displayed
    StringBuilder appText = new StringBuilder();
    // Category1: number of apps
    // Category2: number of apps, etc.
    for (Map.Entry<ActionType, List<AppActionProtos.AppAction>> entry :
        type2appActionList.entrySet()) {
      appText.append(
          getString(
              R.string.key_value,
              entry.getKey().getName(),
              Integer.toString(entry.getValue().size())));
    }
    ((TextView) findViewById(R.id.apps)).setText(appText);
  }

  /**
   * Count the parameter number of each fulfillment option
   */
  private void countFulfillmentOptions() {
    // Use these variables to do a statistics of deep links
    int allFulfillmentSize = 0,
        noneParameter = 0,
        singleParameter = 0,
        multiParameter = 0,
        slice = 0;
    // Iterate over the whole list to get the numbers
    for (AppActionProtos.AppAction appAction : AppActionsGenerator.appActions) {
      for (AppActionProtos.Action action : appAction.getActionsList()) {
        allFulfillmentSize += action.getFulfillmentOptionCount();
        for (AppActionProtos.FulfillmentOption fulfillmentOption :
            action.getFulfillmentOptionList()) {
          // The Slice options could not be counted as deep links
          if (fulfillmentOption.getFulfillmentMode()
              == AppActionProtos.FulfillmentOption.FulfillmentMode.SLICE) {
            slice++;
            continue;
          }
          AppActionProtos.UrlTemplate urlTemplate = fulfillmentOption.getUrlTemplate();
          int cnt = urlTemplate.getParameterMapCount();
          // Count the number of parameters of each fulfillment
          if (cnt == 0) {
            noneParameter++;
          } else if (cnt > 1) {
            multiParameter++;
          } else {
            singleParameter++;
          }
        }
      }
    }
    StringBuilder text = new StringBuilder();
    // Build the string as below:
    // All: number of apps
    // None parameter: number
    // Single parameter: number
    // Multiple parameter: number
    // Slice parameter: number
    text.append(getString(R.string.all_parameter, allFulfillmentSize))
        .append(getString(R.string.no_parameter, noneParameter))
        .append(getString(R.string.single_parameter, singleParameter))
        .append(getString(R.string.multiple_parameter, multiParameter))
        .append(getString(R.string.slice, slice));
    ((TextView) findViewById(R.id.fulfillment_option)).setText(text);
  }

  @Override
  protected void initView() {
    super.initView();
    getSupportActionBar().setTitle(getString(R.string.dashboard));
    findViewById(R.id.detail)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, DeepLinkListActivity.class);
                startActivity(intent);
              }
            });
  }
}
