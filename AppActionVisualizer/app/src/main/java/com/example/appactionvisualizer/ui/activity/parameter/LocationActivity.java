package com.example.appactionvisualizer.ui.activity.parameter;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.ui.activity.CustomActivity;
import com.example.appactionvisualizer.ui.adapter.AddressListRecyclerViewAdapter;
import com.example.appactionvisualizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.example.appactionvisualizer.constants.Constant.ERROR_NO_PLACE;

public class LocationActivity extends CustomActivity {
  private static final String TAG = "SelectLocation";
  private final static int SELECT_PICK_UP = 0, SELECT_DROP_OFF = 1, UPDATE = 2, ERROR = 3;
  Handler mHandlerThread;
  private RecyclerView addressListView;
  private TextInputEditText pickUpInput, dropOffInput;
  private List<Address> addressList = new ArrayList<>();
  private AddressListRecyclerViewAdapter adapter = new AddressListRecyclerViewAdapter(addressList, LocationActivity.this);
  private int inputSelect = 0;
  private String input;
  View.OnClickListener clickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      if (view.getId() == R.id.search_pick_up) {
        inputSelect = SELECT_PICK_UP;
        if (Objects.requireNonNull(pickUpInput.getText()).toString().isEmpty()) {
          errorHint();
        } else {
          input = pickUpInput.getText().toString();
          getAddressList(input);
        }
      } else {
        inputSelect = SELECT_DROP_OFF;
        if (Objects.requireNonNull(dropOffInput.getText()).toString().isEmpty()) {
          errorHint();
        } else {
          input = dropOffInput.getText().toString();
          getAddressList(input);
        }
      }
    }
  };
  private Address pickUpAddress = null, dropOffAddress = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_select_location);
    initData();
    initView();
  }

  @Override
  protected void initData() {

  }

  @Override
  protected void initView() {
    super.initView();
    getSupportActionBar().setTitle(TAG);
    addressListView = findViewById(R.id.address_list);
    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(addressListView.getContext(),
        new LinearLayoutManager(LocationActivity.this).getOrientation());
    addressListView.addItemDecoration(dividerItemDecoration);

    addressListView.setAdapter(adapter);
    pickUpInput = findViewById(R.id.pick_up_text);
    dropOffInput = findViewById(R.id.drop_off_text);
    Button searchPickUp = findViewById(R.id.search_pick_up);
    Button searchDropOff = findViewById(R.id.search_drop_off);
    searchPickUp.setOnClickListener(clickListener);
    searchDropOff.setOnClickListener(clickListener);
    mHandlerThread = new Handler(Looper.getMainLooper()) {
      @Override
      public void handleMessage(@NonNull Message msg) {
        if (msg.what == UPDATE) {
          adapter.notifyDataSetChanged();
        } else if (msg.what == ERROR) {
          errorHint();
        }
      }
    };
  }

  private void errorHint() {
    Utils.showMsg(ERROR_NO_PLACE, LocationActivity.this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.location_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.save) {
      checkInput();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void checkInput() {
    if (pickUpAddress == null || dropOffAddress == null) {
      errorHint();
      return;
    }
    Intent intent = new Intent();
    intent.putExtra(Constant.PICK_UP_LATITUDE, Double.toString(pickUpAddress.getLatitude()));
    intent.putExtra(Constant.PICK_UP_LONGITUDE, Double.toString(pickUpAddress.getLongitude()));
    intent.putExtra(Constant.DROP_OFF_LATITUDE, Double.toString(dropOffAddress.getLatitude()));
    intent.putExtra(Constant.DROP_OFF_LONGITUDE, Double.toString(dropOffAddress.getLongitude()));
    setResult(Activity.RESULT_OK, intent);
    finish();
  }

  public void setAddress(Address address) {
    if (inputSelect == SELECT_PICK_UP) {
      pickUpAddress = address;
      if (!pickUpAddress.getAddressLine(0).isEmpty()) {
        pickUpInput.setText(pickUpAddress.getAddressLine(0));
      }
    } else {
      dropOffAddress = address;
      if (!dropOffAddress.getAddressLine(0).isEmpty()) {
        dropOffInput.setText(dropOffAddress.getAddressLine(0));
      }
    }
    addressListView.setVisibility(View.INVISIBLE);
  }


  private void getAddressList(final String address) {
    addressListView.setVisibility(View.VISIBLE);
    final Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.US);
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          addressList.clear();
          addressList.addAll(geocoder.getFromLocationName(address, Constant.MAX_RESULTS));
          mHandlerThread.sendEmptyMessage(UPDATE);
        } catch (Exception e) {
          mHandlerThread.sendEmptyMessage(ERROR);
          e.printStackTrace();
        }
      }
    }).start();
  }

  public String getInput() {
    return input;
  }


}