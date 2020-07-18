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
   * @return list of app actions
   */
  public List<AppAction> generateFromTest() {
    List<AppAction> appActions = new ArrayList<>();
    //generate some app action data for test use
    appActions.add(DunkinDonuts.generateDDAppAction());
    appActions.add(Uber.generateUberAppAction());
    appActions.add(Lyft.generateLyftAppAction());
    appActions.add(Paypal.generatePaypalAppAction());
    appActions.add(Nike.generateNikeAppAction());
    return appActions;
  }

  //todo: classes below are all hardcode classes for test use, will be replaced by parser
  static class DunkinDonuts {
    /**
     * used for test the specific app actions
     *
     * @return dunkin donuts app actions
     */
    private static AppAction generateDDAppAction() {
      AppAction newAction = new AppAction();
      newAction.setAppName("Duckin donuts");
      newAction.setPackageName("com.dunkinbrands.otgo");
      newAction.getActions().add(genTestActionDD1());
      newAction.getActions().add(genTestActionDD2());
      newAction.getActions().add(genTestActionDD3());
      newAction.getActions().add(genTestActionDD4());
      return newAction;
    }

    private static Action genTestActionDD1() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      fulfillmentArrayList.add(new Fulfillment("dunkin://orders/history", "DEEPLINK"));
      Action newAction = new Action("actions.intent.GET_ORDER", ActionType.COMMON, fulfillmentArrayList);
      return newAction;
    }

    private static Action genTestActionDD2() {
      ParameterMapping parameterMapping = new ParameterMapping();
      Map<String, List<ParameterMapping.Mapping>> map = new HashMap<>();
      String key = "menuItemName";
      List<ParameterMapping.Mapping> list = new ArrayList<>();
      list.add(parameterMapping.new Mapping("1200701", "Signature Latte"));
      list.add(parameterMapping.new Mapping("1200702", "Iced Signature Latte"));
      list.add(parameterMapping.new Mapping("1000102", "Latte"));
      map.put(key, list);
      Fulfillment fulfillment = new Fulfillment("dunkin://orders/item/{menuItemName}", "DEEPLINK");
      parameterMapping.setKey2MapList(map);
      fulfillment.setParameterMapping(parameterMapping);
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      fulfillmentArrayList.add(fulfillment);
      Action newAction = new Action("actions.intent.ORDER_MENU_ITEM", ActionType.FOOD_AND_DRINK, fulfillmentArrayList);
      return newAction;
    }

    private static Action genTestActionDD3() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      fulfillmentArrayList.add(new Fulfillment("dunkin://home", "DEEPLINK"));
      Action newAction = new Action("actions.intent.OPEN_APP_FEATURE", ActionType.COMMON, fulfillmentArrayList);
      return newAction;
    }

    private static Action genTestActionDD4() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      fulfillmentArrayList.add(new Fulfillment("dunkin://ddcards", "DEEPLINK"));
      Action newAction = new Action("actions.intent.CREATE_MONEY_TRANSFER", ActionType.FINANCE, fulfillmentArrayList);
      return newAction;
    }
  }

  static class Uber {
    /**
     * used for test the specific app actions
     *
     * @return uber app actions
     */
    private static AppAction generateUberAppAction() {
      AppAction newAction = new AppAction();
      newAction.setAppName("Uber");
      newAction.setPackageName("com.ubercab");
      newAction.getActions().add(genTestAction1());
      return newAction;
    }

    private static Action genTestAction1() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      String t = "https://m.uber.com/ul/?client_id=2bqvSbUmB1eX1yHe7UyC_Ia4GeHP0EwS&action=setPickup&product_id=a1111c8c-c720-46c3-8534-2fcdd730040d&link_text=View team roster&partner_deeplink=partner://team/9383&action=setPickup{&pickup[latitude],pickup[longitude],pickup[nickname],dropoff[latitude],dropoff[longitude],dropoff[nickname]}";
      fulfillmentArrayList.add(new Fulfillment(t, "DEEPLINK"));
      String t2 = "https://m.uber.com/ul/?action=setPickup{&pickup[latitude],pickup[longitude],pickup[nickname],dropoff[latitude],dropoff[longitude],dropoff[nickname]}";
      fulfillmentArrayList.add(new Fulfillment(t2, "DEEPLINK"));
//      fulfillmentArrayList.add(new Fulfillment("https://m.uber.com/ul/?client_id=2bqvSbUmB1eX1yHe7UyC_Ia4GeHP0EwS&action=setPickup&product_id=a1111c8c-c720-46c3-8534-2fcdd730040d&link_text=View team roster&partner_deeplink=partner://team/9383&action=setPickup?dropoff[latitude]=37.3861&dropoff[longitude]=-122.084", "DEEPLINK"));
//      fulfillmentArrayList.add(new Fulfillment("https://m.uber.com/ul/?client_id=2bqvSbUmB1eX1yHe7UyC_Ia4GeHP0EwS&action=setPickup&product_id=a1111c8c-c720-46c3-8534-2fcdd730040d&link_text=View team roster&partner_deeplink=partner://team/9383&action=setPickup?dropoff[latitude]=37.4474&dropoff[longitude]=-122.1594&dropoff[nickname]=fox theater", "DEEPLINK"));
//      fulfillmentArrayList.add(new Fulfillment("https://m.uber.com/ul/?client_id=2bqvSbUmB1eX1yHe7UyC_Ia4GeHP0EwS&action=setPickup&product_id=a1111c8c-c720-46c3-8534-2fcdd730040d&link_text=View team roster&partner_deeplink=partner://team/9383&action=setPickup?pickup[latitude]=37.3861&pickup[longitude]=-122.084&pickup[nickname]=Mountain View&dropoff[latitude]=37.4474&dropoff[longitude]=-122.1594&dropoff[nickname]=fox theater", "DEEPLINK"));

      Action newAction = new Action("actions.intent.CREATE_TAXI_RESERVATION", ActionType.TRANSPORTATION, fulfillmentArrayList);
      return newAction;
    }

  }

  static class Lyft {
    /**
     * used for test the specific app actions
     *
     * @return Lyft app actions
     */
    private static AppAction generateLyftAppAction() {
      AppAction newAction = new AppAction();
      newAction.setAppName("Lyft");
      newAction.setPackageName("me.lyft.android");
      newAction.getActions().add(genTestAction1());
      return newAction;
    }

    private static Action genTestAction1() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      String t = "lyft://book-a-ride?id=lyft&pickup_latitude=37.3861&pickup_longitude=-122.084&pickup_title=Mountain View&pickup_formatted_address=123 Easy Street, Mountain View, CA&dropoff_latitude=37.4474&dropoff_longitude=-122.1594&dropoff_title=fox theater&dropoff_formatted_address=456 Main Street, Palo Alto";
      String t2 = "lyft://book-a-ride?id=lyft&pickup_latitude=37.3861&pickup_longitude=-122.084&dropoff_latitude=37.4474&dropoff_longitude=-122.1594";
      String t3 = "lyft://book-a-ride?id=lyft{&pickup_latitude,pickup_longitude,pickup_title,pickup_formatted_address,dropoff_latitude,dropoff_longitude,dropoff_title,dropoff_formatted_address}";
      fulfillmentArrayList.add(new Fulfillment(t, "DEEPLINK"));
      fulfillmentArrayList.add(new Fulfillment(t2, "DEEPLINK"));
      fulfillmentArrayList.add(new Fulfillment(t3, "DEEPLINK"));
      Action newAction = new Action("actions.intent.CREATE_TAXI_RESERVATION", ActionType.TRANSPORTATION, fulfillmentArrayList);
      return newAction;
    }
  }

  static class Paypal {
    /**
     * used for test the specific app actions
     *
     * @return Paypal app actions
     */
    private static AppAction generatePaypalAppAction() {
      AppAction newAction = new AppAction();
      newAction.setAppName("Paypal");
      newAction.setPackageName("com.paypal.android.p2pmobile");
      newAction.getActions().add(genTestAction1());
      return newAction;
    }

    private static Action genTestAction1() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      String tmp2 = "paypal://balance_withdraw_review_new{?amount,currency,name,provider}";
      String tmp = "paypal://send_money{?amount,currency,originName,destinationName}";
      String tmp3 = "paypal://send_money?amount=1&currency=USD&originName=Hua Chen&destinationName=茹俊 李";
      String tmp4 = "paypal://balance_withdraw_review_new?amount=1&currency=USD&name=HC&provider=HC";
      ParameterMapping parameterMapping = new ParameterMapping();
      Map<String, List<ParameterMapping.Mapping>> map = new HashMap<>();
      map.put("amount", null);
      map.put("currency", null);
      map.put("originName", null);
      map.put("destinationName", null);
      Fulfillment fulfillment = new Fulfillment(tmp, "DEEPLINK");
      parameterMapping.setKey2MapList(map);
      fulfillment.setParameterMapping(parameterMapping);
      fulfillmentArrayList.add(fulfillment);
      fulfillmentArrayList.add(new Fulfillment(tmp2, "DEEPLINK"));
      fulfillmentArrayList.add(new Fulfillment(tmp3, "DEEPLINK"));
      fulfillmentArrayList.add(new Fulfillment(tmp4, "DEEPLINK"));
      Action newAction = new Action("actions.intent.CREATE_MONEY_TRANSFER", ActionType.FINANCE, fulfillmentArrayList);
      return newAction;
    }
  }

  static class Nike {
    /**
     * used for test the specific app actions
     *
     * @return Nike app actions
     */
    private static AppAction generateNikeAppAction() {
      AppAction newAction = new AppAction();
      newAction.setAppName("Nike Run Club");
      newAction.setPackageName("com.nike.plusgps");
      newAction.getActions().add(genTestAction1());
      newAction.getActions().add(genTestAction2());
      newAction.getActions().add(genTestAction3());
      newAction.getActions().add(genTestAction4());
      return newAction;
    }

    private static Action genTestAction1() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      String t = "nikerunclub://x-callback-url/startrun/basic?exercise=running";
      String t2 = "nikerunclub://x-callback-url/run";
      fulfillmentArrayList.add(new Fulfillment(t, "DEEPLINK"));
      fulfillmentArrayList.add(new Fulfillment(t2, "DEEPLINK"));
      Action newAction = new Action("actions.intent.START_EXERCISE", ActionType.HEALTH_AND_FITNESS, fulfillmentArrayList);
      return newAction;
    }

    private static Action genTestAction2() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      String t = "nikerunclub://x-callback-url/stoprun?exercise=running";
      String t2 = "nikerunclub://x-callback-url/run";
      fulfillmentArrayList.add(new Fulfillment(t, "DEEPLINK"));
      fulfillmentArrayList.add(new Fulfillment(t2, "DEEPLINK"));
      Action newAction = new Action("actions.intent.STOP_EXERCISE", ActionType.HEALTH_AND_FITNESS, fulfillmentArrayList);
      return newAction;
    }

    private static Action genTestAction3() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      String template = "nikerunclub://x-callback-url/pauserun?exercise=running";
      String t2 = "nikerunclub://x-callback-url/run";
      fulfillmentArrayList.add(new Fulfillment(template, "DEEPLINK"));
      fulfillmentArrayList.add(new Fulfillment(t2, "DEEPLINK"));
      Action newAction = new Action("actions.intent.PAUSE_EXERCISE", ActionType.HEALTH_AND_FITNESS, fulfillmentArrayList);
      return newAction;
    }

    private static Action genTestAction4() {
      List<Fulfillment> fulfillmentArrayList = new ArrayList<>();
      String template = "nikerunclub://x-callback-url/resumerun?exercise=running";
      String t2 = "nikerunclub://x-callback-url/run";
      fulfillmentArrayList.add(new Fulfillment(template, "DEEPLINK"));
      fulfillmentArrayList.add(new Fulfillment(t2, "DEEPLINK"));
      Action newAction = new Action("actions.intent.RESUME_EXERCISE", ActionType.HEALTH_AND_FITNESS, fulfillmentArrayList);
      return newAction;
    }
  }
}
