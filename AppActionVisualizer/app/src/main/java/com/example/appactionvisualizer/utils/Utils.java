package com.example.appactionvisualizer.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppActionProtos;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

  //Android resources could only save 0-9, a-z and underscore. So the package name need to be converted
  public static int getResIdByPackageName(String pkgName, Class<?> className) {
    return getResId(pkgName.toLowerCase().replace('.', '_'), className);
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

  //go to play store
  public static void jumpToStore(Context context, final String packageName) {
    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
  }

  //first check if the url can jump to activity, if not, check if package exists
  public static void jumpToApp(Context context, final String url, final String packageName) {
    try {
      Intent intent = Intent.parseUri(url, 0);
      context.startActivity(intent);
    } catch (Exception e) {
      try {
        context.getPackageManager().getApplicationInfo(packageName, 0);
        Utils.showMsg(context.getString(R.string.error_parsing),context);
      } catch (PackageManager.NameNotFoundException ex) {
        Utils.jumpToStore(context, packageName);
      }
    }
  }


  /**
   * dialog for user to choose among given list values
   */
  public static void popUpDialog(final Context context, final String title, List<CharSequence> list, DialogInterface.OnClickListener listener) {
    try {
      MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
      materialAlertDialogBuilder.setTitle(title);
      CharSequence[] keys = new CharSequence[list.size()];
      materialAlertDialogBuilder.setItems(list.toArray(keys), listener).show();
    } catch (Exception e) {
      Utils.showMsg(context.getString(R.string.error), context);
      e.printStackTrace();
    }
  }
}
