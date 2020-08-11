package com.example.appactionvisualizer.ui.activity.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos;
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.databean.Tuple;
import com.example.appactionvisualizer.ui.activity.CustomActivity;
import com.example.appactionvisualizer.ui.adapter.ExpandableAdapter;
import com.example.appactionvisualizer.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// Display deep links using an expandable list view
public class DeepLinkListActivity extends CustomActivity {
  // For each action name, we need a tuple of <AppAction, Action, FulfillmentOption> data
  // so that the link can jump into ParameterActivity and parse required data to activity.
  Map<String, List<Tuple<AppAction, Action, FulfillmentOption>>> intentMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_deep_link_list);
    initData();
    initView();
  }

  @Override
  protected void initData() {
    // Use tree map so that the actions are sorted.
    // The total actions wouldn't be much so it wouldn't lose much time compared to hash map.
    // Make sure actions with same type stay together.
    Comparator<String> comparator =
        new Comparator<String>() {
          @Override
          public int compare(String s1, String s2) {
            ActionType type1 = ActionType.getActionTypeByName(s1);
            ActionType type2 = ActionType.getActionTypeByName(s2);
            if (!type1.equals(type2)) {
              return type1.compareTo(type2);
            }
            return s1.compareTo(s2);
          }
        };
    intentMap = new TreeMap<>(comparator);
    extractActions();
  }

  @Override
  protected void initView() {
    super.initView();
    getSupportActionBar().setTitle(getString(R.string.fulfillment_option));
    displayDeepLinks();
  }

  /**
   * Generate a <key: action, value: Tuple> tree map from current data, need tree map since we
   * want sorted actions.
   */
  private void extractActions() {
    // Iterate over the whole list to get the numbers, and add fulfillment options to their
    // corresponding intent name.
    for (AppActionProtos.AppAction appAction : AppActionsGenerator.appActions) {
      for (AppActionProtos.Action action : appAction.getActionsList()) {
        String intentName = action.getIntentName();
        for (AppActionProtos.FulfillmentOption fulfillmentOption :
            action.getFulfillmentOptionList()) {
          // The Slice options could not be counted as deep links.
          if (fulfillmentOption.getFulfillmentMode()
              == AppActionProtos.FulfillmentOption.FulfillmentMode.SLICE) {
            continue;
          }
          if (intentMap.get(intentName) == null) {
            intentMap.put(intentName, new ArrayList<Tuple<AppAction, Action, FulfillmentOption>>());
          }
          intentMap.get(intentName).add(new Tuple<>(appAction, action, fulfillmentOption));
        }
      }
    }
  }

  /** Parse data and pass it to the expandable list view. */
  private void displayDeepLinks() {
    ExpandableListView expandableListView = findViewById(R.id.expandable_list);
    // Display action names as group titles.
    final List<String> actionNames = new ArrayList<>(intentMap.keySet());
    ExpandableListAdapter adapter = new ExpandableAdapter(this, intentMap, actionNames);
    expandableListView.setAdapter(adapter);
    expandableListView.setOnChildClickListener(
        new ExpandableListView.OnChildClickListener() {
          @Override
          public boolean onChildClick(
              ExpandableListView expandableListView,
              View view,
              int groupPosition,
              int childPosition,
              long id) {
            // The jump logic is the same as the click of deep links at ActionsActivity page:
            // If the deep link needs parameters, jump into ParameterActivity.
            // Otherwise, jump to corresponding apps
            Tuple<AppAction, Action, FulfillmentOption> listData =
                intentMap.get(actionNames.get(groupPosition)).get(childPosition);
            Utils.jumpByType(
                DeepLinkListActivity.this, listData.left, listData.mid, listData.right);
            return false;
          }
        });
  }
}
