package com.example.appactionvisualizer.databean;

import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;

// Provide a trivial class for holding all the data Parameter Activity needs.
public class AppFulfillment {
  public AppAction appAction;
  public Action action;
  public FulfillmentOption fulfillmentOption;

  public AppFulfillment(AppAction appAction, Action action, FulfillmentOption fulfillmentOption) {
    this.appAction = appAction;
    this.action = action;
    this.fulfillmentOption = fulfillmentOption;
  }
}
