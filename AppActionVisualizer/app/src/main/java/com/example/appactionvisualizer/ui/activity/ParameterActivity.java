package com.example.appactionvisualizer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.Fulfillment;
import com.example.appactionvisualizer.databean.ParameterMapping;
import com.example.appactionvisualizer.ui.activity.parameter.InputParameterActivity;
import com.example.appactionvisualizer.ui.activity.parameter.ListItemActivity;
import com.example.appactionvisualizer.ui.activity.parameter.LocationActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity for users to select parameters.
 * Use recyclerview since the typical number of parameters is unknown yet.
 */
public class ParameterActivity extends CustomActivity {
  private static final String TAG = "Parameter";
  private Fulfillment fulfillment;
  private String urlTemplate;
  private TextView tvUrlTemplate, tvUrl;

  //curMap saves the mapping that user have selected
  private Map<String, String> curMap = new HashMap<>();

  @Override
  protected void initData() {
    Intent intent = getIntent();
    fulfillment = (Fulfillment) intent.getSerializableExtra(Constant.FULFILLMENT);
  }


  @Override
  protected void initView() {
    super.initView();
    getSupportActionBar().setTitle(TAG);
    tvUrlTemplate = findViewById(R.id.url_template);
    tvUrl = findViewById(R.id.url);
    setClickableText();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_parameter);
    initData();
    initView();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == Activity.RESULT_CANCELED) {
      return;
    }
    switch (requestCode) {
      case Constant.SELECT_SINGLE_PARAMETER:
        replaceSingleParameter(data);
        break;
      case Constant.SELECT_ADDRESS:
        replaceAddressParameter(data);
        break;
      case Constant.SELECT_MULTIPLE_PARAMETER:
        replaceMultiParameter(data);
        break;
    }
  }

  private void replaceMultiParameter(Intent data) {
    int idx = urlTemplate.indexOf("{");
    String curUrl = urlTemplate.substring(0, idx) + urlTemplate.charAt(idx + 1);
    List<String> text = new ArrayList<>();
    final ParameterMapping parameterMapping = fulfillment.getParameterMapping();
    for(final Map.Entry<String, List<ParameterMapping.Mapping>> entry: parameterMapping.getKey2MapList().entrySet()) {
      String key = entry.getKey();
      String value = data.getStringExtra(key);
      text.add(key + "=" + value);
    }
    curUrl += TextUtils.join("&", text);
    setFulfillUrl(curUrl);
  }


  /**
   * set the ways of selecting parameter
   * currently 3 ways:
   * 1. select from list
   * 2. select two addresses(for transportation action intent)
   * 3. user input arbitrary text
   */
  private void setClickableText() {
    urlTemplate = fulfillment.getUrlTemplate();
    if(urlTemplate.isEmpty())
      return;
    SpannableString ss = new SpannableString(urlTemplate);
    if(fulfillment.getParameterMapping() != null) {
      setMappingParameter(ss);
    }else {
      setLocationParameter(ss);
    }
    tvUrlTemplate.setText(ss);
    tvUrlTemplate.setMovementMethod(LinkMovementMethod.getInstance());
  }

  private void setLocationParameter(SpannableString ss) {
    ClickableSpan clickable =  new ClickableSpan() {
      @Override
      public void onClick(@NonNull View view) {
        Intent intent = new Intent(ParameterActivity.this, LocationActivity.class);
        startActivityForResult(intent, Constant.SELECT_ADDRESS);
      }
    };
    int start = urlTemplate.indexOf("{");
    int end = urlTemplate.length();
    if(start == -1)
      return;
    ss.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  private void setMappingParameter(SpannableString ss) {
    final ParameterMapping parameterMapping = fulfillment.getParameterMapping();
    for(final Map.Entry<String, List<ParameterMapping.Mapping>> entry: parameterMapping.getKey2MapList().entrySet()) {
      final String key = entry.getKey();
      ClickableSpan clickable =  new ClickableSpan() {
        @Override
        public void onClick(@NonNull View view) {
          List<ParameterMapping.Mapping> mappingList = entry.getValue();
          if(mappingList == null) {
            //if no provided list for the key, jump to the input text activity
            Intent intent = new Intent(ParameterActivity.this, InputParameterActivity.class);
            intent.putExtra(Constant.PARAMETER_MAPPING, parameterMapping);
            startActivityForResult(intent, Constant.SELECT_MULTIPLE_PARAMETER);

          }else {
            Intent intent = new Intent(ParameterActivity.this, ListItemActivity.class);
            intent.putExtra(Constant.PARAMETER_MAPPING, fulfillment.getParameterMapping());
            intent.putExtra(Constant.KEY, key);
            startActivityForResult(intent, Constant.SELECT_SINGLE_PARAMETER);
          }
        }
      };
      int start = urlTemplate.indexOf(key);
      int end = start + key.length();
      ss.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }


  /**
   * @param data intent data received from selectActivity
   * construct the url
   */
  void replaceSingleParameter(Intent data){
    String key = data.getStringExtra(Constant.KEY);
    String identifier = data.getStringExtra(Constant.IDENTIFIER);
    if(key == null)
      return;
    curMap.put(key, identifier);
    String curUrl = fulfillment.getUrlTemplate();
    for(Map.Entry<String, String> entry : curMap.entrySet()) {
      curUrl = curUrl.replaceAll(entry.getKey(), entry.getValue());
    }
    boolean buildDone = true;
    for(final Map.Entry<String, List<ParameterMapping.Mapping>> entry: fulfillment.getParameterMapping().getKey2MapList().entrySet()) {
      if(curUrl.contains(entry.getKey())) {
        buildDone = false;
        break;
      }
    }
    if(buildDone) {
      curUrl = curUrl.replaceAll("[{}?]", "");
      setFulfillUrl(curUrl);
    }else {
      tvUrl.setText(curUrl);
    }
  }


  /**
   * @param curUrl the constructed url
   * set the constructed url which can be used to open corresponding application
   */
  private void setFulfillUrl(final String curUrl) {
    tvUrl.setText(curUrl);
    tvUrl.setTextColor(getResources().getColor(R.color.colorAccent));
    tvUrl.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(curUrl));
        startActivity(intent);
      }
    });
  }


  /**
   * @param data intent data received from selectActivity
   * construct the url
   */
  void replaceAddressParameter(Intent data){
    int idx = urlTemplate.indexOf("{");
    String curUrl = urlTemplate.substring(0, idx);
    String pickupLat = data.getStringExtra(Constant.PICK_UP_LATITUDE);
    String pickupLong = data.getStringExtra(Constant.PICK_UP_LONGITUDE);
    String dropOffLat = data.getStringExtra(Constant.DROP_OFF_LATITUDE);
    String dropOffLong = data.getStringExtra(Constant.DROP_OFF_LONGITUDE);
    //todo: need auto parser
    if(curUrl.contains("uber")) {
      curUrl += "&pickup[latitude]=" + pickupLat +"&pickup[longitude]=" +pickupLong + "&dropoff[latitude]=" +dropOffLat+ "&dropoff[longitude]=" +dropOffLong;
    }else {
      curUrl += "&pickup_latitude=" + pickupLat +"&pickup_longitude=" +pickupLong + "&dropoff_latitude=" +dropOffLat+ "&dropoff_longitude=" +dropOffLong;
    }
    setFulfillUrl(curUrl);
  }
}