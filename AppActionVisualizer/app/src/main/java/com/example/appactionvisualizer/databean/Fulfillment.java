package com.example.appactionvisualizer.databean;

import java.io.Serializable;

/**
 * Information about how to fulfill the user intent using the Android app.
 * Developers may provide multiple <fulfillment> tags in actions.xml, with different set of required parameters for each intent.
 * https://developers.google.com/assistant/app/action-schema#fulfillment
 * implements Serializable to pass between activities
 */
public class Fulfillment implements Serializable{
    private String urlTemplate;
    private String fulfillmentMode;
    private ParameterMapping parameterMapping = null;

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
