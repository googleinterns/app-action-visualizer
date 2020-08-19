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
  public void checkTemplate() {
    TextView urlTemplate = rule.getActivity().findViewById(R.id.url_template);
    assertNotNull(urlTemplate.getText());
  }

  @Test
  public void checkUrl() {
    TextView url = rule.getActivity().findViewById(R.id.url);
    assertNotNull(url.getText());
  }

  @Test
  public void checkLink() {
    TextView link = rule.getActivity().findViewById(R.id.link);
    assertNotNull(link.getText());
  }
}