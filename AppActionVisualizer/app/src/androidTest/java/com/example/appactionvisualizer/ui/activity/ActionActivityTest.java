package com.example.appactionvisualizer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

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

import static org.junit.Assert.*;

public class ActionActivityTest {

  @Rule
  public ActivityTestRule<ActionActivity> rule = new ActivityTestRule<>(ActionActivity.class, false, false);
  private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
  private ActionActivity activity;

  @Test
  public void initData_isCorrect() {
    setData();
    assertNotNull(activity.appAction);
  }

  /**
   * list items count should be equal to the sum of all actions and fulfillment options
   */
  @Test
  public void list_isCorrect() {
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

  private void setData() {
    AppActionsGenerator.getInstance().readFromFile(appContext);
    Intent intent = new Intent();
    intent.putExtra(Constant.APP_NAME, AppActionsGenerator.appActions.get(0));
    activity = rule.launchActivity(intent);
  }
}