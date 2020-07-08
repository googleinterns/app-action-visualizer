package com.example.appactionvisualizer.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.appactionvisualizer.ui.activity.ParameterActivity;
import com.example.appactionvisualizer.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Adapter of ActionActivity Recyclerview
 * consists of two types of view type TYPE_ACTION/TYPE_FULFILLMENT with different view
 */
public class ActionRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private AppAction appAction;
  private Context context;
  private static final int TYPE_ACTION = 0, TYPE_FULFILLMENT = 1;
  private List<Integer> actionPos = new ArrayList<>();
  private int allSize = 0;

  public ActionRecyclerViewAdapter(AppAction items, Context context) {
    this.context = context;
    appAction = items;
    //find all the action item position
    for(Action action : appAction.getActions()) {
      actionPos.add(allSize);
      allSize += action.getFulfillmentArrayList().size() + 1;
    }
  }

  /**
   * @param position search the actionPos integer list to get the corresponding data idx of each item, logic same as @onBindViewHolder
   * @return type of the view: TYPE_ACTION, TYPE_FULFILLMENT
   */
  @Override
  public int getItemViewType(int position) {
    if(actionPos.contains(position))
      return TYPE_ACTION;
    return TYPE_FULFILLMENT;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if(viewType == TYPE_ACTION) {
      View view = LayoutInflater.from(context)
          .inflate(R.layout.action_rv_item, parent, false);
      return new ActionViewHolder(view);
    }
    View view = LayoutInflater.from(context)
        .inflate(R.layout.fulfillment_rv_item, parent, false);
    return new FulfillViewHolder(view);
  }

  /**
   * @param holder two types: TYPE_ACTION, TYPE_FULFILLMENT
   * @param position search the actionPos integer list to get the corresponding data idx of each item
   *                 eg: if three actions action0 (1 fulfillmentUrl: 0-0), action1(2 fulfillmentUrl: 1-0, 1-1), action2(2 fulfillmentUrl: 2-0, 2-1).
   *                 actionPos is [0, 2, 5]
   *                 position 0 - action0
   *                 position 1 - Fulfillment 0-0
   *                 position 2 - action1
   *                 position 3 - Fulfillment 1-0
   *                 position 4 - Fulfillment 1-1
   *                 position 5 - action2
   *                 position 6 - Fulfillment 2-0
   *                 position 7 - Fulfillment 2-1
   */
  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
    switch (holder.getItemViewType()) {
      case TYPE_ACTION:
        ActionViewHolder actionHolder = (ActionViewHolder) holder;
        //search the corresponding action item idx
        int idx = Arrays.binarySearch(actionPos.toArray(), position);
        final Action action = appAction.getActions().get(idx);
        actionHolder.tvActionName.setText(action.getIntentName() + " (" + action.getFulfillmentArrayList().size() + ")" );
        actionHolder.tvActionType.setText(action.getActionType().toString());
        //todo deprecate
//        actionHolder.llAction.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View view) {
//            Intent intent = new Intent(context, FulfillmentActivity.class);
//            intent.putExtra(Constant.ACTION, action);
//            intent.putExtra(Constant.APP_NAME, appAction.getAppName());
//            context.startActivity(intent);
//          }
//        });
        break;
      case TYPE_FULFILLMENT:
        FulfillViewHolder fulfillHolder = (FulfillViewHolder) holder;
        //search the corresponding action item idx and fulfill item idx
        int actionIdx =  (-Arrays.binarySearch(actionPos.toArray(), position)) - 2;
        int fulfillIdx =  position - actionPos.get(actionIdx) - 1;
        final Action.Fulfillment fulfillment = appAction.getActions().get(actionIdx).getFulfillmentArrayList().get(fulfillIdx);
        final String url = fulfillment.getUrlTemplate();
        fulfillHolder.tvFulfillName.setText(url);
        fulfillHolder.tvFulfillName.setTextColor(context.getResources().getColor(fulfillment.getParameterMapping() == null ? R.color.colorAccent: R.color.design_default_color_error));
        fulfillHolder.llFulfillment.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            //use "{" to judge whether the users has selected all the parameters
            Intent intent = new Intent();
            if(url.contains("{")) {
              intent = new Intent(context, ParameterActivity.class);
              intent.putExtra(Constant.FULFILLMENT, fulfillment);
            }else {
              intent.setAction(Intent.ACTION_VIEW);
              intent.setData(Uri.parse(url));
            }
            context.startActivity(intent);
          }
        });

    }

  }

  @Override
  public int getItemCount() {
    return allSize;
  }

  public class ActionViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final LinearLayout llAction;
    public final TextView tvActionName;
    public final TextView tvActionType;

    public ActionViewHolder(View view) {
      super(view);
      mView = view;
      llAction =  view.findViewById(R.id.ll_action);
      tvActionName = view.findViewById(R.id.tv_action_name);
      tvActionType = view.findViewById(R.id.tv_action_type);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + tvActionName.getText() + "'";
    }
  }

  public class FulfillViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final LinearLayout llFulfillment;
    public final TextView tvFulfillName;

    public FulfillViewHolder(View view) {
      super(view);
      mView = view;
      llFulfillment =  view.findViewById(R.id.ll_fulfillment);
      tvFulfillName = view.findViewById(R.id.tv_fulfill_name);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + tvFulfillName.getText() + "'";
    }
  }
}