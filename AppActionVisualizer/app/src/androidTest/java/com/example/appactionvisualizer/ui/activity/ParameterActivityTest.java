package com.example.appactionvisualizer.ui.activity;

import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import com.example.appactionvisualizer.R;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ParameterActivityTest {

  @Rule
  public ActivityTestRule<ParameterActivity> rule = new ActivityTestRule<>(ParameterActivity.class, false, true);

  /**
   * check if views are correctly initialized
   */
  @Test
  public void initViewTest() {
    ParameterActivity activity = rule.getActivity();
    TextView urlTemplate = activity.findViewById(R.id.url_template);
    assertNotNull(urlTemplate.getText());
    TextView url = activity.findViewById(R.id.url);
    assertNotNull(url.getText());
    TextView link = activity.findViewById(R.id.link);
    assertNotNull(link.getText());

  }
}