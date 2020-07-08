package com.example.appactionvisualizer.databean;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An App Action that the app supports
 * https://developers.google.com/assistant/app/action-schema#description
 * implements Serializable to pass between activities
 */
public class Action implements Serializable {
  private static final String TAG = "Action";
  /* eg: "actions.intent.ORDER_MENU_ITEM" */
  private String intentName;
  private ActionType actionType;
  private List<Fulfillment> fulfillmentArrayList = new ArrayList<>();

  private Action(){}

  /**
   * @param intentName Built-in intent for the App Action (for example, "actions.intent.CREATE_TAXI_RESERVATION")
   * @param actionType built in action type: (COMMON, FINANCE, FOODANDDRINK, HEALTHANDFITNESS, TRANSPORTATION)
   * @param fulfillmentArrayList
   */
  public Action(String intentName, ActionType actionType, List<Fulfillment> fulfillmentArrayList) {
    this.intentName = intentName;
    this.actionType = actionType;
    this.fulfillmentArrayList = fulfillmentArrayList;
  }

  public String getIntentName() {
    return intentName;
  }

  public void setIntentName(String intentName) {
    this.intentName = intentName;
  }

  public ActionType getActionType() {
    return actionType;
  }

  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  public List<Fulfillment> getFulfillmentArrayList() {
    return fulfillmentArrayList;
  }

  public void setFulfillmentArrayList(ArrayList<Fulfillment> fulfillmentArrayList) {
    this.fulfillmentArrayList = fulfillmentArrayList;
  }

  @Override
  public String toString() {
    return "Action{" +
        "intentName='" + intentName + '\'' +
        ", actionType=" + actionType +
        ", fulfillmentArrayList=" + fulfillmentArrayList.toString() +
        '}';
  }
}
