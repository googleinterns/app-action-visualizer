package com.example.appactionvisualizer.ui.activity.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
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
import androidx.core.app.ActivityCompat;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.ActionType;
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.EntitySet;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.databean.AppActionProtos.UrlTemplate;
import com.example.appactionvisualizer.databean.AppActionsGenerator;
import com.example.appactionvisualizer.databean.AppFulfillment;
import com.example.appactionvisualizer.ui.activity.CustomActivity;
import com.example.appactionvisualizer.ui.activity.parameter.InputParameterActivity;
import com.example.appactionvisualizer.ui.adapter.ExpandableAdapter;
import com.example.appactionvisualizer.utils.StringUtils;
import com.example.appactionvisualizer.utils.AppUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.protobuf.ListValue;
import com.google.protobuf.Value;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.example.appactionvisualizer.constants.Constant.ENTITY_URL;
import static com.example.appactionvisualizer.constants.Constant.INTENT_PREFIX;
import static com.example.appactionvisualizer.constants.Constant.UNDERLINE;
import static com.example.appactionvisualizer.constants.Constant.URL_PARAMETER_INDICATOR;
import static com.example.appactionvisualizer.constants.Constant.WHITESPACE;

// Display deep links using an expandable list view.
public class DeepLinkListActivity extends CustomActivity {
  private static final String TAG = DeepLinkListActivity.class.getSimpleName();
  // These bits are used to indicate classify results.
  private static final int PICK_UP_ADDRESS = 1, DROP_OFF_ADDRESS = 2, REQUEST_LOCATION = 0x100, ERROR = -1;
  // Group titles are action names.
  final List<String> actionNames = new ArrayList<>();
  // Comparator used by tree map.
  Comparator<String> comparator =
      new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
          ActionType type1 = ActionType.getActionTypeByName(s1);
          ActionType type2 = ActionType.getActionTypeByName(s2);
          // Make sure actions with same type stay together.
          if (!type1.equals(type2)) {
            return type1.compareTo(type2);
          }
          return s1.compareTo(s2);
        }
      };
  // For each action name, we need a structure of <AppAction, Action, FulfillmentOption> data
  // so that the link can jump into ParameterActivity and parse required data to activity.
  private Map<String, List<AppFulfillment>> intentMap, defaultMap;
  // Map app name to app action
  private Map<String, AppAction> appNameMap = new HashMap<>();
  // Map action name to a list of words
  // eg: <"actions.intent.OPEN_APP_FEATURE", ["open", "app", "feature"]>
  private Map<String, String[]> actionNameWords = new HashMap<>();
  private BaseExpandableListAdapter adapter;
  private ExpandableListView expandableListView;
  // In order to match with an app name, the word must score at least 0.7
  private static final double MINIMUM_SCORE = 0.7;
  // Used to get current location.
  protected FusedLocationProviderClient fusedLocationClient;
  // Taxi Reservation variables
  // All the app fulfillment of taxi reservation. Cached it in memory so we can get corresponding
  // item quickly when needed.
  private Map<String, List<AppFulfillment>> taxiReservation = new HashMap<>();
  // Coordinates variables
  private double pickUpLatitude = -1, pickupLongitude = -1;
  private double dropOffLatitude = -1, dropOffLongitude = -1;

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
            // When user hit enter button on keyboard, do the recommendation for the text.
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
              String text = userInput.getText().toString().toLowerCase();
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
   * Make a recommendation based on user inputs
   *
   * @param input user input sentence
   */
  protected void recommend(final String input) {
    // Use text classifier to check if it is an address.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      classifyText(input);
    }else {
      updateView(getDisplayMap(input));
    }
  }

  /**
   * @param input user input sentence
   * @return <key: action Name, value: list of deep link data> data to be displayed
   */
  protected Map<String, List<AppFulfillment>> getDisplayMap(final String input) {
    // Ignore invalid inputs, only accept a-z A-z 0-9 and whitespace
    String sentence = input.replaceAll("[^a-zA-Z0-9\\s]", "");
    if (sentence.trim().isEmpty()) {
      return new HashMap<>();
    }
    // Split the sentence using space(similar to tokenization)
    String[] words = sentence.toLowerCase().split(WHITESPACE);
    // Words index - app name. The word and app name may differ so we need both.
    Pair<Integer, String> pair = checkApp(words);
    String appName = pair.second;
    // Detect no app name, try to match all actions
    if (appName.isEmpty()) {
      // Try to match all inventory entity sets' item name.
      TreeMap<Integer, List<AppFulfillment>> allInventoryScore = getDeepLinkFromAllInventory(sentence);
      // If only one score, that must be zero. And zero score can not be used as reference to an inventory.
      if (allInventoryScore != null && allInventoryScore.size() > 1)
        return getMatchedFromScore(allInventoryScore);
      // If inventory could not be matched, try match actions from all actions.
      return getActionFromWords(defaultMap, words);
    }
    sentence = removeAppName(words, pair.first /* index of the app name in sentence */);
    // There's an app name in this sentence, we can read all the actions of this specific app.
    // Returned appAction wouldn't be null since app name is detected from appNameMap's keySet
    AppAction appAction = appNameMap.get(appName);
    Map<String, List<AppFulfillment>> singleAppMap = parseActionsFromApp(appAction);
    // If there's only app name return the result
    if (sentence.isEmpty()) {
      return singleAppMap;
    }

    // Try to match some inline inventory items using user input
    // This will generate one or some deep links
    TreeMap<Integer, List<AppFulfillment>> scoreMap = getDeepLinkFromInventory(appAction, sentence);
    if (scoreMap != null && !scoreMap.isEmpty()) {
      return getMatchedFromScore(scoreMap);
    }
    // There's no need to parse other words since the app has only one action.
    if (appAction.getActionsCount() == 1) {
      return singleAppMap;
    }
    // Match some action name of this app using user input
    // This will generate most likely actions, which may include several deep links
    return getActionFromWords(singleAppMap, sentence.split(WHITESPACE));
  }

  /**
   * Remove app name from sentence in case it interferes with other parse stage
   *
   * @param words user input words
   * @param appIdx app name's index
   * @return user sentence without app name
   */
  private String removeAppName(String[] words, Integer appIdx) {
    if (words.length == 1) return "";
    StringBuilder sbSentence = new StringBuilder();
    for (int idx = 0; idx < words.length; ++idx) {
      if (idx == appIdx) {
        continue;
      }
      sbSentence.append(words[idx]).append(WHITESPACE);
    }
    // We don't need the last white space
    return sbSentence.substring(0, sbSentence.length() - 1);
  }

  /**
   * Try to get specific deep links using all apps' inline inventory: either from a parameter's
   * inventory or from url inventory.
   *
   * @param sentence user input sentence
   * @return the found data or null if not found <key: score, value: list of deep link data>
   */
  protected TreeMap<Integer, List<AppFulfillment>> getDeepLinkFromAllInventory(String sentence) {
    TreeMap<Integer, List<AppFulfillment>> result = new TreeMap<>();
    for (AppAction appAction : AppActionsGenerator.appActions) {
      Map<Integer, List<AppFulfillment>> appDeepLinks = getDeepLinkFromInventory(appAction, sentence);
      if (appDeepLinks != null && !appDeepLinks.isEmpty()) {
        // Append each list of deep links to corresponding action name
        for (Map.Entry<Integer, List<AppFulfillment>> entry : appDeepLinks.entrySet()) {
          int score = entry.getKey();
          if (result.get(score) == null) {
            result.put(score, new ArrayList<AppFulfillment>());
          }
          result.get(score).addAll(entry.getValue());
        }
      }
    }
    return result;
  }

  /**
   * Try to get specific deep links using a specific app's inline inventory: either from a
   * parameter's inventory or from url inventory.
   *
   * @param appAction matched app
   * @param sentence user input sentence
   * @return the found data or null if not found <key: score, value: list of deep link data>
   */
  private TreeMap<Integer, List<AppFulfillment>> getDeepLinkFromInventory(
      AppAction appAction, String sentence) {
    for (Action action : appAction.getActionsList()) {
      if (action.getParametersCount() == 0) continue;
      for (FulfillmentOption fulfillmentOption : action.getFulfillmentOptionList()) {
        UrlTemplate urlTemplate = fulfillmentOption.getUrlTemplate();
        // Deal with {@url} case
        if (urlTemplate.getTemplate().contains(Constant.URL_NO_LINK)) {
          return checkUrlEntity(appAction, action, fulfillmentOption, sentence);
        }
        // Currently match only single parameter case!
        // todo: For more parameters
        if (urlTemplate.getParameterMapCount() == 1) {
          // Get the key to be matched
          String key = urlTemplate.getParameterMapMap().keySet().iterator().next();
          EntitySet entitySet =
              InputParameterActivity.checkEntitySet(key, appAction, action, fulfillmentOption);
          return checkParameterEntity(
              appAction, action, fulfillmentOption, key, entitySet, sentence);
        }
      }
    }
    return null;
  }

  /**
   * Try to get specific deep links from a parameter key using inline inventory.
   *
   * @param appAction app action of the deep link
   * @param action action of the deep link
   * @param fulfillmentOption fulfillmentOption of the deep link
   * @param key Specific parameter key name
   * @param entitySet inline inventory of the key
   * @param sentence user input sentence
   * @return <key: score, value: list of deep link data> recommended deep links or null if not found
   *     key
   */
  private TreeMap<Integer, List<AppFulfillment>> checkParameterEntity(
      AppAction appAction,
      Action action,
      FulfillmentOption fulfillmentOption,
      String key,
      EntitySet entitySet,
      String sentence) {
    if (entitySet == null) return null;
    final ListValue entityItemValue =
        entitySet.getItemList().getFieldsOrThrow(Constant.ENTITY_ITEM_LIST).getListValue();
    // Assign a score to each recommended deeplink, return the deeplink with the maximum score
    TreeMap<Integer, List<AppFulfillment>> scoreMap = new TreeMap<>();
    // For each entity, compute matched score with our sentence.
    for (Value entity : entityItemValue.getValuesList()) {
      Value identifier = entity.getStructValue().getFieldsOrThrow(Constant.ENTITY_FIELD_IDENTIFIER);
      String name =
          entity
              .getStructValue()
              .getFieldsOrDefault(Constant.ENTITY_FIELD_NAME, identifier)
              .getStringValue()
              .toLowerCase();
      int score = StringUtils.matchScore(name.split(WHITESPACE), sentence.split(WHITESPACE));
      String urlTemplate = fulfillmentOption.getUrlTemplate().getTemplate();
      String curUrl =
          StringUtils.replaceSingleParameter(
              this, urlTemplate, key, identifier.getStringValue());
      UrlTemplate builtUrl = UrlTemplate.newBuilder().setTemplate(curUrl).build();
      FulfillmentOption builtFulfillment =
          FulfillmentOption.newBuilder().setUrlTemplate(builtUrl).build();
      if (scoreMap.get(score) == null) scoreMap.put(score, new ArrayList<AppFulfillment>());
      scoreMap.get(score).add(new AppFulfillment(appAction, action, builtFulfillment));
    }
    return scoreMap;
  }

  /**
   * Try to get specific deep links from url inventory.
   *
   * @param appAction app action of the deep link
   * @param action action of the deep link
   * @param fulfillmentOption fulfillmentOption of the deep link
   * @param sentence user input sentence
   * @return <key: score, value: list of deep link data> recommended deep links or null if not found
   *     url entity
   */
  private TreeMap<Integer, List<AppFulfillment>> checkUrlEntity(
      AppAction appAction, Action action, FulfillmentOption fulfillmentOption, String sentence) {
    if (fulfillmentOption.getUrlTemplate().getParameterMapCount() > 0
        || !action.getParameters(0).getEntitySetReference(0).getUrlFilter().isEmpty()) {
      return null;
    }
    final EntitySet entitySet = AppUtils.checkUrlEntitySet(appAction, action);
    if (entitySet == null) {
      return null;
    }
    // Assign a score to each recommended deeplink, return the deeplink with the maximum score
    TreeMap<Integer, List<AppFulfillment>> scoreMap = new TreeMap<>();
    // For all the url values, app action would provide a corresponding name. Find if the sentence
    // contains any of these name and this url is our recommended one.
    final ListValue listValue =
        entitySet.getItemList().getFieldsOrThrow(Constant.ENTITY_ITEM_LIST).getListValue();
    // Get the list contents and try to match input sentence with one of the struct value's name
    for (Value entity : listValue.getValuesList()) {
      String name =
          entity.getStructValue().getFieldsOrThrow(Constant.ENTITY_FIELD_NAME).getStringValue();
      int score = StringUtils.matchScore(name.split(WHITESPACE), sentence.split(WHITESPACE));
      // Build a fulfillment containing the found url
      UrlTemplate builtUrl =
          UrlTemplate.newBuilder()
              .setTemplate(entity.getStructValue().getFieldsOrThrow(ENTITY_URL).getStringValue())
              .build();
      FulfillmentOption builtFulfillment =
          FulfillmentOption.newBuilder().setUrlTemplate(builtUrl).build();
      if (scoreMap.get(score) == null) {
        scoreMap.put(score, new ArrayList<AppFulfillment>());
      }
      scoreMap.get(score).add(new AppFulfillment(appAction, action, builtFulfillment));
    }
    return scoreMap;
  }

  /**
   * @param scoreMap score map of each deep link
   * @return recommended result map
   */
  private Map<String, List<AppFulfillment>> getMatchedFromScore(
      TreeMap<Integer, List<AppFulfillment>> scoreMap) {
    // We only need the last entry which has the max score
    Map.Entry<Integer, List<AppFulfillment>> entry = scoreMap.lastEntry();
    // If score is only 1, we don't need this entry
    if (entry == null || entry.getKey() <= 1) {
      return new HashMap<>();
    }
    Map<String, List<AppFulfillment>> matched = new HashMap<>();
    for (AppFulfillment appFulfillment : entry.getValue()) {
      String actionName = appFulfillment.action.getIntentName();
      if (matched.get(actionName) == null) {
        matched.put(actionName, new ArrayList<AppFulfillment>());
      }
      matched.get(actionName).add(appFulfillment);
    }
    return matched;
  }

  /**
   * @param allActions The actions pool to be selected, key is the action name.
   * @param inputWords all the words of user input.
   * @return the most likely action(s) among all actions.
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
      int score = StringUtils.matchScore(tobeMatched, inputWords);
      if (score == 0)
        continue;
      if (scoresMap.get(score) == null) {
        scoresMap.put(score, new ArrayList<String>());
      }
      scoresMap.get(score).add(actionName);
    }
    if (scoresMap.isEmpty()) {
      return new HashMap<>();
    }
    List<String> matchedActions = scoresMap.lastEntry().getValue();
    // Keep the map order as insertion order
    Map<String, List<AppFulfillment>> matched = new HashMap<>();
    for (String actionName : matchedActions) {
      matched.put(actionName, allActions.get(actionName));
    }
    return matched;
  }

  /**
   * @param appAction an app action to be parsed
   * @return <key: action Name, value: list of deep link data> Construct an data map from single
   *     AppAction.
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
   * Update expandable list view by passing some data. Parse null to indicate default data.
   *
   * @param displayMap the content of expandable list view.
   */
  private void updateView(Map<String, List<AppFulfillment>> displayMap) {
    // If no recommended data, display the default data.
    boolean unfold = true;
    if (displayMap == null || displayMap.isEmpty()) {
      displayMap = defaultMap;
      unfold = false;
    }
    actionNames.clear();
    intentMap.clear();
    actionNames.addAll(displayMap.keySet());
    intentMap.putAll(displayMap);
    adapter.notifyDataSetChanged();
    // Only unfold the first group when having recommended data
    if (unfold) {
      expandableListView.expandGroup(0);
    } else {
      expandableListView.collapseGroup(0);
    }
  }

  /**
   * Check if words match any known app names using fuzzy match.
   *
   * @param words input sentence
   * @return found app name or ""
   */
  protected Pair<Integer, String> checkApp(String[] words) {
    Set<String> names = appNameMap.keySet();
    double score = 0;
    int appIdx = 0;
    String maxAppName = "";
    for (int idx = 0; idx < words.length; ++idx) {
      String str = words[idx];
      for (String appName : names) {
        double currentScore = (double) StringUtils.getScore(str, appName) / appName.length();
        if (currentScore > score) {
          score = currentScore;
          maxAppName = appName;
          appIdx = idx;
        }
      }
    }
    if (score < MINIMUM_SCORE) {
      return new Pair<>(-1, "");
    }
    return new Pair<>(appIdx, maxAppName);
  }

  protected void initData() {
    // Use tree map so that the actions are sorted.
    // The total actions wouldn't be much so it wouldn't lose much time compared to hash map.
    intentMap = new TreeMap<>(comparator);
    defaultMap = new TreeMap<>(comparator);
    extractActions();
  }

  protected void initView() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    displayDeepLinks();
  }

  /**
   * Generate a <key: action name, value: list of AppFulfillment> tree map from current data, need
   * tree map since we want sorted actions.
   */
  private void extractActions() {
    // Iterate over the whole list to get the numbers and construct two maps we need.
    for (AppAction appAction : AppActionsGenerator.appActions) {
      String appName =
          AppUtils.getAppNameByPackageName(DeepLinkListActivity.this, appAction.getPackageName())
              .replaceAll("[^a-zA-Z0-9]", "")
              .toLowerCase();
      for (Action action : appAction.getActionsList()) {
        // Ignore the uppercase
        appNameMap.put(appName, appAction);
        String actionName = action.getIntentName();
        // taxiReservation map contains all reservation fulfillment and can be used later
        if(actionName.equals(getString(R.string.create_taxi))) {
          if (taxiReservation.get(actionName) == null) {
            taxiReservation.put(actionName, new ArrayList<AppFulfillment>());
          }
          taxiReservation.get(actionName).add(new AppFulfillment(appAction, action, action.getFulfillmentOption(0)));
        }
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

  /**
   * split action name into string array
   *
   * @param actionName
   */
  private void decompose(String actionName) {
    if (actionName.isEmpty() || !actionName.startsWith(INTENT_PREFIX)) return;
    // eg: actions.intent.OPEN_APP_FEATURE, we only need the last part
    String realActionName = actionName.substring(INTENT_PREFIX.length());
    // eg: real action name is OPEN_APP_FEATURE
    actionNameWords.put(actionName, realActionName.toLowerCase().split(UNDERLINE));
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
            AppUtils.jumpByType(
                DeepLinkListActivity.this,
                appFulfillment.appAction,
                appFulfillment.action,
                appFulfillment.fulfillmentOption);
            return false;
          }
        });
  }

  // Test Use
  protected Map<String, List<AppFulfillment>> getIntentMap() {
    return intentMap;
  }

  // handler to handle the data from geocoder and fusedLocationClient
  private Handler addressHandler =
      new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
          if (msg.what == PICK_UP_ADDRESS) {
            Bundle pickUp = msg.getData();
            pickUpLatitude = pickUp.getDouble(Constant.PICK_UP_LATITUDE, -1);
            pickupLongitude = pickUp.getDouble(Constant.PICK_UP_LONGITUDE, -1);
            // Update the view if dropOff coordinates have been calculated.
            if (dropOffLatitude != -1) {
              updateView(getTaxiReservationMap(
                  pickUpLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude));
            }
          } else if (msg.what == DROP_OFF_ADDRESS) {
            Bundle dropOff = msg.getData();
            dropOffLatitude = dropOff.getDouble(Constant.DROP_OFF_LATITUDE, -1);
            dropOffLongitude = dropOff.getDouble(Constant.DROP_OFF_LONGITUDE, -1);
            // Update the view if pickUp coordinates have been calculated.
            if (pickUpLatitude != -1) {
              updateView(getTaxiReservationMap(
                  pickUpLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude));
            }
          } else if (msg.what == ERROR) {
            Log.d(TAG, getString(R.string.unknown_texts));
          }
        }
      };

  // Create app fulfillment list according to the coordinates and update the view.
  protected Map<String, List<AppFulfillment>> getTaxiReservationMap(
      double pickUpLatitude,
      double pickupLongitude,
      double dropOffLatitude,
      double dropOffLongitude) {
    Map<String, List<AppFulfillment>> taxiMap = new HashMap<>();
    taxiMap.put(getString(R.string.create_taxi), new ArrayList<AppFulfillment>());
    List<AppFulfillment> appFulfillmentList = taxiReservation.get(getString(R.string.create_taxi));
    if (appFulfillmentList == null) {
      return taxiMap;
    }
    for (AppFulfillment appFulfillment : appFulfillmentList) {
      List<String> parameters = new ArrayList<>();
      FulfillmentOption fulfillmentOption = appFulfillment.fulfillmentOption;
      for (Map.Entry<String, String> entry :
          fulfillmentOption.getUrlTemplate().getParameterMapMap().entrySet()) {
        AppUtils.addLocationParameters(
            this,
            entry,
            parameters,
            Double.toString(pickUpLatitude),
            Double.toString(pickupLongitude),
            Double.toString(dropOffLatitude),
            Double.toString(dropOffLongitude));
      }
      String urlTemplate = fulfillmentOption.getUrlTemplate().getTemplate();
      int idx = urlTemplate.indexOf(URL_PARAMETER_INDICATOR);
      StringBuilder url = new StringBuilder();
      url.append(urlTemplate.substring(0, idx)).append(urlTemplate.charAt(idx + 1));
      url.append(TextUtils.join("&", parameters));
      Log.d(TAG, url.toString());
      UrlTemplate builtUrl = UrlTemplate.newBuilder().setTemplate(url.toString()).build();
      FulfillmentOption builtFulfillment =
          FulfillmentOption.newBuilder().setUrlTemplate(builtUrl).build();
      taxiMap
          .get(getString(R.string.create_taxi))
          .add(
              new AppFulfillment(
                  appFulfillment.appAction, appFulfillment.action, builtFulfillment));
    }
    return taxiMap;
  }

  /**
   * Using textClassifier to identify if the text is an address
   *
   * @param text user input sentence
   */
  @RequiresApi(api = Build.VERSION_CODES.P)
  private void classifyText(final String text) {
    Log.d(TAG, "isAddress: " + text);
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
                      text, 0/* Start index */, text.length() - 1/* End index, beware out of bound */, LocaleList.getDefault());
              String entity = textClassification.getEntity(0);
              Log.d(TAG, entity);
              if (entity.equals(TextClassifier.TYPE_ADDRESS)) {
                resetCoordinates();
                getPickUpLocation();
                getDropOffLocation(text);
              }else {
                updateView(getDisplayMap(text));
              }
            } catch (Exception e) {
              addressHandler.sendEmptyMessage(ERROR);
            }
          }
        })
        .start();
  }


  protected void resetCoordinates() {
    pickUpLatitude = pickupLongitude = dropOffLongitude = dropOffLatitude = -1;
  }

  // Get pick up location, which is user's current location , need to request permission first.
  protected void getPickUpLocation() {
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
      // request for permission
      ActivityCompat.requestPermissions(
          this,
          new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
          },
          REQUEST_LOCATION);
    } else {
      // Permission granted
      requestLocationByFusedLocationProvideder();
    }
  }

  /**
   * Get drop off location coordinates using geocoder,
   * pass result to handler to update the view
   *
   * @param text address text
   */
  private void getDropOffLocation(final String text) {
    final Geocoder geocoder = new Geocoder(DeepLinkListActivity.this, Locale.US);
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Address address = geocoder.getFromLocationName(text, 1).get(0);
          Message message = new Message();
          message.what = DROP_OFF_ADDRESS;
          Bundle bundle = new Bundle();
          bundle.putDouble(Constant.DROP_OFF_LATITUDE, address.getLatitude());
          bundle.putDouble(Constant.DROP_OFF_LONGITUDE, address.getLongitude());
          message.setData(bundle);
          addressHandler.sendMessage(message);
        } catch (Exception e) {
          addressHandler.sendEmptyMessage(ERROR);
        }
      }
    }).start();
  }

  // Request last location, which is our current location, do not need to worry about permission
  // issues since already granted.
  private void requestLocationByFusedLocationProvideder() {
    fusedLocationClient
        .getLastLocation()
        .addOnSuccessListener(
            this,
            new OnSuccessListener<Location>() {
              @Override
              public void onSuccess(Location location) {
                if (location != null) {
                  Message message = new Message();
                  message.what = PICK_UP_ADDRESS;
                  Bundle bundle = new Bundle();
                  bundle.putDouble(Constant.PICK_UP_LATITUDE, location.getLatitude());
                  bundle.putDouble(Constant.PICK_UP_LONGITUDE, location.getLongitude());
                  message.setData(bundle);
                  addressHandler.sendMessage(message);
                }
              }
            });
  }

  /**
   * Callback for the result from requesting permissions. This method
   * is invoked for every call on {@link #requestPermissions(String[], int)}.
   * <p>
   * <strong>Note:</strong> It is possible that the permissions request interaction
   * with the user is interrupted. In this case you will receive empty permissions
   * and results arrays which should be treated as a cancellation.
   * </p>
   *
   * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
   * @param permissions  The requested permissions. Never null.
   * @param grantResults The grant results for the corresponding permissions
   *                     which is either {@link PackageManager#PERMISSION_GRANTED}
   *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
   * @see #requestPermissions(String[], int)
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case REQUEST_LOCATION:
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          requestLocationByFusedLocationProvideder();
        }
    }
  }

}
