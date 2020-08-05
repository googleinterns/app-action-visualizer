package com.example.appactionvisualizer.ui.activity;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppActionProtos;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.ui.adapter.ActionRecyclerViewAdapter;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ActionActivityTest {

  @Rule
  public ActivityTestRule<ActionActivity> rule = new ActivityTestRule<>(ActionActivity.class, false, false);
  // Context of the app under test.
  private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
  private ActionActivity activity;

  @Test
  public void initDataTest() {
    setData();
    assertNotNull(activity.appAction);
  }

  /**
   * list items count should be equal to the sum of all actions and fulfillment options
   */
  @Test
  public void listViewIsCorrect() {
    setData();
    RecyclerView recyclerView = activity.findViewById(R.id.list);
    assertNotNull(recyclerView);
    ActionRecyclerViewAdapter adapter = (ActionRecyclerViewAdapter) recyclerView.getAdapter();
    assertNotNull(adapter);
    AppAction appAction = activity.appAction;
    assertEquals(adapter.getItemCount(), getCount(appAction));
  }

  //the number of all actions and fulfillment options
  private int getCount(AppAction appAction) {
    int cnt = 0;
    for(AppActionProtos.Action action : appAction.getActionsList()) {
      cnt += action.getFulfillmentOptionCount() + 1;
    }
    return cnt;
  }

  //set some test data for the recyclerview
  private void setData() {
    AppActionsGenerator.getInstance().readFromFile(appContext);
    Intent intent = new Intent();
    intent.putExtra(Constant.APP_NAME, AppActionsGenerator.appActions.get(0));
    activity = rule.launchActivity(intent);
  }
}