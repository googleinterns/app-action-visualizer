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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppAction;
import com.example.appactionvisualizer.ui.activity.ActionActivity;
import com.example.appactionvisualizer.utils.Utils;

import java.util.ArrayList;

/**
 * Adapter of AppFragment Recyclerview
 */
public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.ViewHolder> {

  private ArrayList<AppAction> appActionArrayList;
  private Context context;

  public AppRecyclerViewAdapter(ActionType type, Context context) {
    this.context = context;
    appActionArrayList = AppAction.appActionList.get(type);
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
    holder.llApp.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(context, ActionActivity.class);
        intent.putExtra(Constant.ACTIONTYPE, appAction);
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
    public final LinearLayout llApp;
    public final ImageView ivAppIcon;
    public final TextView tvAppName;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      llApp =  view.findViewById(R.id.ll_app);
      ivAppIcon =  view.findViewById(R.id.iv_app);
      tvAppName = view.findViewById(R.id.tv_app_name);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + tvAppName.getText() + "'";
    }

  }
}