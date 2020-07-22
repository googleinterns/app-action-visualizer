package com.example.appactionvisualizer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.ui.activity.parameter.ListItemActivity;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import java.util.List;

/**
 * Adapter of ListItemActivity Recyclerview
 */
public class SelectRecyclerViewAdapter extends RecyclerView.Adapter<SelectRecyclerViewAdapter.ViewHolder> {

  private ListValue mappingList;
  private Context context;
  private String key;

  public SelectRecyclerViewAdapter(ListValue mappingList, String key, Context context) {
    this.context = context;
    this.mappingList = mappingList;
    this.key = key;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.list_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    final Value entityValue = mappingList.getValues(position);
    holder.textContent.setText(entityValue.getStructValue().getFieldsOrThrow(Constant.ENTITY_FIELD_NAME).getStringValue());
    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ((ListItemActivity)(context)).finish(entityValue.getStructValue().getFieldsOrThrow(Constant.ENTITY_FIELD_IDENTIFIER).getStringValue());
      }
    });
  }

  @Override
  public int getItemCount() {
    return mappingList.getValuesCount();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView textContent;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      textContent = view.findViewById(R.id.text_content);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + textContent.getText() + "'";
    }
  }
}