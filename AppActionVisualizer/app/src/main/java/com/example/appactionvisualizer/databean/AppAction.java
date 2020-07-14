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
 * implements Serializable to pass between activities
 */
public class AppAction implements Serializable {
  private static final String TAG = "APPACTION";

  private int actionId;
  private String appName;
  private String appPackage;
  private List<Action> actions = new ArrayList<>();
  //todo: add entityset

  public static List<AppAction> allAppActions= new ArrayList<>();
  public static Map<ActionType, ArrayList<AppAction>> appActionList = new HashMap<>();


  public static void parseData() {
    //TODO: parse from file logic
    //generate some data for test use
    allAppActions.clear();
    AppAction appAction = TestGenerator.getInstance().generateDDAppAction();
    allAppActions.add(appAction);


    //set up each fragments' data list, make sure there's no duplicate data in one action type
    Map<ActionType, Set<AppAction>> appActionUnique = new HashMap<>();
    for(AppAction app : allAppActions) {
      for(Action action : app.actions) {
        if(appActionUnique.get((action.getActionType())) == null)
          appActionUnique.put(action.getActionType(), new HashSet<AppAction>());
        appActionUnique.get(action.getActionType()).add(app);
      }
    }
    //in case there're some types haven't been initialized
    for (ActionType actiontype: ActionType.values()) {
      if(appActionList.get(actiontype) == null)
        appActionList.put(actiontype, new ArrayList<AppAction>());
    }
    for(Map.Entry<ActionType, Set<AppAction>> entry : appActionUnique.entrySet()) {
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

  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }


}
