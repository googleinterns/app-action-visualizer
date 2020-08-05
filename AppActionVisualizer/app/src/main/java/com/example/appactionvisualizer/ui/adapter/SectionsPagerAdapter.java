package com.example.appactionvisualizer.ui.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.appactionvisualizer.ui.fragment.AppFragment;

import static com.example.appactionvisualizer.constants.Constant.TAB_TITLES;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the tabs.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

  private final Context context;

  public SectionsPagerAdapter(Context context, FragmentManager fm) {
    super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    this.context = context;
  }

  @Override
  public Fragment getItem(int position) {
    return AppFragment.newInstance(position);
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return context.getResources().getString(TAB_TITLES[position]);
  }

  @Override
  public int getCount() {
    return TAB_TITLES.length;
  }
}