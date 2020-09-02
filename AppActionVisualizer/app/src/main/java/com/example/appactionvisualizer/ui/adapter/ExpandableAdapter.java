package com.example.appactionvisualizer.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppFulfillment;
import com.example.appactionvisualizer.utils.AppUtils;

import java.util.List;
import java.util.Map;

public class ExpandableAdapter extends BaseExpandableListAdapter {
  private Context context;
  // Key is the action name, value is a list of fulfillment options.
  private Map<String, List<AppFulfillment>> intentMap;
  // The title of each single group is the action name.
  private List<String> actionNames;

  public ExpandableAdapter(
      Context context, Map<String, List<AppFulfillment>> intentMap, List<String> actionNames) {
    this.context = context;
    this.intentMap = intentMap;
    this.actionNames = actionNames;
  }

  @Override
  public int getGroupCount() {
    return actionNames.size();
  }

  /** @param groupPosition the position of the unexpanded groups */
  @Override
  public int getChildrenCount(int groupPosition) {
    // Get() method wouldn't return null since actionNames are the key set of the map.
    return intentMap.get(actionNames.get(groupPosition)).size();
  }

  @Override
  public String getGroup(int groupPosition) {
    return actionNames.get(groupPosition);
  }

  @Override
  public AppFulfillment getChild(int groupPosition, int childPosition) {
    return intentMap.get(actionNames.get(groupPosition)).get(childPosition);
  }

  @Override
  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
    String title = getGroup(groupPosition);
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.title_item, parent, false);
    }
    TextView name = view.findViewById(R.id.text_content);
    name.setText(title);
    name.setTypeface(null, Typeface.BOLD);
    return view;
  }

  @Override
  public View getChildView(
      int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
    final AppFulfillment data = getChild(groupPosition, childPosition);
    final String url = data.fulfillmentOption.getUrlTemplate().getTemplate();
    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.deep_link_item, parent, false);
    }
    TextView contents = view.findViewById(R.id.text_content);
    contents.setText(url);
    // Red deep links need parameter(s), green ones don't.
    if (url.contains(Constant.URL_PARAMETER_INDICATOR)) {
      contents.setTextColor(context.getResources().getColor(R.color.design_default_color_error));
    } else {
      contents.setTextColor(context.getResources().getColor(R.color.colorAccent));
    }
    ((ImageView) view.findViewById(R.id.app_icon))
        .setImageDrawable(AppUtils.getIconByPackageName(context, data.appAction.getPackageName()));
    return view;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }
}
