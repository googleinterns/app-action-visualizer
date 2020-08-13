package com.example.appactionvisualizer.ui.activity.dashboard;

import android.content.Context;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.appactionvisualizer.R;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

// Test DashboardActivity
public class DashboardActivityTest {
  @Rule
  public ActivityTestRule<DashboardActivity> rule =
      new ActivityTestRule<>(DashboardActivity.class, false, true);
  // Context of the app under test.
  private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

  /** check if views are correctly initialized */
  @Test
  public void initViewTest() {
    DashboardActivity activity = rule.getActivity();
    assertNotNull(((TextView) activity.findViewById(R.id.apps)).getText());
    assertNotNull(((TextView) activity.findViewById(R.id.fulfillment_option)).getText());
  }
}
