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
import static org.junit.Assert.assertNull;

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
  public void testCheckApp1() {
    setData();
    testAppName("open facebook", "facebook", 1);
  }

  @Test
  public void testCheckApp2() {
    setData();
    testAppName("youtube history", "youtube", 0);
  }

  @Test
  public void testCheckApp3() {
    setData();
    testAppName("youtub history", "youtube", 0);
  }

  @Test
  public void testCheckApp4() {
    setData();
    testAppName("open facbook", "", -1);
  }

  @Test
  public void testCheckApp5() {
    setData();
    testAppName("open facebo", "facebook", 1);
  }

  private void testAppName(String sentence, String appName, Integer appIdx) {
    String[] words = sentence.split(Constant.WHITESPACE);
    Pair<Integer, String> pair = activity.checkApp(words);
    assertEquals(pair.first, appIdx);
    assertEquals(pair.second, appName);
  }

  @Test
  public void testGetDisplayMap1() {
    setData();
    testGetDisplayMap("dunkin", 4);
  }

  @Test
  public void testGetDisplayMap2() {
    setData();
    testGetDisplayMap("dunkin latte", 1);
  }

  @Test
  public void testGetDisplayMap3() {
    setData();
    testGetDisplayMap("fit running", 1);
  }

  @Test
  public void testGetDisplayMap4() {
    setData();
    testGetDisplayMap("latte", 1);
  }

  @Test
  public void testGetDisplayMap5() {
    setData();
    testGetDisplayMap("coupon", 1);
  }

  @Test
  public void testGetDisplayMap6() {
    setData();
    testGetDisplayMap("coup", 1);
  }

  @Test
  public void testGetDisplayMap7() {
    setData();
    testGetDisplayMap("birthday", 1);
  }

  @Test
  public void testGetDisplayMap8() {
    setData();
    testGetDisplayMap("later", 1);
  }

  @Test
  public void testGetDisplayMap9() {
    setData();
    testGetDisplayMap("tea", 1);
  }

  public void testGetDisplayMap(String sentence, int mapSize) {
    Map<String, List<AppFulfillment>> recommend = activity.getDisplayMap(sentence);
    if (mapSize == -1) {
      assertNull(recommend);
    } else {
      assertEquals(recommend.size(), mapSize);
    }
  }

  @Test
  public void testTaxiReservation() {
    setData();
    assertNotEquals(activity.getTaxiReservationMap(0,0,0,0).size(), 0);
  }

  // Set some test data for the expandable list view.
  private void setData() {
    AppActionsGenerator.getInstance().readFromFile(appContext);
    activity = rule.launchActivity(new Intent());
  }
}
