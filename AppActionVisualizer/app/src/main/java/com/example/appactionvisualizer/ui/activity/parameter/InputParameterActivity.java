package com.example.appactionvisualizer.ui.activity.parameter;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ParameterMapping;
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
  private ParameterMapping parameterMapping;
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
    parameterMapping = (ParameterMapping) getIntent().getSerializableExtra(Constant.PARAMETER_MAPPING);
    //for those who doesn't have a parameter mapping, we require the user input
    for(final Map.Entry<String, List<ParameterMapping.Mapping>> entry: parameterMapping.getKey2MapList().entrySet()) {
      if(entry.getValue()== null) {
        keys.add(entry.getKey());
      }
    }
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
        Log.d(TAG, "NULL:" + key);
        continue;
      }
      String value = Objects.requireNonNull(entry.getValue().getText()).toString();
      if(value.isEmpty()) {
        Utils.showMsg(getString(R.string.input_hint, key), this);
        return;
      }
      Log.d(TAG, key + ":" + value);
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

  private void addSelectKeyLayout(final String key) {

    String[] test = new String[]{"haha", "hoho"};

    Spinner spinner = (Spinner) findViewById(R.id.spinner);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, test);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

      }
    });
  }

}