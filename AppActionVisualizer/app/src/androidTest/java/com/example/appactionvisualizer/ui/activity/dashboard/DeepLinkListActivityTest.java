package com.example.appactionvisualizer.ui.activity.dashboard;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.databean.AppFulfillment;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class DeepLinkListActivityTest {
  private static final String TAG = DeepLinkListActivityTest.class.getSimpleName();

  @Rule
  public ActivityTestRule<DeepLinkListActivity> rule =
      new ActivityTestRule<>(DeepLinkListActivity.class, false, false);

  private DeepLinkListActivity activity;
  // Context of the app under test.
  private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();


  @Test
  public void testMapSize() {
    setData();
    Map<String, List<AppFulfillment>> intentMap =
        activity.getIntentMap();
    assertNotNull(intentMap);
    assertNotEquals(0, intentMap.size());
  }

  /** Check if each group is correctly initialized. */
  @Test
  public void testGroupSize() {
    setData();
    for (Map.Entry<String, List<AppFulfillment>> tupleList :
        activity.getIntentMap().entrySet()) {
      assertNotNull(tupleList);
      // Each action group should have some data, can not be empty.
      int size = tupleList.getValue().size();
      assertNotEquals(0, size);
    }
  }

  @Test
  public void testCheckApp_Facebook() {
    setData();
    String sentence = "open fabook";
    String[] words = sentence.split(" ");
    Pair<Integer, String> pair = activity.checkApp(words);
    int appIdx = pair.first;
    String appName = pair.second;
    assertEquals(appIdx, 1);
    assertEquals(appName, "facebook");
  }

  @Test
  public void testCheckApp_Youtube() {
    setData();
    String sentence = "youtub history";
    String[] words = sentence.split(" ");
    Pair<Integer, String> pair = activity.checkApp(words);
    int appIdx = pair.first;
    String appName = pair.second;
    assertEquals(appIdx, 0);
    assertEquals(appName, "youtube");
  }

  // Set some test data for the expandable list view.
  private void setData() {
    AppActionsGenerator.getInstance().readFromFile(appContext);
    activity = rule.launchActivity(new Intent());
  }


}
