package com.example.appactionvisualizer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.TestGenerator;
import com.example.appactionvisualizer.ui.activity.ActionActivity;

import java.util.HashSet;
import java.util.List;

/**
 * Adapter of AppFragment Recyclerview
 */
public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.ViewHolder> {

  private static final String TAG = "AppRecyclerView" ;
  private List<AppAction> appActionArrayList;
  private Context context;

  public AppRecyclerViewAdapter(ActionType type, Context context) {
    this.context = context;
    if(type != null) {
      appActionArrayList = TestGenerator.type2appActionList.get(type);
    }
    else {
      appActionArrayList = TestGenerator.appActionsUnique;
    }
    Log.d(TAG,(type == null? "null" :type.getName()) + " "+ appActionArrayList.size());
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
    ApplicationInfo applicationInfo = null;
    PackageManager packageManager = context.getPackageManager();
    try {
      icon = packageManager.getApplicationIcon(appAction.getPackageName());
      holder.appIcon.setImageDrawable(icon);
      applicationInfo = packageManager.getApplicationInfo(appAction.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
//      Utils.showMsg("icon not found", context);
//      e.printStackTrace();
      Log.d(TAG, appAction.getPackageName() + " not install");
    }
    holder.appName.setText(applicationInfo != null? packageManager.getApplicationLabel(applicationInfo) : "unknown");
    //Use hash set to avoid duplicate tags
    final HashSet<ActionType> uniqueSet = new HashSet<>(5);
    for (int i = 0; i < appAction.getActionsCount(); i++) {
      //todo: add field in proto
      uniqueSet.add(ActionType.getActionTypeByName(appAction.getActions(i).getIntentName()));
    }
    int idx = 0;
    for (ActionType actionType: uniqueSet) {
      holder.appTags[idx].setVisibility(View.VISIBLE);
      holder.appTags[idx++].setText(actionType.getName());
    }
    for(; idx < 5; ++idx) {
      holder.appTags[idx].setVisibility(View.INVISIBLE);
    }
    holder.app.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(Constant.APP_ACTION, appAction);
        context.startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return appActionArrayList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final RelativeLayout app;
    public final ImageView appIcon;
    public final TextView appName;
    public final TextView appTags[] = new TextView[5];

    public ViewHolder(View view) {
      super(view);
      mView = view;
      app =  view.findViewById(R.id.app);
      appIcon =  view.findViewById(R.id.app_icon);
      appName = view.findViewById(R.id.app_name);
      int[] ids= {R.id.tag_1, R.id.tag_2, R.id.tag_3, R.id.tag_4, R.id.tag_5};
      for (int i = 0; i < ids.length; i++) {
        appTags[i] = view.findViewById(ids[i]);
      }
    }

    @Override
    public String toString() {
      return super.toString() + " '" + appName.getText() + "'";
    }

  }
}