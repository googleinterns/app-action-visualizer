package com.example.appactionvisualizer.databean;


import com.example.appactionvisualizer.constants.Constant;

import java.io.Serializable;

public enum ActionType implements Serializable {
  COMMON("Common", "common"),
  FINANCE("Finance", "finance"),
  FOOD_AND_DRINK("Food And drink", "food-and-drink"),
  HEALTH_AND_FITNESS("Health and fitness", "health-and-fitness"),
  TRANSPORTATION("Transportation", "transportation");

  private String name;
  private String url;

  ActionType(String name, String url) {
    this.name = name;
    this.url = url;
  }

  public static ActionType getActionTypeValue(int idx) {
    return ActionType.values()[idx % 5];
  }

  // return the corresponding action type provided by official website
  public static ActionType getActionTypeByName(String intentName) {
    if (Constant.COMMON_SET.contains(intentName)) {
      return COMMON;
    }
    if (Constant.FINANCE_SET.contains(intentName)) {
      return FINANCE;
    }
    if (Constant.FOOD_SET.contains(intentName)) {
      return FOOD_AND_DRINK;
    }
    if (Constant.TRANSPORTATION_SET.contains(intentName)) {
      return TRANSPORTATION;
    }
    return HEALTH_AND_FITNESS;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }
}
