package com.example.appactionvisualizer.utils;

import android.content.Context;
import android.widget.Toast;

public class Utils {
  private Utils() {
  }


  public static void showMsg(String message, Context context) {
    getToast(message, context, Toast.LENGTH_SHORT).show();
  }

  private static Toast getToast(String message, Context context) {
    return getToast(message, context, Toast.LENGTH_LONG);
  }

  private static Toast getToast(String message, Context context, int length) {
    return Toast.makeText(context, message, length);
  }
}
