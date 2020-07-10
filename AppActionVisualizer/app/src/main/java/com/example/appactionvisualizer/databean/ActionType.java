package com.example.appactionvisualizer.databean;

import androidx.annotation.NonNull;

import java.io.Serializable;

public enum ActionType implements Serializable {
  COMMON {
    @Override
    public String toString() {
      return "Common";
    }
  },
  FINANCE {
    @Override
    public String toString() {
      return "Finance";
    }
  }, FOOD_AND_DRINK {
    @Override
    public String toString() {
      return "Food And drink";
    }
  }, HEALTH_AND_FITNESS {
    @Override
    public String toString() {
      return "Health and fitness";
    }
  }, TRANSPORTATION {
    @Override
    public String toString() {
      return "Transportation";
    }
  };
}
