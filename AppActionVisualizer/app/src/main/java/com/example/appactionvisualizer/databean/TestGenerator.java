package com.example.appactionvisualizer.databean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestGenerator {
  private static TestGenerator single_instance = null;
  public static TestGenerator getInstance() {
    if (single_instance == null)
      single_instance = new TestGenerator();
    return single_instance;
  }

  /**
   * used for test the specific app actions
   * @return dunkin donuts app actions
   */
  public AppAction generateDDAppAction() {
    AppAction newAction = new AppAction();
    newAction.setAppName("Duckin donuts");
    newAction.setAppPackage("com.dunkinbrands.otgo");
    newAction.getActions().add(TestGenerator.getInstance().genTestActionDD1());
    newAction.getActions().add(TestGenerator.getInstance().genTestActionDD2());
    newAction.getActions().add(TestGenerator.getInstance().genTestActionDD3());
    newAction.getActions().add(TestGenerator.getInstance().genTestActionDD4());
    return newAction;
  }

  public Action genTestActionDD1() {
    List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
    fulfillmentArrayList.add(new Fulfillment("dunkin://orders/history", "DEEPLINK"));
    Action newAction = new Action("actions.intent.GET_ORDER", ActionType.COMMON, fulfillmentArrayList);
    return newAction;
  }

  public Action genTestActionDD2() {
    ParameterMapping parameterMapping = new ParameterMapping();
    Map<String, List<ParameterMapping.Mapping>> map = new HashMap<>();
    String key = "menuItemName";
    List<ParameterMapping.Mapping> list = new ArrayList<>();
    list.add(parameterMapping.new Mapping("1200701","Signature Latte"));
    list.add(parameterMapping.new Mapping("1200702","Iced Signature Latte"));
    list.add(parameterMapping.new Mapping("1000102","Latte"));
    map.put(key, list);
    Fulfillment fulfillment = new Fulfillment("dunkin://orders/item/{menuItemName}", "DEEPLINK");
    parameterMapping.setKey2MapList(map);
    fulfillment.setParameterMapping(parameterMapping);
    List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
    fulfillmentArrayList.add(fulfillment);
    Action newAction = new Action("actions.intent.ORDER_MENU_ITEM",ActionType.FOOD_AND_DRINK, fulfillmentArrayList);
    return newAction;
  }

  public Action genTestActionDD3() {
    List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
    fulfillmentArrayList.add(new Fulfillment("dunkin://home", "DEEPLINK"));
    Action newAction = new Action("actions.intent.OPEN_APP_FEATURE", ActionType.COMMON, fulfillmentArrayList);
    return newAction;
  }

  public Action genTestActionDD4() {
    List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
    fulfillmentArrayList.add(new Fulfillment("dunkin://ddcards", "DEEPLINK"));
    Action newAction = new Action("actions.intent.CREATE_MONEY_TRANSFER", ActionType.FINANCE, fulfillmentArrayList);
    return newAction;
  }

  /**
   * used for test the specific app actions
   * @return dunkin donuts app actions
   */
  public AppAction getUberAppAction() {
    AppAction newAction = new AppAction();
    newAction.setAppName("Uber");
    newAction.setAppPackage("com.ubercab");
    newAction.getActions().add(TestGenerator.getInstance().genTestActionDD1());
    newAction.getActions().add(TestGenerator.getInstance().genTestActionDD2());
    newAction.getActions().add(TestGenerator.getInstance().genTestActionDD3());
    newAction.getActions().add(TestGenerator.getInstance().genTestActionDD4());
    return newAction;
  }

}
