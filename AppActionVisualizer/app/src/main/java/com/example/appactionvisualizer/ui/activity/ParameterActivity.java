package com.example.appactionvisualizer.ui.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.ui.activity.parameter.InputParameterActivity;
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
    } else {
      setMappingParameter(ss);
    }
    tvUrlTemplate.setText(ss);
    tvUrlTemplate.setMovementMethod(LinkMovementMethod.getInstance());
  }

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
        Intent intent = new Intent();
        try {
          intent = Intent.parseUri(curUrl, 0);
          startActivity(intent);
        } catch (Exception e) {
          Utils.goToStore(ParameterActivity.this, appAction.getPackageName());
        }
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