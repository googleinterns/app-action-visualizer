package com.example.appactionvisualizer.databean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * ParameterMapping data class
 * parameter key to a list of mapping
 * <"menuItemName", [{"Signature Latte", "1200701"}, {"Iced Signature Latte", "1200702"}]>
 */
public class ParameterMapping implements Serializable {
  private Map<String, List<Mapping>> key2MapList;

  public Map<String, List<Mapping>> getKey2MapList() {
    return key2MapList;
  }

  public void setKey2MapList(Map<String, List<Mapping>> key2MapList) {
    this.key2MapList = key2MapList;
  }

  public class Mapping implements Serializable{
    private String identifier;
    private String name;

    public Mapping(String identifier, String name) {
      this.identifier = identifier;
      this.name = name;
    }

    public String getIdentifier() {
      return identifier;
    }

    public void setIdentifier(String identifier) {
      this.identifier = identifier;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
