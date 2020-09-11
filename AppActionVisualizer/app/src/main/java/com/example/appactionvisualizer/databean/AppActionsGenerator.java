package com.example.appactionvisualizer.databean;

import android.content.Context;
import android.util.Log;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.utils.AppUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppActionsGenerator {
  private static final String TAG = "AppActionsGenerator";
  public static List<AppAction> appActions = new ArrayList<>();
  public static Map<ActionType, List<AppAction>> type2appActionList = new HashMap<>();
  private static AppActionsGenerator single_instance = null;

  public static AppActionsGenerator getInstance() {
    if (single_instance == null)  {
      single_instance = new AppActionsGenerator();
    }
    return single_instance;
  }

  // Parse the data using protobuf api
  public void readFromFile(Context context) {
    if (!appActions.isEmpty()) {
      Log.d(TAG,context.getString(R.string.loaded));
      return;
    }
    InputStream is = context.getResources().openRawResource(R.raw.protobufbinary);
    try {
      appActions.addAll(AppActionProtos.AppActions.parseFrom(is).getAppActionsList());
      appActions = deduplication(appActions);
    } catch (IOException e) {
      e.printStackTrace();
    }
    sortAppActionByName(context, appActions);
    parseDataToEachType(context, appActions);
  }

  // Sort the appName using app name
  // P.S:  Current built-in app icons and names are extracted manually.
  // So If new app action data is added, the new apps will have no icons and names if the user
  // didn't install them
  // and will be placed at the end of list.
  private void sortAppActionByName(final Context context, final List<AppAction> appActions) {
    Collections.sort(
        appActions,
        new Comparator<AppAction>() {
          /**
           * sort by the app name
           *
           * @param appAction1
           * @param appAction2
           */
          @Override
          public int compare(AppAction appAction1, AppAction appAction2) {
            return AppUtils.getAppNameByPackageName(context, appAction1.getPackageName())
                .toLowerCase()
                .compareTo(
                    AppUtils.getAppNameByPackageName(context, appAction2.getPackageName())
                        .toLowerCase());
          }
        });
  }

  // Remove app action with duplicate name from the list using hash set
  private List<AppAction> deduplication(List<AppAction> appActions) {
    int sz = appActions.size();
    // These 4 apps cannot be downloaded from app store
    Set<String> seen =
        new HashSet<>(
            Arrays.asList(
                "com.gojuno.rider",
                "com.kimfrank.android.fitactions",
                "com.deeplocal.smores",
                "com.runtastic.android.pro2"));
    List<AppAction> unique = new ArrayList<>();
    for (int idx = sz - 1; idx >= 0; --idx) {
      String name = appActions.get(idx).getPackageName();
      if (seen.contains(name)) {
        continue;
      }
      seen.add(name);
      unique.add(appActions.get(idx));
    }
    return unique;
  }

  private void parseDataToEachType(final Context context, final List<AppAction> appActions) {
    // Set up each fragments' data list, make sure there's no duplicate data in one action type
    Map<ActionType, Set<AppAction>> appActionUnique = new HashMap<>();
    for (AppAction app : appActions) {
      for (AppActionProtos.Action action : app.getActionsList()) {
        if (appActionUnique.get(ActionType.getActionTypeByName(action.getIntentName())) == null) {
          appActionUnique.put(
              ActionType.getActionTypeByName(action.getIntentName()), new HashSet<AppAction>());
        }
        appActionUnique.get(ActionType.getActionTypeByName(action.getIntentName())).add(app);
      }
    }
    // In case there're some types haven't been initialized
    for (ActionType actiontype : ActionType.values()) {
      if (type2appActionList.get(actiontype) == null) {
        type2appActionList.put(actiontype, new ArrayList<AppAction>());
      }
    }
    for (Map.Entry<ActionType, Set<AppAction>> entry : appActionUnique.entrySet()) {
      type2appActionList.get(entry.getKey()).addAll(entry.getValue());
      sortAppActionByName(context, type2appActionList.get(entry.getKey()));
    }
  }
}
