package com.example.appactionvisualizer.ui.activity;

import androidx.test.rule.ActivityTestRule;
import androidx.viewpager.widget.ViewPager;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.ui.adapter.SectionsPagerAdapter;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MainActivityTest {

  @Rule
  public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, false, true);
  /**
   * assert that view pager is correctly initialized
   */
  @Test
  public void viewPagerIsCorrect() {
    MainActivity activity = rule.getActivity();
    ViewPager viewPager = activity.findViewById(R.id.view_pager);
    assertNotNull(viewPager);
    SectionsPagerAdapter adapter = (SectionsPagerAdapter) viewPager.getAdapter();
    assertNotNull(adapter);
    //the viewpager adapter should have same size with tab titles
    assertEquals(adapter.getCount(), Constant.TAB_TITLES.length);
  }


}