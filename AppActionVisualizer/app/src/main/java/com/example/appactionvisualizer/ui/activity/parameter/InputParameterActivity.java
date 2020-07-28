package com.example.appactionvisualizer.ui.activity.parameter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.EntitySet;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.ui.activity.CustomActivity;
import com.example.appactionvisualizer.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InputParameterActivity extends CustomActivity {
  private static final String TAG = "InputParameterActivity";
  List<String> keys = new ArrayList<>();
  Map<String, String> map = new HashMap<>();
  Map<String, TextInputEditText> key2textInputEditTexts = new HashMap<>();
  private FulfillmentOption fulfillmentOption;
  private Action action;
  private AppAction appAction;

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
    if (fulfillmentOption != null) {
      keys.addAll(fulfillmentOption.getUrlTemplate().getParameterMapMap().keySet());
      map = fulfillmentOption.getUrlTemplate().getParameterMapMap();
    }
    action = (Action) getIntent().getSerializableExtra(Constant.ACTION);
    appAction = (AppAction) getIntent().getSerializableExtra(Constant.APP_ACTION);
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
    for (Map.Entry<String, TextInputEditText> entry : key2textInputEditTexts.entrySet()) {
      String key = entry.getKey();
      if (entry.getValue() == null) {
        continue;
      }
      String value = Objects.requireNonNull(entry.getValue().getText()).toString();
      if (value.isEmpty()) {
        Utils.showMsg(getString(R.string.input_hint, key), this);
        return;
      }
      //convert to identifier
      if (value.contains("(")) {
        value = value.substring(value.lastIndexOf("(") + 1, value.length() - 1);
      }
      intent.putExtra(key, value);
    }
    setResult(Activity.RESULT_OK, intent);
    finish();
  }

  private void addParameterViews() {
    for (String key : keys) {
      addInputKeyLayout(key);
    }
  }

  /**
   * @param key create a TextInputLayout with TextInputEditText with specific key dynamically
   */
  private void addInputKeyLayout(final String key) {
    final LinearLayout linearLayout = findViewById(R.id.parameter_list);
    TextInputLayout textInputLayout = (TextInputLayout) LayoutInflater.from(InputParameterActivity.this).inflate(R.layout.text_input, null);
    textInputLayout.setHint(key);
    textInputLayout.setHelperText(map.get(key));
    final TextInputEditText textInput = textInputLayout.findViewById(R.id.text_input);
    key2textInputEditTexts.put(key, textInput);
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.setMargins(10, 10, 10, 0);
    textInputLayout.setLayoutParams(params);
    final EntitySet entitySet = checkEntitySet(key);
    if (entitySet != null) {
      textInput.setFocusable(false);
      textInput.setClickable(true);
      textInput.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          popUpDialog(textInput, entitySet);
        }
      });
    }

    linearLayout.addView(textInputLayout);
  }

  /**
   * @param textInput textInput to trigger the dialog
   * @param entitySet the entity set of certain key
   * dialog for user to choose among given list values
   */
  private void popUpDialog(final TextInputEditText textInput, final EntitySet entitySet) {
    try {
      final ListValue listValue = entitySet.getItemList().getFieldsOrThrow(Constant.ENTITY_ITEM_LIST).getListValue();
      MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
      materialAlertDialogBuilder.setTitle(entitySet.getItemList().getFieldsOrThrow(Constant.ENTITY_FIELD_IDENTIFIER).getStringValue());
      List<CharSequence> list = new ArrayList<>();
      for (Value entity : listValue.getValuesList()) {
        Value identifier = entity.getStructValue().getFieldsOrThrow(Constant.ENTITY_FIELD_IDENTIFIER);
        list.add(entity.getStructValue().getFieldsOrDefault(Constant.ENTITY_FIELD_NAME, identifier).getStringValue());
      }
      CharSequence[] keys = new CharSequence[list.size()];
      materialAlertDialogBuilder.setItems(list.toArray(keys), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
          Struct item = listValue.getValues(i).getStructValue();
          Value identifier = item.getFieldsOrThrow(Constant.ENTITY_FIELD_IDENTIFIER);
          textInput.setText(getString(R.string.addition_text, item.getFieldsOrDefault(Constant.ENTITY_FIELD_NAME, identifier).getStringValue(), identifier.getStringValue()));
        }
      }).show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  //check if entity set has provided corresponding list items
  private EntitySet checkEntitySet(String key) {
    Log.d(TAG, "checkEntitySet: " + key);
    String parameterValue = fulfillmentOption.getUrlTemplate().getParameterMapMap().get(key);
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
}