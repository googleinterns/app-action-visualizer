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
import com.example.appactionvisualizer.databean.Fulfillment;
import com.example.appactionvisualizer.ui.activity.ParameterActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    //make sure actions with the same type stay together
    Collections.sort(appAction.getActions(), new Comparator<Action>() {
      @Override
      public int compare(Action t1, Action t2) {
        return t1.getIntentName().compareTo(t2.getIntentName());
      }
    });
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
    if(actionPos.contains(position)) {
      return TYPE_ACTION;
    }else {
      return TYPE_FULFILLMENT;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if(viewType == TYPE_ACTION) {
      View view = LayoutInflater.from(context)
          .inflate(R.layout.action_rv_item, parent, false);
      return new ActionViewHolder(view);
    }
    View view = LayoutInflater.from(context)
        .inflate(R.layout.list_item, parent, false);
    return new FulfillViewHolder(view);
  }

  /**
   * @param holder two types: TYPE_ACTION, TYPE_FULFILLMENT
   * @param position search the actionPos integer list to get the corresponding data index of each item
   *                 e.g., three actions action0 (1 fulfillmentUrl: 0-0), action1(2 fulfillmentUrl: 1-0, 1-1), action2(2 fulfillmentUrl: 2-0, 2-1).
   *                 actionPos is [0, 2, 5]
   *                 position 0 - action0
   *                 position 1 - fulfillment 0-0
   *                 position 2 - action1
   *                 position 3 - fulfillment 1-0
   *                 position 4 - fulfillment 1-1
   *                 position 5 - action2
   *                 position 6 - fulfillment 2-0
   *                 position 7 - fulfillment 2-1
   */
  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
    switch (holder.getItemViewType()) {
      case TYPE_ACTION:
        ActionViewHolder actionHolder = (ActionViewHolder) holder;
        //search the corresponding action item index
        int idx = Arrays.binarySearch(actionPos.toArray(), position);
        final Action action = appAction.getActions().get(idx);
        actionHolder.actionName.setText(action.getIntentName());
        actionHolder.actionType.setText(action.getActionType().toString());
        break;
      case TYPE_FULFILLMENT:
        FulfillViewHolder fulfillHolder = (FulfillViewHolder) holder;
        //search the corresponding action item index and fulfill item index
        int actionIdx =  (-Arrays.binarySearch(actionPos.toArray(), position)) - 2;
        int fulfillIdx =  position - actionPos.get(actionIdx) - 1;
        final Fulfillment fulfillment = appAction.getActions().get(actionIdx).getFulfillmentArrayList().get(fulfillIdx);
        final String url = fulfillment.getUrlTemplate();
        fulfillHolder.textContent.setText(url);
        fulfillHolder.textContent.setTextColor(context.getResources().getColor(fulfillment.getParameterMapping() == null ? R.color.colorAccent: R.color.design_default_color_error));
        fulfillHolder.item.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            //use "{" to judge whether the user has selected all the parameters
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

  private class ActionViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final LinearLayout actionIntent;
    public final TextView actionName;
    public final TextView actionType;

    public ActionViewHolder(View view) {
      super(view);
      mView = view;
      actionIntent =  view.findViewById(R.id.action_intent);
      actionName = view.findViewById(R.id.action_name);
      actionType = view.findViewById(R.id.action_type);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + actionName.getText() + "'";
    }
  }

  private class FulfillViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final LinearLayout item;
    public final TextView textContent;

    public FulfillViewHolder(View view) {
      super(view);
      mView = view;
      item =  view.findViewById(R.id.item);
      textContent = view.findViewById(R.id.text_content);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + textContent.getText() + "'";
    }
  }
}