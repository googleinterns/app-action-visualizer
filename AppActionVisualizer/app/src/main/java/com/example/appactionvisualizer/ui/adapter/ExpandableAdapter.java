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
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.databean.Tuple;
import com.example.appactionvisualizer.utils.Utils;

import java.util.List;
import java.util.Map;

public class ExpandableAdapter extends BaseExpandableListAdapter {
  private Context context;
  private Map<String, List<Tuple<AppAction, Action, FulfillmentOption>>> intentMap;
  // The title of each single group is the action name.
  private List<String> actionNames;

  public ExpandableAdapter(
      Context context,
      Map<String, List<Tuple<AppAction, Action, FulfillmentOption>>> intentMap,
      List<String> actionNames) {
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
  public Tuple<AppAction, Action, FulfillmentOption> getChild(
      int groupPosition, int childPosition) {
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
    final Tuple<AppAction, Action, FulfillmentOption> data = getChild(groupPosition, childPosition);
    final String url = data.right.getUrlTemplate().getTemplate();
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
        .setImageDrawable(Utils.getIconByPackageName(context, data.left.getPackageName()));
    return view;
  }

  @Override
  public boolean isChildSelectable(int i, int i1) {
    return true;
  }
}
