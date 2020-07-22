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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.EntitySet;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;


import com.example.appactionvisualizer.ui.activity.parameter.InputParameterActivity;
import com.example.appactionvisualizer.ui.activity.parameter.ListItemActivity;
import com.example.appactionvisualizer.ui.activity.parameter.LocationActivity;
import com.example.appactionvisualizer.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.appactionvisualizer.constants.Constant.DROP_OFF_LATITUDE_FIELD;
import static com.example.appactionvisualizer.constants.Constant.DROP_OFF_LONGITUDE_FIELD;
import static com.example.appactionvisualizer.constants.Constant.PICK_UP_LATITUDE_FIELD;
import static com.example.appactionvisualizer.constants.Constant.PICK_UP_LONGITUDE_FIELD;
import static com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption.FulfillmentMode.DEEPLINK;

/**
 * Activity for users to select parameters.
 * Use recyclerview since the typical number of parameters is unknown yet.
 */
public class ParameterActivity extends CustomActivity {
  private static final String TAG = "Parameter";
  private FulfillmentOption fulfillmentOption;
  private Action action;
  private AppAction appAction;
  private String urlTemplate;
  private TextView tvUrlTemplate, tvUrl;

  //curMap saves the mapping that user have selected
//  private Map<String, String> curMap = new HashMap<>();

  @Override
  protected void initData() {
    Intent intent = getIntent();
    fulfillmentOption = (FulfillmentOption) intent.getSerializableExtra(Constant.FULFILLMENT_OPTION);
    action = (Action) intent.getSerializableExtra(Constant.ACTION);
    appAction = (AppAction) intent.getSerializableExtra(Constant.APP_ACTION);
    urlTemplate = fulfillmentOption.getUrlTemplate().getTemplate();
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


  /**
   * set the ways of selecting parameter
   * currently 3 ways:
   * 1. select from list
   * 2. select two addresses(for transportation action intent)
   * 3. user input arbitrary text
   */
  private void setClickableText() {
    if(urlTemplate.isEmpty())
      return;
    if(fulfillmentOption.getFulfillmentMode() != DEEPLINK) {
      Utils.showMsg("non-Deeplink detected", this);
      tvUrlTemplate.setText(urlTemplate);
      return;
    }
    SpannableString ss = new SpannableString(urlTemplate);
    if(action.getIntentName().equals("actions.intent.CREATE_TAXI_RESERVATION")) {
      setLocationParameter(ss);
    }else {
      setMappingParameter(ss);
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
    final Map<String, String> parameterMapMap = fulfillmentOption.getUrlTemplate().getParameterMapMap();
    for(final Map.Entry<String, String> entry: parameterMapMap.entrySet()) {
      final String key = entry.getKey();
      ClickableSpan clickable =  new ClickableSpan() {
        @Override
        public void onClick(@NonNull View view) {
          EntitySet entitySet = checkEntitySet(entry.getValue());
          Log.d(TAG, "entity set: "+ key + "="+ (entitySet == null));
          if(entitySet != null && fulfillmentOption.getUrlTemplate().getParameterMapCount() == 1) {
            Intent intent = new Intent(ParameterActivity.this, ListItemActivity.class);
            intent.putExtra(Constant.ENTITY_SET, entitySet);
            intent.putExtra(Constant.KEY, key);
            startActivityForResult(intent, Constant.SELECT_SINGLE_PARAMETER);
          }else {
            //if no provided list for the key, jump to the input text activity
            Intent intent = new Intent(ParameterActivity.this, InputParameterActivity.class);
            intent.putExtra(Constant.FULFILLMENT_OPTION, fulfillmentOption);
            startActivityForResult(intent, Constant.SELECT_MULTIPLE_PARAMETER);
          }
        }
      };
      int start = urlTemplate.indexOf(key);
      int end = start + key.length();
      ss.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }

  //check if entity set has provided corresponding list items
  private EntitySet checkEntitySet(String key) {
    for(Action.Parameter parameter : action.getParametersList()) {
      if(parameter.getName().equals(key)) {
        String reference = parameter.getEntitySetReference(0).getEntitySetId();
        for(EntitySet set: appAction.getEntitySetsList()) {
          if(set.getItemList().getFieldsOrThrow(Constant.ENTITY_FIELD_IDENTIFIER).getStringValue().equals(reference)) {
            return set;
          }
        }
      }
    }
    return null;
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
    int idx = urlTemplate.indexOf("{");
    String curUrl = urlTemplate.substring(0, idx);
    //https://example.com/test?utm_campaign=appactions{#foo}	"foo": "123"	https://example.com/test?utm_campaign=appactions#foo=123
    //myapp://example/{foo}	"foo": "123"	myapp://example/123
    if(Character.isAlphabetic(urlTemplate.charAt(idx + 1))) {
      curUrl += identifier;
    }else {
      curUrl += urlTemplate.charAt(idx + 1) + getString(R.string.url_parameter, key, identifier);
    }
    setFulfillUrl(curUrl);
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
   * @param data intent data received from LocationActivity
   * construct the url
   */
  void replaceAddressParameter(Intent data){
    int idx = urlTemplate.indexOf("{");
    String curUrl = urlTemplate.substring(0, idx) + urlTemplate.charAt(idx + 1);
    List<String> parameters = new ArrayList<>();
    for (Map.Entry<String,String> entry : fulfillmentOption.getUrlTemplate().getParameterMapMap().entrySet()) {
      addLocationParameters(data, entry, parameters);
    }
    curUrl += TextUtils.join("&", parameters);
    setFulfillUrl(curUrl);
  }

  private void addLocationParameters(Intent data, Map.Entry<String, String> entry, List<String> parameters) {
    if(entry.getValue().equals(PICK_UP_LATITUDE_FIELD)) {
      parameters.add(getString(R.string.url_parameter, entry.getKey(), data.getStringExtra(Constant.PICK_UP_LATITUDE)));
    }else if(entry.getValue().equals(PICK_UP_LONGITUDE_FIELD)) {
      parameters.add(getString(R.string.url_parameter, entry.getKey(), data.getStringExtra(Constant.PICK_UP_LONGITUDE)));
    }else if(entry.getValue().equals(DROP_OFF_LATITUDE_FIELD)) {
      parameters.add(getString(R.string.url_parameter, entry.getKey(), data.getStringExtra(Constant.DROP_OFF_LATITUDE)));
    }else if(entry.getValue().equals(DROP_OFF_LONGITUDE_FIELD)) {
      parameters.add(getString(R.string.url_parameter, entry.getKey(), data.getStringExtra(Constant.DROP_OFF_LONGITUDE)));
    }
  }

  private void replaceMultiParameter(Intent data) {
    int idx = urlTemplate.indexOf("{");
    String curUrl = urlTemplate.substring(0, idx) + urlTemplate.charAt(idx + 1);
    List<String> parameters = new ArrayList<>();
    for(final String key : fulfillmentOption.getUrlTemplate().getParameterMapMap().keySet()) {
      String value = data.getStringExtra(key);
      parameters.add(getString(R.string.url_parameter, key, value));
    }
    curUrl += TextUtils.join("&", parameters);
    setFulfillUrl(curUrl);
  }
}