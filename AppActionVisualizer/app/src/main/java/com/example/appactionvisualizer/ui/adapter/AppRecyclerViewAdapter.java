package com.example.appactionvisualizer.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppAction;
import com.example.appactionvisualizer.ui.activity.ActionActivity;
import com.example.appactionvisualizer.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Adapter of AppFragment Recyclerview
 */
public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.ViewHolder> {

  private ArrayList<AppAction> appActionArrayList;
  private Context context;

  public AppRecyclerViewAdapter(ActionType type, Context context) {
    this.context = context;
    if(type != null)
      appActionArrayList = AppAction.appActionList.get(type);
    else
      appActionArrayList = AppAction.allAppActions;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.apptype_rv_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    final AppAction appAction = appActionArrayList.get(position);
    Drawable icon;
    try {
      icon = context.getPackageManager().getApplicationIcon(appAction.getAppPackage());
      holder.ivAppIcon.setImageDrawable(icon);
    } catch (PackageManager.NameNotFoundException e) {
      Utils.showMsg("icon not found", context);
      e.printStackTrace();
    }
    holder.tvAppName.setText(appAction.getAppName());
    //Use hash set to avoid duplicate tags
    HashSet<ActionType> uniqueSet = new HashSet<>(5);
    for (int i = 0; i < appAction.getActions().size(); i++) {
      uniqueSet.add(appAction.getActions().get(i).getActionType());
    }
    int idx = 0;
    for (ActionType actionType: uniqueSet) {
      holder.tvAppTags[idx].setVisibility(View.VISIBLE);
      holder.tvAppTags[idx++].setText(actionType.toString());
    }
    holder.rlApp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(Constant.APP_NAME, appAction);
        context.startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return appActionArrayList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final RelativeLayout rlApp;
    public final ImageView ivAppIcon;
    public final TextView tvAppName;
    public final TextView tvAppTags[] = new TextView[5];

    public ViewHolder(View view) {
      super(view);
      mView = view;
      rlApp =  view.findViewById(R.id.rl_app);
      ivAppIcon =  view.findViewById(R.id.iv_app);
      tvAppName = view.findViewById(R.id.tv_app_name);
      int[] ids= {R.id.tv_tag_1, R.id.tv_tag_2, R.id.tv_tag_3, R.id.tv_tag_4, R.id.tv_tag_5};
      for (int i = 0; i < ids.length; i++) {
        tvAppTags[i] = view.findViewById(ids[i]);
      }
    }

    @Override
    public String toString() {
      return super.toString() + " '" + tvAppName.getText() + "'";
    }

  }
}