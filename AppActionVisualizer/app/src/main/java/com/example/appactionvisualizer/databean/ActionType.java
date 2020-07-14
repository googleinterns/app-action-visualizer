package com.example.appactionvisualizer.databean;


import java.io.Serializable;

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

  public String getName() {
    return name;
  }
}
