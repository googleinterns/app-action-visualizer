package com.example.appactionvisualizer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.lang.reflect.Field;

public class Utils {
  private Utils() {
  }

  /**
   * resID = getResId("icon", R.drawable.class);
   * @param resName
   * @param className
   * @return resource id if found or -1 not found
   */
  public static int getResId(String resName, Class<?> className) {
    try {
      Field idField = className.getDeclaredField(resName);
      return idField.getInt(idField);
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
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

  public static void goToStore(Context context,final String packageName) {
    //go to play store
    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
  }
}
