package com.example.appactionvisualizer.constants;

import androidx.annotation.StringRes;

import com.example.appactionvisualizer.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constant {
  public static final String ACTION_TYPE = "ACTION_TYPE";
  public static final String APP_NAME = "APP_NAME";
  public static final String ACTION = "ACTION";
  public static final String FULFILLMENT_OPTION = "FULFILLMENT";
  public static final String ENTITY_SET = "PARAMETER_MAPPING";
  public static final String KEY = "KEY";
  public static final String ERROR_FILL_PARAMETER = "Please select all parameters";
  public static final String ERROR_NO_PLACE = "Could not find address";
  public static final String PICK_UP_LATITUDE = "PICK_UP_LATITUDE";
  public static final String PICK_UP_LONGITUDE = "PICK_UP_LONGITUDE";
  public static final String DROP_OFF_LATITUDE = "DROP_OFF_LATITUDE";
  public static final String DROP_OFF_LONGITUDE = "DROP_OFF_LONGITUDE";
  public static final String PICK_UP_LATITUDE_FIELD = "taxiReservation.pickupLocation.geo.latitude";
  public static final String PICK_UP_LONGITUDE_FIELD =
      "taxiReservation.pickupLocation.geo.longitude";
  public static final String DROP_OFF_LATITUDE_FIELD =
      "taxiReservation.dropoffLocation.geo.latitude";
  public static final String DROP_OFF_LONGITUDE_FIELD =
      "taxiReservation.dropoffLocation.geo.longitude";

  // "{" indicates that this deeplink needs additional parameter(s)
  public static final String URL_PARAMETER_INDICATOR = "{";
  public static final String URL_QUESTION_MARK = "?";
  // A fulfillment uses {@url} in the URL template,
  // The fulfillment attempts to derive {@url} from sources such as web and inline inventory
  public static final String URL_NO_LINK = "{@url}";

  public static final String URL_KEY = "feature";
  public static final String ENTITY_FIELD_IDENTIFIER = "identifier";
  public static final String ENTITY_FIELD_NAME = "name";
  public static final String ENTITY_ITEM_LIST = "itemListElement";
  public static final String ENTITY_URL = "url";

  public static final int MAX_RESULTS = 5;

  // Activity result code
  public static final int SELECT_ADDRESS = 0x1;
  public static final int INPUT_PARAMETER = 0x10;

  // Some Actions type Constant String values
  public static final Set<String> COMMON_SET =
      new HashSet<>(
          Arrays.asList(
              "actions.intent.OPEN_APP_FEATURE",
              "actions.intent.GET_ACCOUNT",
              "actions.intent.GET_ORDER",
              "actions.intent.GET_THING"));
  public static final Set<String> FINANCE_SET =
      new HashSet<>(
          Arrays.asList(
              "actions.intent.CREATE_MONEY_TRANSFER",
              "actions.intent.CREATE_TRADE_ORDER",
              "actions.intent.GET_FINANCIAL_POSITION",
              "actions.intent.GET_STOCK_QUOTE",
              "actions.intent.GET_INVOICE",
              "actions.intent.PAY_INVOICE"));
  public static final Set<String> FOOD_SET =
      new HashSet<>(Arrays.asList("actions.intent.ORDER_MENU_ITEM"));
  public static final Set<String> TRANSPORTATION_SET =
      new HashSet<>(
          Arrays.asList(
              "actions.intent.CANCEL_TAXI_RESERVATION",
              "actions.intent.CREATE_TAXI_RESERVATION",
              "actions.intent.GET_TAXI_RESERVATION"));

  @StringRes
  public static final int[] TAB_TITLES =
      new int[] {
        R.string.tab_text_statistics,
        R.string.tab_text_all,
        R.string.tab_text_1,
        R.string.tab_text_2,
        R.string.tab_text_3,
        R.string.tab_text_4,
        R.string.tab_text_5
      };
  // Action name prefix.
  public static final String INTENT_PREFIX = "actions.intent.";
  public static final String WHITESPACE = " ";
  public static final String UNDERLINE = "_";
}
