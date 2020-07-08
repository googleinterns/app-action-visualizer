package com.example.appactionvisualizer.databean;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

  private int actionId;
  private String appName;
  private String appPackage;
  private ArrayList<Action> actions = new ArrayList<>();
  //todo: add entityset

  public static ArrayList<AppAction> allAppActions= new ArrayList<>();
  public static Map<ActionType, ArrayList<AppAction>> appActionList = new HashMap<>();


  public static void parseData() {
    //TODO: parse from file logic
    //generate some data for test use
    AppAction appAction = TestGen.getInstance().genDDAppAction();
    allAppActions.add(appAction);


    //set up each fragments' data list, make sure there's no duplicate data in one action type
    Map<ActionType, Set<AppAction>> AppActionUnique = new HashMap<>();
    for(AppAction app : allAppActions) {
      for(Action action : app.actions) {
        if(AppActionUnique.get((action.getActionType())) == null)
          AppActionUnique.put(action.getActionType(), new HashSet<AppAction>());
        AppActionUnique.get(action.getActionType()).add(app);
      }
    }
    //in case there're some types haven't been initialized
    for (ActionType actiontype: ActionType.values()) {
      if(appActionList.get(actiontype) == null)
        appActionList.put(actiontype, new ArrayList<AppAction>());
    }
    for(Map.Entry<ActionType, Set<AppAction>> entry : AppActionUnique.entrySet()) {
      appActionList.get(entry.getKey()).addAll(entry.getValue());
    }
  }

  /**
   * generate a new app action
   * @return a new action with with a unique id
   */
  public static AppAction genActions() {
    AppAction newAction = new AppAction();
    //TODO: need parse from file logic

    return newAction;
  }

  @Override
  public String toString() {
    return "AppAction{" +
        "actionId=" + actionId +
        ", appName='" + appName + '\'' +
        ", appPackage='" + appPackage + '\'' +
        ", actions=" + actions.toString() +
        '}';
  }

  public int getActionId() {
    return actionId;
  }

  public void setActionId(int actionId) {
    this.actionId = actionId;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppPackage() {
    return appPackage;
  }

  public void setAppPackage(String appPackage) {
    this.appPackage = appPackage;
  }

  public ArrayList<Action> getActions() {
    return actions;
  }

  public void setActions(ArrayList<Action> actions) {
    this.actions = actions;
  }


}
