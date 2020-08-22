package com.example.appactionvisualizer.ui.activity.dashboard;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.databean.AppFulfillment;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

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
    Map<String, List<AppFulfillment>> intentMap = activity.getIntentMap();
    assertNotNull(intentMap);
    assertNotEquals(0, intentMap.size());
  }

  /** Check if each group is correctly initialized. */
  @Test
  public void testGroupSize() {
    setData();
    for (Map.Entry<String, List<AppFulfillment>> tupleList : activity.getIntentMap().entrySet()) {
      assertNotNull(tupleList);
      // Each action group should have some data, can not be empty.
      int size = tupleList.getValue().size();
      assertNotEquals(0, size);
    }
  }


  @Test
  public void testCheckApp() {
    setData();
    testAppName("youtube history", "youtube", 0);
    testAppName("open facebook", "facebook", 1);
    testAppName("youtub history", "youtube", 0);
    testAppName("open facbook", "facebook", 1);
  }

  private void testAppName(String sentence, String appName, Integer appIdx) {
    String[] words = sentence.split(Constant.WHITESPACE);
    Pair<Integer, String> pair = activity.checkApp(words);
    assertEquals(pair.first, appIdx);
    assertEquals(pair.second, appName);
  }

  @Test
  public void testGetDisplayMap() {
    setData();
    testGetDisplayMap("dunkin", 4);
    testGetDisplayMap("dunkin latte", 1);
    testGetDisplayMap("fit running", 1);
  }

  public void testGetDisplayMap(String sentence, int actionSize) {
    Map<String, List<AppFulfillment>> recommend = activity.getDisplayMap(sentence);
    assertEquals(recommend.size(), actionSize);
  }

  // Set some test data for the expandable list view.
  private void setData() {
    AppActionsGenerator.getInstance().readFromFile(appContext);
    activity = rule.launchActivity(new Intent());
  }
}
