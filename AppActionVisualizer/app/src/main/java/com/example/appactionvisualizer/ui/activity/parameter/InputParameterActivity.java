package com.example.appactionvisualizer.ui.activity.parameter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.ui.activity.CustomActivity;
import com.example.appactionvisualizer.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InputParameterActivity extends CustomActivity {
  private static final String TAG = "InputParameterActivity" ;
  private FulfillmentOption fulfillmentOption;
  List<String> keys = new ArrayList<>();
  Map<String, TextInputEditText> key2textInputEditTexts = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_input_parameter);
    initData();
    initView();
  }

  @Override
  protected void initData() {
    fulfillmentOption = (FulfillmentOption) getIntent().getSerializableExtra(Constant.FULFILLMENT_OPTION);
    //for those who doesn't have a parameter mapping, we require the user input
    keys.addAll(fulfillmentOption.getUrlTemplate().getParameterMapMap().keySet());
  }

  @Override
  protected void initView() {
    super.initView();
    addParameterViews();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.location_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.save) {
      checkInputAndReturn();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void checkInputAndReturn() {
    Intent intent = new Intent();
    for(Map.Entry<String, TextInputEditText> entry: key2textInputEditTexts.entrySet()) {
      String key = entry.getKey();
      if(entry.getValue() == null) {
        continue;
      }
      String value = Objects.requireNonNull(entry.getValue().getText()).toString();
      if(value.isEmpty()) {
        Utils.showMsg(getString(R.string.input_hint, key), this);
        return;
      }
      intent.putExtra(key, value);
    }
    setResult(Activity.RESULT_OK, intent);
    finish();
  }

  private void addParameterViews() {
    for(String key : keys) {
      key2textInputEditTexts.put(key, new TextInputEditText(this));
      addInputKeyLayout(key);
    }
  }

  /**
   * @param key
   * create a TextInputLayout with TextInputEditText with specific key dynamically
   */
  private void addInputKeyLayout(final String key) {
    final LinearLayout linearLayout = findViewById(R.id.parameter_list);
    TextInputLayout textInputLayout = (TextInputLayout) LayoutInflater.from(InputParameterActivity.this).inflate(R.layout.text_input, null);
    textInputLayout.setHint(key);
    key2textInputEditTexts.put(key, (TextInputEditText) textInputLayout.findViewById(R.id.text_input));
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
    params.setMargins(10, 10,10, 0);
    textInputLayout.setLayoutParams(params);
    linearLayout.addView(textInputLayout);
    linearLayout.updateViewLayout(textInputLayout, params);
  }

}