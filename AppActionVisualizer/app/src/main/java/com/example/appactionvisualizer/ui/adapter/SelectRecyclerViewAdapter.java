package com.example.appactionvisualizer.ui.adapter;

import android.app.Activity;
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
import com.example.appactionvisualizer.databean.Fulfillment;
import com.example.appactionvisualizer.databean.ParameterMapping;
import com.example.appactionvisualizer.ui.activity.ParameterActivity;
import com.example.appactionvisualizer.ui.activity.SelectActivity;

import java.util.List;

/**
 * Adapter of SelectActivity Recyclerview
 */
public class SelectRecyclerViewAdapter extends RecyclerView.Adapter<SelectRecyclerViewAdapter.ViewHolder> {

  private List<ParameterMapping.Mapping> mappingList;
  private Context context;
  String key;

  public SelectRecyclerViewAdapter(List<ParameterMapping.Mapping> mappingList, String key, Context context) {
    this.context = context;
    this.mappingList = mappingList;
    this.key = key;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.rv_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    final ParameterMapping.Mapping mapping = mappingList.get(position);
    holder.tvName.setText(mapping.getName());
    holder.llItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ((SelectActivity)(context)).finish(mapping.getIdentifier());
      }
    });
  }

  @Override
  public int getItemCount() {
    return mappingList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final LinearLayout llItem;
    public final TextView tvName;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      llItem =  view.findViewById(R.id.ll_item);
      tvName = view.findViewById(R.id.tv_text);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + tvName.getText() + "'";
    }
  }
}