package com.example.appactionvisualizer.databean;


import android.content.Context;
import android.util.Log;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.databean.protobuf.AppActionProtos;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * basic app action class
 * https://developers.google.com/assistant/app/action-schema#overview
 * implements Serializable to pass between activities
 */
public class AppAction implements Serializable {
  private static final String TAG = "APPACTION";

  private String appName;
  private String packageName;
  private List<Action> actions = new ArrayList<>();
  //todo: add entityset

  public static List<AppAction> allAppActions= new ArrayList<>();
  public static Map<ActionType, List<AppAction>> type2appActionList = new HashMap<>();


  public static void parseData() {
    //TODO: parse from file logic
    allAppActions = TestGenerator.getInstance().generateFromTest();

    //set up each fragments' data list, make sure there's no duplicate data in one action type
    Map<ActionType, Set<AppAction>> appActionUnique = new HashMap<>();
    for(AppAction app : allAppActions) {
      for(Action action : app.actions) {
        if(appActionUnique.get((action.getActionType())) == null) {
          appActionUnique.put(action.getActionType(), new HashSet<AppAction>());
        }
        appActionUnique.get(action.getActionType()).add(app);
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

  /**
   * generate a new app action
   * @return a new action with with a unique id
   */
  public static AppAction genActions(Context context) {
    //TODO: need parse from file logic
    AppAction newAction = new AppAction();
    InputStream is = context.getResources().openRawResource(R.raw.protobufbinary);
    try {
      AppActionProtos.AppActions appActions = AppActionProtos.AppActions.parseFrom(is);
      Log.d(TAG, appActions.getAppActionsCount() + ": size");
    } catch (IOException e) {
      Log.d(TAG, "error");
      e.printStackTrace();
    }
    return newAction;
  }

  @Override
  public String toString() {
    return "AppAction{" +
        ", appName='" + appName + '\'' +
        ", appPackage='" + packageName + '\'' +
        ", actions=" + actions.toString() +
        '}';
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }


}
