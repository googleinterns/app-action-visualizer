package com.example.appactionvisualizer.constants;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constant {
  public final static String ACTION_TYPE = "ACTION_TYPE";
  public final static String APP_NAME = "APP_NAME";
  public final static String ACTION = "ACTION";
  public final static String FULFILLMENT_OPTION = "FULFILLMENT";
  public final static String ENTITY_SET = "PARAMETER_MAPPING";
  public final static String KEY = "KEY";
  public final static String ERROR_FILL_PARAMETER = "Please select all parameters";
  public final static String ERROR_NO_PLACE = "Could not find address";
  public final static String PICK_UP_LATITUDE = "PICK_UP_LATITUDE";
  public final static String PICK_UP_LONGITUDE = "PICK_UP_LONGITUDE";
  public final static String DROP_OFF_LATITUDE = "DROP_OFF_LATITUDE";
  public final static String DROP_OFF_LONGITUDE = "DROP_OFF_LONGITUDE";
  public final static String PICK_UP_LATITUDE_FIELD = "taxiReservation.pickupLocation.geo.latitude";
  public final static String PICK_UP_LONGITUDE_FIELD = "taxiReservation.pickupLocation.geo.longitude";
  public final static String DROP_OFF_LATITUDE_FIELD = "taxiReservation.dropoffLocation.geo.latitude";
  public final static String DROP_OFF_LONGITUDE_FIELD = "taxiReservation.dropoffLocation.geo.longitude";


  public final static String ENTITY_FIELD_IDENTIFIER = "identifier";
  public final static String ENTITY_FIELD_NAME = "name";
  public final static String ENTITY_ITEM_LIST = "itemListElement";
  public final static String ENTITY_URL = "url";

  public final static int MAX_RESULTS = 5;

  //Activity result code
  public final static int SELECT_ADDRESS = 0x1;
  public final static int INPUT_PARAMETER = 0x10;

  //Some Actions type Constant String values
  public final static Set<String> COMMON_SET = new HashSet<>(Arrays.asList("actions.intent.OPEN_APP_FEATURE", "actions.intent.GET_ACCOUNT", "actions.intent.GET_ORDER", "actions.intent.GET_THING"));
  public final static Set<String> FINANCE_SET = new HashSet<>(Arrays.asList("actions.intent.CREATE_MONEY_TRANSFER", "actions.intent.CREATE_TRADE_ORDER", "actions.intent.GET_FINANCIAL_POSITION", "actions.intent.GET_STOCK_QUOTE", "actions.intent.GET_INVOICE", "actions.intent.PAY_INVOICE"));
  public final static Set<String> FOOD_SET = new HashSet<>(Arrays.asList("actions.intent.ORDER_MENU_ITEM"));
  public final static Set<String> TRANSPORTATION_SET = new HashSet<>(Arrays.asList("actions.intent.CANCEL_TAXI_RESERVATION", "actions.intent.CREATE_TAXI_RESERVATION", "actions.intent.GET_TAXI_RESERVATION"));

}
