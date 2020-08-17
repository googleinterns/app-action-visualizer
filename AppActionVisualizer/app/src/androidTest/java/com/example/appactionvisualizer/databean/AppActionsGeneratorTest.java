package com.example.appactionvisualizer.databean;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.appactionvisualizer.utils.Utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AppActionsGeneratorTest {
  // Context of the app under test.
  private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

  /**
   * Test read from file logic
   */
  @Test
  public void readFromFileTest() {
    AppActionsGenerator.getInstance().readFromFile(appContext);
    assertNotNull(AppActionsGenerator.appActions);
  }

  /**
   * Test if there are any two apps with same app names
   */
  @Test
  public void deduplicationTest() {
    List<String> list = new ArrayList<>();
    for(AppActionProtos.AppAction appAction : AppActionsGenerator.appActions) {
      list.add(appAction.getPackageName());
    }
    Set<String> set = new HashSet<>(list);
    assertEquals(set.size(), list.size());
  }

  /**
   * Test if apps are sorted by name
   */
  @Test
  public void sortAppActionByNameTest() {
    List<AppActionProtos.AppAction> sortedActions = new ArrayList<>(AppActionsGenerator.appActions);
    Collections.sort(sortedActions, new Comparator<AppActionProtos.AppAction>() {
      @Override
      public int compare(AppActionProtos.AppAction appAction1, AppActionProtos.AppAction appAction2) {
        return Utils.getAppNameByPackageName(appContext, appAction1.getPackageName()).toLowerCase().compareTo
            (Utils.getAppNameByPackageName(appContext, appAction2.getPackageName()).toLowerCase());
      }
    });
    assertEquals(AppActionsGenerator.appActions, sortedActions);
  }
}