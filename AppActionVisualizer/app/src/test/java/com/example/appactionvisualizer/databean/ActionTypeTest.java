package com.example.appactionvisualizer.databean;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActionTypeTest {

  /**
   * Test if getActionTypeValue works correctly
   */
  @Test
  public void getActionTypeValueTest() {
    assertEquals(ActionType.COMMON, ActionType.getActionTypeValue(0));
    assertEquals(ActionType.FINANCE, ActionType.getActionTypeValue(1));
    assertEquals(ActionType.FOOD_AND_DRINK, ActionType.getActionTypeValue(2));
    assertEquals(ActionType.HEALTH_AND_FITNESS, ActionType.getActionTypeValue(3));
    assertEquals(ActionType.TRANSPORTATION, ActionType.getActionTypeValue(4));
  }

  /**
   * Test if getActionTypeByName works correctly by using some examples provided by https://developers.google.com/assistant/app/reference/built-in-intents
   */
  @Test
  public void getActionTypeByNameTest() {
    assertEquals(ActionType.COMMON, ActionType.getActionTypeByName("actions.intent.OPEN_APP_FEATURE"));
    assertEquals(ActionType.FINANCE, ActionType.getActionTypeByName("actions.intent.CREATE_MONEY_TRANSFER"));
    assertEquals(ActionType.FOOD_AND_DRINK, ActionType.getActionTypeByName("actions.intent.ORDER_MENU_ITEM"));
    assertEquals(ActionType.HEALTH_AND_FITNESS, ActionType.getActionTypeByName("actions.intent.START_EXERCISE"));
    assertEquals(ActionType.TRANSPORTATION, ActionType.getActionTypeByName("actions.intent.CREATE_TAXI_RESERVATION"));
  }
}