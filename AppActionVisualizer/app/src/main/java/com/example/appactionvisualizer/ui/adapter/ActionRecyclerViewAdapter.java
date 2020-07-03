package com.example.appactionvisualizer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.AppAction;
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
    holder.tvActionName.setText(appAction.getActions().get(position).getIntentName());
    holder.llAction.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Utils.showMsg("click " + position, context);
//                Intent intent = new Intent(context, ActionActivity.class);
//                intent.putExtra(Constant.ACTIONTYPE, appAction);
//                context.startActivity(intent);
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