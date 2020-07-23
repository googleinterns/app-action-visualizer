package com.example.appactionvisualizer.databean;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestGenerator {
  private static final String TAG = "TestGenerator";
  private static TestGenerator single_instance = null;

  public static List<AppAction> appActions = new ArrayList<>();
  public static List<AppAction> appActionsUnique = new ArrayList<>();
  public static Map<ActionType, List<AppAction>> type2appActionList = new HashMap<>();


  public static TestGenerator getInstance() {
    if (single_instance == null)
      single_instance = new TestGenerator();
    return single_instance;
  }

  public void readFromFile(Context context) {
    InputStream is = context.getResources().openRawResource(R.raw.protobufbinary);
    try {
      appActions.addAll(AppActionProtos.AppActions.parseFrom(is).getAppActionsList());
      int sz = appActions.size();
      int duplicate = 0;
      for(int i = 0; i < sz; ++i) {
        boolean isUnique = true;
        for(int j = i + 1; j < sz; ++j) {
          if(appActions.get(j).getPackageName().equals(appActions.get(i).getPackageName())) {
            isUnique = false;
//            Log.d(TAG, appActions.get(i).getPackageName());
            duplicate++;
          }
        }
        if(isUnique) {
          appActionsUnique.add(appActions.get(i));
        }
      }
      int allFulfillmentSize = 0;
      int zeroParameter = 0, singleParameter = 0, multiParameter = 0;
      int isCoordinates = 0;
      Iterator<AppActionProtos.AppAction> it = appActionsUnique.iterator();
      while(it.hasNext()) {
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
                if(urlTemplate.getTemplate().contains("lat")) {
                  isCoordinates++;
                }
                multiParameter++;
              }else {
                singleParameter++;
              }
            } else {
              zeroParameter++;
            }
          }
        }
      }

      Log.d(TAG, "duplicate: " + duplicate);
      Log.d(TAG,  "allsize = " + allFulfillmentSize);
      Log.d(TAG, "multiParameter = " + multiParameter + "...singleParameter = " + singleParameter + "...zeroParameter =" + zeroParameter);
      Log.d(TAG, "isCoordinates = " + isCoordinates);
    } catch (IOException e) {
      Log.d(TAG, "error");
      e.printStackTrace();
    }
    parseDataToEachType(appActionsUnique);
  }


  private void parseDataToEachType(List<AppActionProtos.AppAction> appActions) {

    //set up each fragments' data list, make sure there's no duplicate data in one action type
    Map<ActionType, Set<AppAction>> appActionUnique = new HashMap<>();
    for(AppAction app : appActions) {
      for(AppActionProtos.Action action : app.getActionsList()) {
        if(appActionUnique.get(ActionType.getActionTypeByName(action.getIntentName())) == null) {
          appActionUnique.put(ActionType.getActionTypeByName(action.getIntentName()), new HashSet<AppAction>());
        }
        appActionUnique.get(ActionType.getActionTypeByName(action.getIntentName())).add(app);
      }
    }
    //in case there're some types haven't been initialized
    for (ActionType actiontype: ActionType.values()) {
      if(type2appActionList.get(actiontype) == null) {
        type2appActionList.put(actiontype, new ArrayList<AppAction>());
      }
    }
    for(Map.Entry<ActionType, Set<AppAction>> entry : appActionUnique.entrySet()) {
      type2appActionList.get(entry.getKey()).addAll(entry.getValue());
    }
  }
}
