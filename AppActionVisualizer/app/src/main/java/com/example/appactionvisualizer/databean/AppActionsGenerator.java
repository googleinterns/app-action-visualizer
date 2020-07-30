package com.example.appactionvisualizer.databean;

import android.content.Context;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.utils.Utils;

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
    if (single_instance == null)
      single_instance = new AppActionsGenerator();
    return single_instance;
  }

  public void readFromFile(Context context) {
    if(!appActions.isEmpty())
      return;
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

  private void sortAppActionByName(final Context context, final List<AppAction> appActions) {
    Collections.sort(appActions, new Comparator<AppAction>() {
      @Override
      public int compare(AppAction t1, AppAction t2) {
        int strId1 = Utils.getResIdByPackageName(t1.getPackageName(), R.string.class);
        int strId2 = Utils.getResIdByPackageName(t2.getPackageName(), R.string.class);
        if(strId1 == -1) return 1;
        if(strId2 == -1) return -1;
        return context.getString(strId1).toLowerCase().compareTo(context.getString(strId2).toLowerCase());
      }
    });
  }

  private List<AppAction> deduplication(List<AppAction> appActions) {
    int sz = appActions.size();
    //these 4 apps cannot be downloaded from app store
    Set<String> seen = new HashSet<>(Arrays.asList("com.gojuno.rider", "com.kimfrank.android.fitactions", "com.deeplocal.smores", "com.runtastic.android.pro2"));
    List<AppAction> unique = new ArrayList<>();
    for (int i = sz - 1; i >= 0; --i) {
      String name = appActions.get(i).getPackageName();
      if(seen.contains(name)) {
        continue;
      }
      seen.add(name);
      unique.add(appActions.get(i));
    }
    return unique;
  }


  private void parseDataToEachType(final Context context, final List<AppAction> appActions) {

    //set up each fragments' data list, make sure there's no duplicate data in one action type
    Map<ActionType, Set<AppAction>> appActionUnique = new HashMap<>();
    for (AppAction app : appActions) {
      for (AppActionProtos.Action action : app.getActionsList()) {
        if (appActionUnique.get(ActionType.getActionTypeByName(action.getIntentName())) == null) {
          appActionUnique.put(ActionType.getActionTypeByName(action.getIntentName()), new HashSet<AppAction>());
        }
        appActionUnique.get(ActionType.getActionTypeByName(action.getIntentName())).add(app);
      }
    }
    //in case there're some types haven't been initialized
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
