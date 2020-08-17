package com.example.appactionvisualizer.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.ui.activity.dashboard.DeepLinkListActivity;

import static com.example.appactionvisualizer.databean.AppActionsGenerator.type2appActionList;

public class DashboardFragment extends Fragment {

  private static final String TAG = "DashboardFragment";
  private View view;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_dashboard, container, false);
    initView();
    return view;
  }

  protected void initView() {
    countApps();
    countFulfillmentOptions();
    setDetail();
  }

  /** Count the number of apps of each category */
  private void countApps() {
    // Text to be displayed
    StringBuilder appText = new StringBuilder();
    // Category1: number of apps
    // Category2: number of apps, etc.
    int allSize = 0;
    // Do not use range-based for loop since hash map is unordered
    for (int pos = 0; pos < 5; ++pos) {
      ActionType actionType = ActionType.getActionTypeValue(pos);
      String name = actionType.getName();
      int count = type2appActionList.get(actionType).size();
      allSize += count;
      appText.append(getString(R.string.kv_string_int, name, count));
    }
    // The string won't be long so linear insert is acceptable
    appText.insert(0, getString(R.string.all_parameter, allSize));
    ((TextView) view.findViewById(R.id.apps)).setText(appText);
  }

  /** Count the parameter number of each fulfillment option */
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
    ((TextView) view.findViewById(R.id.fulfillment_option)).setText(text);
  }

  // Set the jump logic of detail button
  protected void setDetail() {
    view.findViewById(R.id.detail)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DeepLinkListActivity.class);
                startActivity(intent);
              }
            });
  }
}
