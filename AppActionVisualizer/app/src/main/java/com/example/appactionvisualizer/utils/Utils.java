package com.example.appactionvisualizer.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.example.appactionvisualizer.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
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

  //go to a web page with url
  public static void jumpToWebPage(Context context, final String url) {
    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
  }

  //first check if package exists since user may haven't installed the app
  //then check if the url can jump to activity
  public static void jumpToApp(Context context, final String url, final String packageName) {
    try {
      context.getPackageManager().getApplicationInfo(packageName, 0);
    } catch (PackageManager.NameNotFoundException e) {
      Utils.jumpToWebPage(context, context.getString(R.string.url_reference_prefix, packageName));
    }
    try {
      Intent intent = Intent.parseUri(url, 0);
      intent.setPackage(packageName);
      context.startActivity(intent);
    } catch (Exception e) {
      String errorMsg;
      if(e instanceof URISyntaxException) {
        errorMsg = context.getString(R.string.error_parsing);
      }else if(e instanceof ActivityNotFoundException) {
        errorMsg = context.getString(R.string.error_activity);
      }else{
        errorMsg = context.getString(R.string.error_unknown);
      }
      Utils.showMsg(errorMsg, context);
    }
  }


  /**
   * dialog for user to choose among given list values
   */
  public static void popUpDialog(final Context context, final String title, List<CharSequence> list, DialogInterface.OnClickListener listener) {
    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
    materialAlertDialogBuilder.setTitle(title);
    CharSequence[] keys = new CharSequence[list.size()];
    materialAlertDialogBuilder.setItems(list.toArray(keys), listener).show();
  }
}
