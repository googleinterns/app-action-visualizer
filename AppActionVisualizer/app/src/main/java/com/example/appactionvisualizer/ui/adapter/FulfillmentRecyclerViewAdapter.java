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
import com.example.appactionvisualizer.databean.Action;
import com.example.appactionvisualizer.utils.Utils;

/**
 * Adapter of ActionActivity Recyclerview
 */
public class FulfillmentRecyclerViewAdapter extends RecyclerView.Adapter<FulfillmentRecyclerViewAdapter.ViewHolder> {

  private Action action;
  private Context context;

  public FulfillmentRecyclerViewAdapter(Action items, Context context) {
    this.context = context;
    action = items;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.fulfillment_rv_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    final Action.Fulfillment fulfillment = action.getFulfillmentArrayList().get(position);
    holder.tvFulfillName.setText(fulfillment.getUrlTemplate());
    holder.llFulfillment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(fulfillment.getParameterMapping() == null) {
          //if no paramater-mapping, fulfill the user intent
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fulfillment.getUrlTemplate()));
          context.startActivity(intent);
        }else {
          //todo: if there's any parameter-mapping, select the parameters

        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return action.getFulfillmentArrayList().size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final LinearLayout llFulfillment;
    public final TextView tvFulfillName;

    public ViewHolder(View view) {
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