package com.example.appactionvisualizer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.Action;
import com.example.appactionvisualizer.databean.AppAction;
import com.example.appactionvisualizer.ui.activity.FulfillmentActivity;
import com.example.appactionvisualizer.utils.Utils;

/**
 * Adapter of ActionActivity Recyclerview
 */
public class ActionRecyclerViewAdapter extends RecyclerView.Adapter<ActionRecyclerViewAdapter.ViewHolder> {

  private AppAction appAction;
  private Context context;

  public ActionRecyclerViewAdapter(AppAction items, Context context) {
    this.context = context;
    appAction = items;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.action_rv_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    final Action action = appAction.getActions().get(position);
    holder.tvActionName.setText(action.getIntentName() + " (" + action.getFulfillmentArrayList().size() + ")" );
    holder.llAction.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(context, FulfillmentActivity.class);
        intent.putExtra(Constant.ACTION, action);
        intent.putExtra(Constant.APP_NAME, appAction.getAppName());
        context.startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return appAction.getActions().size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final LinearLayout llAction;
    public final TextView tvActionName;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      llAction =  view.findViewById(R.id.ll_action);
      tvActionName = view.findViewById(R.id.tv_action_name);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + tvActionName.getText() + "'";
    }

  }
}