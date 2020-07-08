package com.example.appactionvisualizer.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.Fulfillment;
import com.example.appactionvisualizer.databean.ParameterMapping;
import com.example.appactionvisualizer.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity for users to select parameters.
 * Use recyclerview since the typical number of parameters is unknown yet.
 */
public class ParameterActivity extends CustomActivity {
  private static final String TAG = "Parameter";
  Fulfillment fulfillment;
  private TextView tvUrlTemplate, tvUrl;

  //curMap saves the mapping that user have selected
  private Map<String, String> curMap = new HashMap<>();

  @Override
  void initData() {
    Intent intent = getIntent();
    fulfillment = (Fulfillment) intent.getSerializableExtra(Constant.FULFILLMENT);
  }

  private void setClickableText() {
    String urlTemplate = fulfillment.getUrlTemplate();

    SpannableString ss = new SpannableString(urlTemplate);
    for(final Map.Entry<String, List<ParameterMapping.Mapping>> entry: fulfillment.getParameterMapping().getKey2MapList().entrySet()) {
      final String key = entry.getKey();
      ClickableSpan clickable =  new ClickableSpan() {
        @Override
        public void onClick(@NonNull View view) {
          List<ParameterMapping.Mapping> mappingList = fulfillment.getParameterMapping().getKey2MapList().get(key);
          if(mappingList == null) {
            Utils.showMsg("no parameter mapping for " + key, ParameterActivity.this);
          }else {
            Intent intent = new Intent(ParameterActivity.this, SelectActivity.class);
            intent.putExtra(Constant.PARAMETER_MAPPING, fulfillment.getParameterMapping());
            intent.putExtra(Constant.KEY, key);
            startActivityForResult(intent, Constant.SELECT_PARAMETER);
          }
        }
      };
      int start = urlTemplate.indexOf(key);
      int end = start + key.length();
      ss.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    tvUrlTemplate.setText(ss);
    tvUrlTemplate.setMovementMethod(LinkMovementMethod.getInstance());
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == Activity.RESULT_CANCELED) {
      return;
    }
    if(requestCode == Constant.SELECT_PARAMETER) {
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
        tvUrl.setText(curUrl);
        final String finalUrl = curUrl;
        tvUrl.setTextColor(getResources().getColor(R.color.colorAccent));
        tvUrl.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
            startActivity(intent);
          }
        });
      }else {
        tvUrl.setText(curUrl);
      }
    }
  }

  @Override
  void initView() {
    super.initView();
    getSupportActionBar().setTitle(TAG);
    tvUrlTemplate = findViewById(R.id.tv_url_template);
    tvUrl = findViewById(R.id.tv_url);
    setClickableText();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_parameter);
    initData();
    initView();
  }
}