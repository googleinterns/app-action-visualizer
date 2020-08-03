package com.example.appactionvisualizer.ui.adapter;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.ui.activity.parameter.LocationActivity;

import java.util.List;

/**
 * Adapter of LocationActivity Recyclerview
 */
public class AddressListRecyclerViewAdapter extends RecyclerView.Adapter<AddressListRecyclerViewAdapter.ViewHolder> {

  private List<Address> addressList;
  private Context context;

  public AddressListRecyclerViewAdapter(List<Address> addressList, Context context) {
    this.addressList = addressList;
    this.context = context;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.address_list_item, parent, false);
    return new ViewHolder(view);
  }

  /**
   * display information:
   * addressName
   * locationName
   * latitude & longitude
   * e.g.:
   * San Francisco International Airport
   * San Mateo Country, California
   * 37.621,-122.38
   */
  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    final Address address = addressList.get(position);
    if (address.getFeatureName().isEmpty()) {
      holder.addressName.setText(((LocationActivity) context).getInput());
    } else {
      holder.addressName.setText(address.getFeatureName());
    }
    String subAdminArea = address.getSubAdminArea();
    String adminArea = address.getAdminArea();
    holder.location.setText(context.getString(R.string.location, subAdminArea.isEmpty() ? "" : subAdminArea, adminArea.isEmpty() ? "" : adminArea));
    holder.pickUp.setText(context.getString(R.string.coordinates_pick_up, String.format("%.5g", address.getLatitude()), String.format("%.5g", address.getLongitude())));
    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ((LocationActivity) context).setAddress(address);
      }
    });
  }

  @Override
  public int getItemCount() {
    return addressList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView addressName;
    public final TextView location;
    public final TextView pickUp;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      addressName = view.findViewById(R.id.name);
      location = view.findViewById(R.id.location);
      pickUp = view.findViewById(R.id.coordinates_pick_up);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + addressName.getText() + "'";
    }
  }
}