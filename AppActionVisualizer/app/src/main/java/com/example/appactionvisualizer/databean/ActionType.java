package com.example.appactionvisualizer.databean;


import java.io.Serializable;
import java.util.HashMap;

public enum ActionType implements Serializable {
  COMMON("Common"),
  FINANCE("Finance"),
  FOOD_AND_DRINK("Food And drink"),
  HEALTH_AND_FITNESS ("Health and fitness"),
  TRANSPORTATION("Transportation");

  private String name;

  ActionType(String name) {
    this.name = name;
  }

  public static ActionType getActionTypeValue(int idx) {
    return ActionType.values()[idx % 5];
  }

  public static ActionType getActionTypeByName(String intentName) {
    if(intentName.equals("actions.intent.OPEN_APP_FEATURE") || intentName.equals("actions.intent.GET_ACCOUNT") || intentName.equals("actions.intent.GET_ORDER")) {
      return COMMON;
    }
    if(intentName.contains("INVOICE") || intentName.equals("actions.intent.CREATE_MONEY_TRANSFER") || intentName.equals("actions.intent.CREATE_TRADE_ORDER") || intentName.equals("actions.intent.GET_FINANCIAL_POSITION") || intentName.equals("actions.intent.GET_STOCK_QUOTE")) {
      return FINANCE;
    }
    if(intentName.equals("actions.intent.ORDER_MENU_ITEM")) {
      return FOOD_AND_DRINK;
    }
    if(intentName.contains("RESERVATION")) {
      return TRANSPORTATION;
    }
    return  HEALTH_AND_FITNESS;
  }

  public String getName() {
    return name;
  }
}
