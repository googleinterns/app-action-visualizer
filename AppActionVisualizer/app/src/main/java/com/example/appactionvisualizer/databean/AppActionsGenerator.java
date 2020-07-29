package com.example.appactionvisualizer.databean;

import android.content.Context;
import android.util.Log;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
      List<AppAction> appActions = new ArrayList<>();
      appActions.addAll(AppActionProtos.AppActions.parseFrom(is).getAppActionsList());
      int sz = appActions.size();
      int duplicate = 0;
      for (int i = 0; i < sz; ++i) {
        boolean isUnique = true;
        for (int j = i + 1; j < sz; ++j) {
          if (appActions.get(j).getPackageName().equals(appActions.get(i).getPackageName())) {
            isUnique = false;
            duplicate++;
          }
        }
        if (isUnique) {
          AppActionsGenerator.appActions.add(appActions.get(i));
        }
      }
      int allFulfillmentSize = 0;
      int zeroParameter = 0, singleParameter = 0, multiParameter = 0;
      int isCoordinates = 0;
      Iterator<AppActionProtos.AppAction> it = AppActionsGenerator.appActions.iterator();
      while (it.hasNext()) {
        AppActionProtos.AppAction appAction = it.next();
        if (appAction.getPackageName().equals("com.gojuno.rider") || appAction.getPackageName().equals("com.kimfrank.android.fitactions") ||
            appAction.getPackageName().equals("com.deeplocal.smores") || appAction.getPackageName().equals("com.runtastic.android.pro2")
        ) {
          it.remove();
          continue;
        }
        for (AppActionProtos.Action action : appAction.getActionsList()) {
          allFulfillmentSize += action.getFulfillmentOptionCount();
          for (AppActionProtos.FulfillmentOption fulfillmentOption : action.getFulfillmentOptionList()) {
            AppActionProtos.UrlTemplate urlTemplate = fulfillmentOption.getUrlTemplate();
            if (urlTemplate.getParameterMapCount() > 0) {
              if (urlTemplate.getParameterMapCount() > 1) {
                if (urlTemplate.getTemplate().contains("lat")) {
                  isCoordinates++;
                }
                multiParameter++;
              } else {
                singleParameter++;
              }
            } else {
              zeroParameter++;
            }
          }
        }
      }

      Log.d(TAG, "duplicate: " + duplicate);
      Log.d(TAG, "allsize = " + allFulfillmentSize);
      Log.d(TAG, "multiParameter = " + multiParameter + "...singleParameter = " + singleParameter + "...zeroParameter =" + zeroParameter);
      Log.d(TAG, "isCoordinates = " + isCoordinates);
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
