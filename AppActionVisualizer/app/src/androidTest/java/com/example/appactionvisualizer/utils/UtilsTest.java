package com.example.appactionvisualizer.utils;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.appactionvisualizer.R;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UtilsTest {
  // Context of the app under test.
  private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

  @Test
  public void getResIdTest() {
    assertNotEquals(Utils.getResId("com_taxis99", R.drawable.class), -1);
  }

  @Test
  public void getResIdByPackageNameTest() {
    assertNotEquals(Utils.getResIdByPackageName("com.nike.plusgps", R.drawable.class), -1);
  }

  @Test
  public void getAppNameByPackageNameTest() {
    assertEquals(Utils.getAppNameByPackageName(appContext, "com.ubercab.eats"), "Uber Eats");
  }
}