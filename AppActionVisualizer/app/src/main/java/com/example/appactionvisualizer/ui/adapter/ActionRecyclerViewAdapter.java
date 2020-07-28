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
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.ui.activity.ParameterActivity;
import com.example.appactionvisualizer.utils.Utils;

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

  private static final String TAG = "ActionRecycler";
  private static final int VIEW_TYPE_ACTION = 0, VIEW_TYPE_FULFILLMENT = 1;
  private AppAction appAction;
  private List<Action> actionList = new ArrayList<>();
  private Context context;
  private List<Integer> actionPos = new ArrayList<>();
  private int allSize = 0;

  public ActionRecyclerViewAdapter(AppAction items, Context context) {
    this.context = context;
    appAction = items;
    actionList.addAll(appAction.getActionsList());
    //make sure actions with the same type stay together
    Collections.sort(actionList, new Comparator<Action>() {
      @Override
      public int compare(Action t1, Action t2) {
        return ActionType.getActionTypeByName(t1.getIntentName()).compareTo(ActionType.getActionTypeByName(t2.getIntentName()));
      }
    });
    //find all the action item position
    for (Action action : actionList) {
      actionPos.add(allSize);
      allSize += action.getFulfillmentOptionCount() + 1;
    }
  }

  /**
   * @param position search the actionPos integer list to get the corresponding data idx of each item, logic same as @onBindViewHolder
   * @return type of the view: TYPE_ACTION, TYPE_FULFILLMENT
   */
  @Override
  public int getItemViewType(int position) {
    if (actionPos.contains(position)) {
      return VIEW_TYPE_ACTION;
    } else {
      return VIEW_TYPE_FULFILLMENT;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_ACTION) {
      View view = LayoutInflater.from(context)
          .inflate(R.layout.action_rv_item, parent, false);
      return new ActionViewHolder(view);
    }
    View view = LayoutInflater.from(context)
        .inflate(R.layout.list_item, parent, false);
    return new FulfillViewHolder(view);
  }

  /**
   * @param holder   two types: TYPE_ACTION, TYPE_FULFILLMENT
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
    final Action action;
    switch (holder.getItemViewType()) {
      case VIEW_TYPE_ACTION:
        ActionViewHolder actionHolder = (ActionViewHolder) holder;
        //search the corresponding action item index
        int idx = Arrays.binarySearch(actionPos.toArray(), position);
        action = actionList.get(idx);
        actionHolder.actionName.setText(action.getIntentName());
        actionHolder.actionType.setText(ActionType.getActionTypeByName(action.getIntentName()).getName());
        break;
      case VIEW_TYPE_FULFILLMENT:
        FulfillViewHolder fulfillHolder = (FulfillViewHolder) holder;
        //search the corresponding action item index and fulfill item index
        int actionIdx = (-Arrays.binarySearch(actionPos.toArray(), position)) - 2;
        int fulfillIdx = position - actionPos.get(actionIdx) - 1;
        action = actionList.get(actionIdx);
        final FulfillmentOption fulfillment = action.getFulfillmentOption(fulfillIdx);
        final String url = fulfillment.getUrlTemplate().getTemplate();
        fulfillHolder.textContent.setText(url);
        if(url.contains("{")) {
          fulfillHolder.textContent.setTextColor(context.getResources().getColor(R.color.design_default_color_error));
        }else {
          fulfillHolder.textContent.setTextColor(context.getResources().getColor(R.color.colorAccent));
          fulfillHolder.textContent.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_set_as, 0, 0, 0);
        }
        fulfillHolder.item.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            jump(action, fulfillment);
          }
        });
    }
  }

  void jump(final Action action, final FulfillmentOption fulfillmentOption) {
    if (fulfillmentOption.getUrlTemplate().getTemplate().contains("@url")) {
      Utils.showMsg(context.getString(R.string.error_parsing), context);
      return;
    }
    if (fulfillmentOption.getUrlTemplate().getParameterMapCount() > 0) {
      Intent intent = new Intent(context, ParameterActivity.class);
      intent.putExtra(Constant.FULFILLMENT_OPTION, fulfillmentOption);
      intent.putExtra(Constant.ACTION, action);
      intent.putExtra(Constant.APP_ACTION, appAction);
      context.startActivity(intent);
    } else {
      Utils.jumpToApp(context, fulfillmentOption.getUrlTemplate().getTemplate(), appAction.getPackageName());
    }
  }

  @Override
  public int getItemCount() {
    return allSize;
  }

  public static class ActionViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final LinearLayout actionIntent;
    public final TextView actionName;
    public final TextView actionType;

    public ActionViewHolder(View view) {
      super(view);
      mView = view;
      actionIntent = view.findViewById(R.id.action_intent);
      actionName = view.findViewById(R.id.action_name);
      actionType = view.findViewById(R.id.action_type);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + actionName.getText() + "'";
    }
  }

  public static class FulfillViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final LinearLayout item;
    public final TextView textContent;

    public FulfillViewHolder(View view) {
      super(view);
      mView = view;
      item = view.findViewById(R.id.item);
      textContent = view.findViewById(R.id.text_content);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + textContent.getText() + "'";
    }
  }
}