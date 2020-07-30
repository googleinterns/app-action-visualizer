package com.example.appactionvisualizer.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Entity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos;
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.databean.AppActionProtos.EntitySet;
import com.example.appactionvisualizer.ui.activity.parameter.InputParameterActivity;
import com.example.appactionvisualizer.ui.activity.parameter.LocationActivity;
import com.example.appactionvisualizer.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

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
  private TextView tvUrlTemplate, tvUrl, link;

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
    tvUrlTemplate = findViewById(R.id.url_template);
    tvUrl = findViewById(R.id.url);
    link = findViewById(R.id.link);
    setReferenceLink();
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
    if (resultCode == Activity.RESULT_CANCELED) {
      return;
    }
    switch (requestCode) {
      case Constant.INPUT_PARAMETER:
        replaceParameter(data);
        break;
      case Constant.SELECT_ADDRESS:
        replaceAddressParameter(data);
        break;
      case Constant.INPUT_URL:
        break;
    }
  }

  //set a reference to corresponding official page
  private void setReferenceLink() {
    String intentName = action.getIntentName();
    String title = intentName.substring(intentName.lastIndexOf('.') + 1);
    getSupportActionBar().setTitle(title);
    String intentUrl = title.toLowerCase().replaceAll("_", "-");
    String linkString = getString(R.string.url_action_prefix, ActionType.getActionTypeByName(intentName).getUrl(), intentUrl);
    setClickableText(link, linkString);
  }


  /**
   * set the ways of selecting parameter
   * currently 2 ways:
   * 1. user inputs arbitrary text/select from list
   * 2. select two addresses(for transportation action intent)
   */
  private void setClickableText() {
    if (urlTemplate.isEmpty())
      return;
    if (fulfillmentOption.getFulfillmentMode() != DEEPLINK) {
      Utils.showMsg("non-Deeplink detected", this);
      tvUrlTemplate.setText(urlTemplate);
      return;
    }
    SpannableString ss = new SpannableString(urlTemplate);
    if (action.getIntentName().equals(getString(R.string.create_taxi))) {
      setLocationParameter(ss);
    } else if(urlTemplate.contains("@url")){
      setUrlParameter(ss);
    }else{
      setMappingParameter(ss);
    }
    tvUrlTemplate.setText(ss);
    tvUrlTemplate.setMovementMethod(LinkMovementMethod.getInstance());
  }

  //for the @url case, just pop up a window for user to choose
  private void setUrlParameter(SpannableString ss) {
    if(fulfillmentOption.getUrlTemplate().getParameterMapCount() > 0 || !action.getParameters(0).getEntitySetReference(0).getUrlFilter().isEmpty()) {
      Utils.showMsg("Do not support url-filter now", this);
      return;
    }
    final EntitySet entitySet = checkUrlEntitySet();
    if(entitySet == null) {
      Utils.showMsg(getString(R.string.error_parsing), this);
      return;
    }
    ClickableSpan clickable = new ClickableSpan() {
      @Override
      public void onClick(@NonNull View view) {
        final ListValue listValue = entitySet.getItemList().getFieldsOrThrow(Constant.ENTITY_ITEM_LIST).getListValue();
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            Struct item = listValue.getValues(i).getStructValue();
            String url = item.getFieldsOrThrow(Constant.ENTITY_URL).getStringValue();
            setClickableText(tvUrl, url);
          }
        };
        List<CharSequence> list = new ArrayList<>();
        //set the list contents
        for (Value entity : listValue.getValuesList()) {
          list.add(entity.getStructValue().getFieldsOrThrow(Constant.ENTITY_FIELD_NAME).getStringValue());
        }
        String title = entitySet.getItemList().getFieldsOrThrow(Constant.ENTITY_FIELD_IDENTIFIER).getStringValue();
        Utils.popUpDialog(ParameterActivity.this, title, list, listener);
      }
    };
    ss.setSpan(clickable, 0, urlTemplate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  }


  private EntitySet checkUrlEntitySet() {
    String parameterValue = "feature";
    for (Action.Parameter parameter : action.getParametersList()) {
      if (parameter.getName().equals(parameterValue)) {
        if (parameter.getEntitySetReferenceCount() == 0)
          continue;
        String reference = parameter.getEntitySetReference(0).getEntitySetId();
        for (EntitySet set : appAction.getEntitySetsList()) {
          if (set.getItemList().getFieldsOrThrow(Constant.ENTITY_FIELD_IDENTIFIER).getStringValue().equals(reference)) {
            return set;
          }
        }
      }
    }
    return null;
  }

  //set the create_taxi intent
  private void setLocationParameter(SpannableString ss) {
    ClickableSpan clickable = new ClickableSpan() {
      @Override
      public void onClick(@NonNull View view) {
        Intent intent = new Intent(ParameterActivity.this, LocationActivity.class);
        startActivityForResult(intent, Constant.SELECT_ADDRESS);
      }
    };
    int start = urlTemplate.indexOf("{");
    int end = urlTemplate.length();
    if (start == -1)
      return;
    ss.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  //set parameters for the fulfillment option
  private void setMappingParameter(SpannableString ss) {
    final Map<String, String> parameterMapMap = fulfillmentOption.getUrlTemplate().getParameterMapMap();
    for (final Map.Entry<String, String> entry : parameterMapMap.entrySet()) {
      final String key = entry.getKey();
      ClickableSpan clickable = new ClickableSpan() {
        @Override
        public void onClick(@NonNull View view) {
          Intent intent = new Intent(ParameterActivity.this, InputParameterActivity.class);
          intent.putExtra(Constant.FULFILLMENT_OPTION, fulfillmentOption);
          intent.putExtra(Constant.ACTION, action);
          intent.putExtra(Constant.APP_ACTION, appAction);
          startActivityForResult(intent, Constant.INPUT_PARAMETER);
        }
      };
      int start = urlTemplate.indexOf(key, urlTemplate.indexOf("{"));
      int end = start + key.length();
      ss.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
  }


  /**
   * @param curUrl the constructed url
   * set the constructed deeplink
   */
  private void setClickableText(final TextView tvUrl, final String curUrl) {
    tvUrl.setText(curUrl);
    tvUrl.setTextColor(getResources().getColor(R.color.colorAccent));
    tvUrl.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_set_as, 0, 0, 0);
    tvUrl.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Utils.jumpToApp(ParameterActivity.this, curUrl, appAction.getPackageName());
      }
    });
  }


  /**
   * @param data intent data received from LocationActivity
   *             construct the url
   */
  void replaceAddressParameter(Intent data) {
    int idx = urlTemplate.indexOf("{");
    String curUrl = urlTemplate.substring(0, idx) + urlTemplate.charAt(idx + 1);
    List<String> parameters = new ArrayList<>();
    for (Map.Entry<String, String> entry : fulfillmentOption.getUrlTemplate().getParameterMapMap().entrySet()) {
      addLocationParameters(data, entry, parameters);
    }
    curUrl += TextUtils.join("&", parameters);
    setClickableText(tvUrl, curUrl);
  }

  //add latitude and longitude parameters to the url
  private void addLocationParameters(Intent data, Map.Entry<String, String> entry, List<String> parameters) {
    if (entry.getValue().equals(PICK_UP_LATITUDE_FIELD)) {
      parameters.add(getString(R.string.url_parameter, entry.getKey(), data.getStringExtra(Constant.PICK_UP_LATITUDE)));
    } else if (entry.getValue().equals(PICK_UP_LONGITUDE_FIELD)) {
      parameters.add(getString(R.string.url_parameter, entry.getKey(), data.getStringExtra(Constant.PICK_UP_LONGITUDE)));
    } else if (entry.getValue().equals(DROP_OFF_LATITUDE_FIELD)) {
      parameters.add(getString(R.string.url_parameter, entry.getKey(), data.getStringExtra(Constant.DROP_OFF_LATITUDE)));
    } else if (entry.getValue().equals(DROP_OFF_LONGITUDE_FIELD)) {
      parameters.add(getString(R.string.url_parameter, entry.getKey(), data.getStringExtra(Constant.DROP_OFF_LONGITUDE)));
    }
  }


  /**
   * @param data intent data received from selectActivity
   *             construct the url
   */
  void replaceSingleParameter(String key, Intent data) {
    String identifier = data.getStringExtra(key);
    if (key == null)
      return;
    int firstPartIdx = urlTemplate.indexOf("{");
    int secondPartIdx = urlTemplate.indexOf("}");
    String curUrl = urlTemplate.substring(0, firstPartIdx);
    //https://example.com/test?utm_campaign=appactions{#foo}	"foo": "123"	https://example.com/test?utm_campaign=appactions#foo=123
    //myapp://example/{foo}	"foo": "123"	myapp://example/123
    if (Character.isAlphabetic(urlTemplate.charAt(firstPartIdx + 1))) {
      curUrl += identifier;
    } else {
      curUrl += urlTemplate.charAt(firstPartIdx + 1) + getString(R.string.url_parameter, key, identifier);
    }
    curUrl += urlTemplate.substring(secondPartIdx + 1);
    setClickableText(tvUrl, curUrl);
  }

  //replace multiple parameters for url
  private void replaceParameter(Intent data) {
    if (fulfillmentOption.getUrlTemplate().getParameterMapCount() == 1) {
      replaceSingleParameter(fulfillmentOption.getUrlTemplate().getParameterMapMap().keySet().iterator().next(), data);
      return;
    }
    int firstPartIdx = urlTemplate.indexOf("{");
    int secondPartIdx = urlTemplate.indexOf("}");
    String curUrl = urlTemplate.substring(0, firstPartIdx) + urlTemplate.charAt(firstPartIdx + 1);
    List<String> parameters = new ArrayList<>();
    for (final String key : fulfillmentOption.getUrlTemplate().getParameterMapMap().keySet()) {
      String value = data.getStringExtra(key);
      parameters.add(getString(R.string.url_parameter, key, value));
    }
    curUrl += TextUtils.join("&", parameters);
    curUrl += urlTemplate.substring(secondPartIdx + 1);
    setClickableText(tvUrl, curUrl);
  }

}