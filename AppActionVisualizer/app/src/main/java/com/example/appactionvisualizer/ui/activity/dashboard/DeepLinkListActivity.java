package com.example.appactionvisualizer.ui.activity.dashboard;

import android.app.RemoteAction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassificationManager;
import android.view.textclassifier.TextClassifier;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos;
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.databean.Tuple;
import com.example.appactionvisualizer.ui.adapter.ExpandableAdapter;
import com.example.appactionvisualizer.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// Display deep links using an expandable list view
public class DeepLinkListActivity extends AppCompatActivity {
  private static final String TAG = DeepLinkListActivity.class.getSimpleName();
  // For each action name, we need a tuple of <AppAction, Action, FulfillmentOption> data
  // so that the link can jump into ParameterActivity and parse required data to activity.
  private Map<String, List<Tuple<AppAction, Action, FulfillmentOption>>> intentMap;
  // These bits are used to indicate classify results
  private static final int UPDATE = 1, ERROR = 2;

  private Handler classifyHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_deep_link_list);
    initData();
    initView();
    getUserInput();
  }

  protected void getUserInput() {
    final EditText userInput = findViewById(R.id.user_input);
    userInput.setOnKeyListener(new View.OnKeyListener() {
      @RequiresApi(api = Build.VERSION_CODES.P)
      @Override
      public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
          String text = userInput.getText().toString();
          recommend(text);
          return true;
        }
        return false;
      }
    });
  }


  protected void recommend(final String sentence) {
    // Split the sentence using space---similar to tokenization
    String[] words = sentence.split(" ");
    for(String str : words) {
      String app = checkApp(str);
      if(!app.isEmpty()) {

      }
    }
  }

  private String checkApp(String str) {
    return "";
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  private void textClassify(final String text) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          TextClassificationManager textClassificationManager = (TextClassificationManager) getSystemService(Context.TEXT_CLASSIFICATION_SERVICE);
          TextClassifier textClassifier = textClassificationManager.getTextClassifier();
          TextClassification textClassification = textClassifier.classifyText(text, 0, text.length() - 1, LocaleList.getDefault());
          Log.d(TAG, "action size: "+ textClassification.getActions().size() );
          for(RemoteAction action : textClassification.getActions()) {
            Log.d(TAG, (String) action.getTitle());
            Log.d(TAG, (String) action.getContentDescription());
          }
          Log.d(TAG, "EntityCount:" + textClassification.getEntityCount());
          Log.d(TAG, "high confidence:" + textClassification.getEntity(0));
          classifyHandler.sendEmptyMessage(UPDATE);
        } catch (Exception e) {
          classifyHandler.sendEmptyMessage(ERROR);
          e.printStackTrace();
        }
      }
    }).start();


  }

  protected void initData() {
    // Use tree map so that the actions are sorted.
    // The total actions wouldn't be much so it wouldn't lose much time compared to hash map.
    // Make sure actions with same type stay together.
    Comparator<String> comparator =
        new Comparator<String>() {
          @Override
          public int compare(String s1, String s2) {
            ActionType type1 = ActionType.getActionTypeByName(s1);
            ActionType type2 = ActionType.getActionTypeByName(s2);
            if (!type1.equals(type2)) {
              return type1.compareTo(type2);
            }
            return s1.compareTo(s2);
          }
        };
    intentMap = new TreeMap<>(comparator);
    extractActions();
    classifyHandler = new Handler(Looper.getMainLooper()) {
      @Override
      public void handleMessage(@NonNull Message msg) {
        if (msg.what == UPDATE) {
        } else if (msg.what == ERROR) {
          Utils.showMsg(getString(R.string.unknown_texts), DeepLinkListActivity.this);
        }
      }
    };
  }

  protected void initView() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    displayDeepLinks();
  }

  /**
   * Generate a <key: action, value: Tuple> tree map from current data, need tree map since we want
   * sorted actions.
   */
  private void extractActions() {
    // Iterate over the whole list to get the numbers, and add fulfillment options to their
    // corresponding intent name.
    for (AppActionProtos.AppAction appAction : AppActionsGenerator.appActions) {
      for (AppActionProtos.Action action : appAction.getActionsList()) {
        String intentName = action.getIntentName();
        for (AppActionProtos.FulfillmentOption fulfillmentOption :
            action.getFulfillmentOptionList()) {
          // The Slice options could not be counted as deep links.
          if (fulfillmentOption.getFulfillmentMode()
              == AppActionProtos.FulfillmentOption.FulfillmentMode.SLICE) {
            continue;
          }
          if (intentMap.get(intentName) == null) {
            intentMap.put(intentName, new ArrayList<Tuple<AppAction, Action, FulfillmentOption>>());
          }
          intentMap.get(intentName).add(new Tuple<>(appAction, action, fulfillmentOption));
        }
      }
    }
  }

  /** Parse data and pass it to the expandable list view. */
  private void displayDeepLinks() {
    ExpandableListView expandableListView = findViewById(R.id.expandable_list);
    // Display action names as group titles.
    final List<String> actionNames = new ArrayList<>(intentMap.keySet());
    ExpandableListAdapter adapter = new ExpandableAdapter(this, intentMap, actionNames);
    expandableListView.setAdapter(adapter);
    expandableListView.setOnChildClickListener(
        new ExpandableListView.OnChildClickListener() {
          @Override
          public boolean onChildClick(
              ExpandableListView expandableListView,
              View view,
              int groupPosition,
              int childPosition,
              long id) {
            // The jump logic is the same as the click of deep links at ActionsActivity page:
            // If the deep link needs parameters, jump into ParameterActivity.
            // Otherwise, jump to corresponding apps
            Tuple<AppAction, Action, FulfillmentOption> listData =
                intentMap.get(actionNames.get(groupPosition)).get(childPosition);
            Utils.jumpByType(
                DeepLinkListActivity.this, listData.left, listData.mid, listData.right);
            return false;
          }
        });
  }

  public Map<String, List<Tuple<AppAction, Action, FulfillmentOption>>> getIntentMap() {
    return intentMap;
  }
}
