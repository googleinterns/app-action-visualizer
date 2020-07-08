package com.example.appactionvisualizer.databean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An App Action that the app supports
 * https://developers.google.com/assistant/app/action-schema#description
 * implements Serializable to ensure pass between activities
 */
public class Action implements Serializable {
  private static final String TAG = "Action";
  /* eg: "actions.intent.ORDER_MENU_ITEM" */
  private String intentName;
  private ActionType actionType;
  private ArrayList<Fulfillment> fulfillmentArrayList = new ArrayList<>();

  private Action(){}

  /**
   * @param intentName Built-in intent for the App Action (for example, "actions.intent.CREATE_TAXI_RESERVATION")
   * @param actionType built in action type: (COMMON, FINANCE, FOODANDDRINK, HEALTHANDFITNESS, TRANSPORTATION)
   * @param fulfillmentArrayList
   */
  public Action(String intentName, ActionType actionType, ArrayList<Fulfillment> fulfillmentArrayList) {
    this.intentName = intentName;
    this.actionType = actionType;
    this.fulfillmentArrayList = fulfillmentArrayList;
  }

  public static Action genTestAction1() {
    Action newAction = new Action();
    newAction.intentName = "actions.intent.GET_ORDER";
    newAction.actionType = ActionType.COMMON;
    newAction.fulfillmentArrayList.add(newAction.new Fulfillment("dunkin://orders/history", "DEEPLINK"));
    return newAction;
  }

  public static Action genTestAction2() {
    Action newAction = new Action();
    newAction.intentName = "actions.intent.ORDER_MENU_ITEM";
    newAction.actionType = ActionType.FOOD_AND_DRINK;
    ParameterMapping parameterMapping = new ParameterMapping();
    Map<String, List<ParameterMapping.Mapping>> map = new HashMap<>();
    String key = "menuItemName";
    map.put(key, new ArrayList<ParameterMapping.Mapping>());
    map.get(key).add(parameterMapping.new Mapping("1200701","Signature Latte"));
    map.get(key).add(parameterMapping.new Mapping("1200702","Iced Signature Latte"));
    map.get(key).add(parameterMapping.new Mapping("1000102","Latte"));
    Fulfillment fulfillment = newAction.new Fulfillment("dunkin://orders/item/{menuItemName}", "DEEPLINK");
    fulfillment.setParameterMapping(parameterMapping);
    newAction.fulfillmentArrayList.add(fulfillment);
    return newAction;
  }

  public static Action genTestAction3() {
    Action newAction = new Action();
    newAction.intentName = "actions.intent.OPEN_APP_FEATURE";
    newAction.actionType = ActionType.COMMON;
    newAction.fulfillmentArrayList.add(newAction.new Fulfillment("dunkin://home", "DEEPLINK"));
    return newAction;
  }



  public class Fulfillment implements Serializable{
    private String urlTemplate;
    private String fulfillmentMode;
    private ParameterMapping parameterMapping;

    /**
     * @param urlTemplate Template for constructing either the deep link or a Slice URI to be opened on the device
     * @param fulfillmentMode DEEPLINK or SLICE
     */
    public Fulfillment(String urlTemplate, String fulfillmentMode) {
      this.urlTemplate = urlTemplate;
      this.fulfillmentMode = fulfillmentMode;
    }


    public String getUrlTemplate() {
      return urlTemplate;
    }

    public void setUrlTemplate(String urlTemplate) {
      this.urlTemplate = urlTemplate;
    }

    public String getFulfillmentMode() {
      return fulfillmentMode;
    }

    public void setFulfillmentMode(String fulfillmentMode) {
      this.fulfillmentMode = fulfillmentMode;
    }


    public ParameterMapping getParameterMapping() {
      return parameterMapping;
    }

    public void setParameterMapping(ParameterMapping parameterMapping) {
      this.parameterMapping = parameterMapping;
    }

    @Override
    public String toString() {
      return "FulfillmentActivity{" +
          "urlTemplate='" + urlTemplate + '\'' +
          ", fulfillmentMode='" + fulfillmentMode + '\'' +
          '}';
    }
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

  public ArrayList<Fulfillment> getFulfillmentArrayList() {
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
