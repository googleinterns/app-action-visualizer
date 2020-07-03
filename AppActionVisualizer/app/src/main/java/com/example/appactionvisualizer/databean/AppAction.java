package com.example.appactionvisualizer.databean;


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
 * implements Serializable to ensure pass between activities
 */
public class AppAction implements Serializable {
  private static int newId = 0;
  private static final String TAG = "APPACTION";

  private int actionId;
  private String appName;
  private String appPackage;
  private ArrayList<Action> actions = new ArrayList<>();
  //todo: add entityset

  public static Map<ActionType, ArrayList<AppAction>> appActionList = new HashMap<>();

  private AppAction(){}

  public static void parseData() {
    //TODO: parse from file logic
    //generate some data for test use
    List<AppAction> item = new ArrayList<AppAction>();
    for(ActionType type : ActionType.values()) {
      AppAction appAction = genTestAppAction();
      appAction.actions.get(0).setActionType(type);
      item.add(appAction);
    }

    //set up each fragments' data list, make sure there's no duplicate data in one action type
    Map<ActionType, Set<AppAction>> AppActionUnique = new HashMap<>();
    for(AppAction app : item) {
      for(Action action : app.actions) {
        if(AppActionUnique.get((action.getActionType())) == null)
          AppActionUnique.put(action.getActionType(), new HashSet<AppAction>());
        AppActionUnique.get(action.getActionType()).add(app);
      }
    }
    for(Map.Entry<ActionType, Set<AppAction>> entry : AppActionUnique.entrySet()) {
      if(appActionList.get(entry.getKey()) == null)
        appActionList.put(entry.getKey(), new ArrayList<AppAction>());
      appActionList.get(entry.getKey()).addAll(entry.getValue());
    }
  }

  /**
   * generate a new app action
   * @return a new action with with a unique id
   */
  public static AppAction genActions() {
    AppAction newAction = new AppAction();
    newAction.actionId = newId++;
    //TODO: need parse from file logic

    return newAction;
  }

  /**
   * used for test the specific app actions
   * @return dunkin donuts app actions
   */
  private static AppAction genTestAppAction() {
    AppAction newAction = new AppAction();
    newAction.actionId = newId++;
    newAction.appName = "Duckin donuts" + newAction.actionId;
    newAction.appPackage = "com.dunkinbrands.otgo";
    newAction.actions.add(Action.genTestAction1());
    newAction.actions.add(Action.genTestAction2());
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
