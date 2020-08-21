package com.example.appactionvisualizer.ui.activity.dashboard;

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
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos.*;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.databean.AppFulfillment;
import com.example.appactionvisualizer.ui.activity.CustomActivity;
import com.example.appactionvisualizer.ui.adapter.ExpandableAdapter;
import com.example.appactionvisualizer.utils.Utils;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.example.appactionvisualizer.constants.Constant.ENTITY_URL;

// Display deep links using an expandable list view.
public class DeepLinkListActivity extends CustomActivity {
  private static final String TAG = DeepLinkListActivity.class.getSimpleName();
  // These bits are used to indicate classify results.
  private static final int UPDATE = 1, ERROR = 2;
  // Action names are group titles.
  final List<String> actionNames = new ArrayList<>();
  // For each action name, we need a structure of <AppAction, Action, FulfillmentOption> data
  // so that the link can jump into ParameterActivity and parse required data to activity.
  private Map<String, List<AppFulfillment>> intentMap, defaultMap;
  // map app name to app action
  private Map<String, AppAction> appNameMap = new HashMap<>();
  // map action name to a list of words
  // eg: <"actions.intent.OPEN_APP_FEATURE", ["open", "app", "feature"]>
  private Map<String, String[]> actionNameWords = new HashMap<>();
  private Handler classifyHandler;
  private BaseExpandableListAdapter adapter;
  private ExpandableListView expandableListView;

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
    userInput.setOnKeyListener(
        new View.OnKeyListener() {

          @Override
          public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
              String text = userInput.getText().toString();
              recommend(text);
              return true;
            }
            return false;
          }
        });
    ImageButton clear = findViewById(R.id.clear);
    clear.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            userInput.setText("");
            updateView(null);
          }
        });
  }

  /**
   * @param input user input sentence
   * Make a recommendation based on user inputs
   */
  protected void recommend(final String input) {
    Log.d(TAG, "RECOMMEND: " + input);
    Map<String, List<AppFulfillment>> displayMap = getDisplayMap(input);
    updateView(displayMap);
  }

  /**
   * @param input user input sentence
   * @return data to be displayed
   *     1. Tokenize user input into a string
   *     array 2. Try to detect app name 3. Match string array with current action names 4. Get
   *      recommended data and update the view
   */
  private Map<String, List<AppFulfillment>> getDisplayMap(final String input) {
    Map<String, List<AppFulfillment>> displayMap = null;
    // Ignore invalid inputs, only accept a-z A-z 0-9 and whitespace
    String sentence = input.replaceAll("[^a-zA-Z0-9\\s]", "");
    if (sentence.trim().isEmpty()) {
      return null;
    }
    // Split the sentence using space(similar to tokenization)
    String[] words = sentence.toLowerCase().split(" ");
    String appName = checkApp(words);
    // Detect no app name, try to match all actions
    if (appName.isEmpty()) {
      //todo: try parse using text classifier
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        classifyText(sentence);
      }
      // If app name is not found, select actions from all actions.
      displayMap = getActionFromWords(defaultMap, words);
      return displayMap;
    }
    // There's an app name in this sentence, we can read all the actions of this specific app.
    // Returned appAction wouldn't be null since app name is detected from appNameMap's keySet
    AppAction appAction = appNameMap.get(appName);
    Map<String, List<AppFulfillment>> singleAppMap = parseActionsFromApp(appAction);
    // Try to match some inline inventory items using user input
    displayMap = getDeepLinkFromInventory(appAction, sentence);
    if(displayMap != null) {
      return displayMap;
    }
    // There's no need to parse other words since the app has only one action.
    if (appAction.getActionsCount() == 1) {
      return singleAppMap;
    }
    // Try to match some action name of this app using user input
    displayMap = getActionFromWords(singleAppMap, words);
    return displayMap;
  }

  /**
   * @param appAction matched app
   * @param sentence user input sentence
   * @return the found data or null if not found
   * try to get specific deep links using inline inventory
   */
  private Map<String, List<AppFulfillment>> getDeepLinkFromInventory(
      AppAction appAction, String sentence) {
    for(Action action : appAction.getActionsList()) {
      if(action.getParametersCount() == 0)
        continue;
      for(FulfillmentOption fulfillmentOption : action.getFulfillmentOptionList()) {
        // Deal with {@url} case
        if(fulfillmentOption.getUrlTemplate().getTemplate().contains(Constant.URL_NO_LINK)) {
          Log.d(TAG, "@url: " + appAction.getPackageName());
          return checkUrlEntity(appAction, action, fulfillmentOption, sentence);
        }
        // Todo: deal with others
      }
    }

    return null;
  }

  // Check inline inventory's urls and try to find a match
  private Map<String, List<AppFulfillment>> checkUrlEntity(AppAction appAction, Action action, FulfillmentOption fulfillmentOption, String sentence) {
    if(fulfillmentOption.getUrlTemplate().getParameterMapCount() > 0 || !action.getParameters(0).getEntitySetReference(0).getUrlFilter().isEmpty()) {
      return null;
    }
    final EntitySet entitySet = checkUrlEntitySet(appAction, action);
    if(entitySet == null) {
      return null;
    }
    // For all the url values, app action would provide a corresponding name. Find if the sentence contains any of these name and this url is our recommended one.
    final ListValue listValue = entitySet.getItemList().getFieldsOrThrow(Constant.ENTITY_ITEM_LIST).getListValue();
    // Get the list contents from list value's names
    for (Value entity : listValue.getValuesList()) {
      String name = entity.getStructValue().getFieldsOrThrow(Constant.ENTITY_FIELD_NAME).getStringValue();
      if(sentence.contains(name)) {
        // Build a fulfillment containing the found url
        Map<String, List<AppFulfillment>> matched = new HashMap<>();
        List<AppFulfillment> list = new ArrayList<>();
        UrlTemplate builtUrl = UrlTemplate.newBuilder().setTemplate(entity.getStructValue().getFieldsOrThrow(ENTITY_URL).getStringValue()).build();
        FulfillmentOption builtFulfillment = FulfillmentOption.newBuilder().setUrlTemplate(builtUrl).build();
        list.add(new AppFulfillment(appAction, action, builtFulfillment));
        matched.put(action.getIntentName(), list);
        return matched;
      }
    }
    return null;
  }

  private EntitySet checkUrlEntitySet(AppAction appAction, Action action) {
    for (Action.Parameter parameter : action.getParametersList()) {
      if (parameter.getName().equals(Constant.URL_KEY)) {
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

  /**
   * @param allActions The actions pool to be selected, key is the action name.
   * @param inputWords all the words of user input.
   * @return the most likely action(s). We need to find which actions are suited among all actions
   */
  private Map<String, List<AppFulfillment>> getActionFromWords(
      Map<String, List<AppFulfillment>> allActions, String[] inputWords) {
    // Has to be tree map since we wanna a ordered score from low to high.
    TreeMap<Integer, List<String>> scoresMap = new TreeMap<>();
    // Iterate over all actions to find the most likely one.
    for (Map.Entry<String, List<AppFulfillment>> entry : allActions.entrySet()) {
      String actionName = entry.getKey();
      String[] tobeMatched = actionNameWords.get(actionName);
      if (tobeMatched == null || tobeMatched.length == 0) {
        continue;
      }
      // Compute score to get a priority of this action.
      int score = matchScore(tobeMatched, inputWords);
      if (scoresMap.get(score) == null) {
        scoresMap.put(score, new ArrayList<String>());
      }
      scoresMap.get(score).add(actionName);
    }
    List<String> matchedActions = scoresMap.lastEntry().getValue();
    // Keep the map order as insertion order
    Map<String, List<AppFulfillment>> matchedMaps = new LinkedHashMap<>();
    for (String matched : matchedActions) {
      matchedMaps.put(matched, allActions.get(matched));
    }
    return matchedMaps;
  }

  /**
   * @param tobeMatched actions array
   * @param inputWords input words array
   * @return a matched "score" by computing matched strings between two arrays
   */
  private int matchScore(String[] tobeMatched, String[] inputWords) {
    int number = 0;
    // Since the word order may be reversed, e.g. "money transfer" and "transfer money",
    // we have to iterate all the combinations.
    for (String text : inputWords) {
      for (String pattern : tobeMatched) {
        if (text.contains(pattern)) {
          number++;
          break;
        }
      }
    }
    return number;
  }

  /**
   * @param appAction an app action to be parsed
   * @return Construct an data map from single AppAction.
   */
  private Map<String, List<AppFulfillment>> parseActionsFromApp(AppAction appAction) {
    Map<String, List<AppFulfillment>> resultMap = new TreeMap<>(comparator);
    for (Action action : appAction.getActionsList()) {
      String intentName = action.getIntentName();
      resultMap.put(intentName, new ArrayList<AppFulfillment>());
      for (FulfillmentOption fulfillmentOption : action.getFulfillmentOptionList()) {
        resultMap.get(intentName).add(new AppFulfillment(appAction, action, fulfillmentOption));
      }
    }
    return resultMap;
  }

  /**
   * @param displayMap parse null to indicate default data
   * Change the content of expandable list view by passing some data.
   */
  private void updateView(Map<String, List<AppFulfillment>> displayMap) {
    // Change the content of expandable list view to the default intentMap with all actions.
    if(displayMap == null)
      displayMap = defaultMap;
    actionNames.clear();
    intentMap.clear();
    actionNames.addAll(displayMap.keySet());
    intentMap.putAll(displayMap);
    adapter.notifyDataSetChanged();
    expandableListView.expandGroup(0);
  }

  /**
   * @param words input sentence
   * @return found app name or ""
   * Check if words contain any known app names.
   */
  private String checkApp(String[] words) {
    Set<String> names = appNameMap.keySet();
    for (String str : words) {
      if (names.contains(str)) {
        return str;
      }
    }
    return "";
  }

  @RequiresApi(api = Build.VERSION_CODES.P)
  private void classifyText(final String text) {
    Log.d(TAG, "classify: " + text);
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                try {
                  TextClassificationManager textClassificationManager =
                      (TextClassificationManager)
                          getSystemService(Context.TEXT_CLASSIFICATION_SERVICE);
                  TextClassifier textClassifier = textClassificationManager.getTextClassifier();
                  TextClassification textClassification =
                      textClassifier.classifyText(
                          text, 0, text.length() - 1, LocaleList.getDefault());
                  classifyHandler.sendEmptyMessage(UPDATE);
                } catch (Exception e) {
                  classifyHandler.sendEmptyMessage(ERROR);
                }
              }
            })
        .start();
  }

  protected void initData() {
    // Use tree map so that the actions are sorted.
    // The total actions wouldn't be much so it wouldn't lose much time compared to hash map.
    intentMap = new TreeMap<>(comparator);
    defaultMap = new TreeMap<>(comparator);
    extractActions();
    classifyHandler =
        new Handler(Looper.getMainLooper()) {
          @Override
          public void handleMessage(@NonNull Message msg) {
            if (msg.what == UPDATE) {
            } else if (msg.what == ERROR) {
              Log.d(TAG, getString(R.string.unknown_texts));
            }
          }
        };
  }

  protected void initView() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    displayDeepLinks();
  }

  /** Generate a tree map from current built-in data, tree map is used since we want sorted actions. */
  private void extractActions() {
    // Iterate over the whole list to get the numbers and construct two maps we need.
    for (AppAction appAction : AppActionsGenerator.appActions) {
      String appName =
          Utils.getAppNameByPackageName(DeepLinkListActivity.this, appAction.getPackageName());
      for (Action action : appAction.getActionsList()) {
        // Ignore the uppercase
        appNameMap.put(appName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(), appAction);
        String actionName = action.getIntentName();
        for (FulfillmentOption fulfillmentOption : action.getFulfillmentOptionList()) {
          // The Slice options could not be counted as deep links.
          if (fulfillmentOption.getFulfillmentMode() == FulfillmentOption.FulfillmentMode.SLICE) {
            continue;
          }
          if (defaultMap.get(actionName) == null) {
            decompose(actionName);
            defaultMap.put(actionName, new ArrayList<AppFulfillment>());
          }
          defaultMap.get(actionName).add(new AppFulfillment(appAction, action, fulfillmentOption));
        }
      }
    }
    intentMap.putAll(defaultMap);
    actionNames.addAll(defaultMap.keySet());
  }

  /** @param actionName split action name into string array*/
  private void decompose(String actionName) {
    if (actionName.isEmpty() || !actionName.startsWith(Constant.INTENT_PREFIX)) return;
    // eg: actions.intent.OPEN_APP_FEATURE, we only need the last part
    String realActionName = actionName.substring(Constant.INTENT_PREFIX.length());
    // eg: real action name is OPEN_APP_FEATURE
    actionNameWords.put(actionName, realActionName.toLowerCase().split("_"));
  }

  /** Parse data and pass it to the expandable list view. */
  private void displayDeepLinks() {
    adapter = new ExpandableAdapter(this, intentMap, actionNames);
    expandableListView = findViewById(R.id.expandable_list);
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
            // Otherwise, jump to corresponding app.
            AppFulfillment appFulfillment =
                intentMap.get(actionNames.get(groupPosition)).get(childPosition);
            Utils.jumpByType(
                DeepLinkListActivity.this,
                appFulfillment.appAction,
                appFulfillment.action,
                appFulfillment.fulfillmentOption);
            return false;
          }
        });
  }

  public Map<String, List<AppFulfillment>> getIntentMap() {
    return intentMap;
  }
}
