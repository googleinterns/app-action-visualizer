package com.example.appactionvisualizer.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.example.appactionvisualizer.R;
import com.example.appactionvisualizer.constants.Constant;
import com.example.appactionvisualizer.databean.AppActionProtos.Action;
import com.example.appactionvisualizer.databean.AppActionProtos.AppAction;
import com.example.appactionvisualizer.databean.AppActionProtos.EntitySet;
import com.example.appactionvisualizer.databean.AppActionProtos.FulfillmentOption;
import com.example.appactionvisualizer.ui.activity.ParameterActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.List;

public class Utils {
  private Utils() {}

  /**
   * resID = getResId("icon", R.drawable.class);
   *
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

  // Android resources could only save 0-9, a-z and underscore. So the package name need to be
  // converted
  public static int getResIdByPackageName(String pkgName, Class<?> className) {
    return getResId(pkgName.toLowerCase().replace('.', '_'), className);
  }

  /**
   * @param context
   * @param pkgName packageName of app
   * @return app's Name get app's Name by packageName 1. Use packageManager 2. use built-in
   *     resources(currently support 56 apps). 3. if 1 and 2 cannot find, return "*unknown" (* is to
   *     make user it will be placed at the end of list after sorted)
   */
  public static String getAppNameByPackageName(final Context context, final String pkgName) {
    String appName = "";
    try {
      PackageManager packageManager = context.getPackageManager();
      appName =
          packageManager
              .getApplicationLabel(packageManager.getApplicationInfo(pkgName, 0))
              .toString();
    } catch (PackageManager.NameNotFoundException e) {
      int strId = Utils.getResIdByPackageName(pkgName, R.string.class);
      if (strId != -1) {
        appName = context.getString(strId);
      } else {
        appName = context.getString(R.string.unknown);
      }
    }
    return appName;
  }

  public static Drawable getIconByPackageName(final Context context, final String pkgName) {
    try {
      PackageManager packageManager = context.getPackageManager();
      return (packageManager.getApplicationIcon(pkgName));
    } catch (PackageManager.NameNotFoundException e) {
      int imgId = Utils.getResIdByPackageName(pkgName, R.drawable.class);
      if (imgId == -1) {
        imgId = R.drawable.rounded_corner;
      }
      return ResourcesCompat.getDrawable(context.getResources(), imgId, null);
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

  // go to a web page with url
  public static void jumpToWebPage(Context context, final String url) {
    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
  }

  // first check if package exists since user may haven't installed the app
  // then check if the url can jump to activity
  public static void jumpToApp(Context context, final String url, final String packageName) {
    try {
      context.getPackageManager().getApplicationInfo(packageName, 0);
    } catch (PackageManager.NameNotFoundException e) {
      Utils.jumpToWebPage(context, context.getString(R.string.url_reference_prefix, packageName));
    }
    try {
      // parseUri() might throw URISyntaxException error
      Intent intent = Intent.parseUri(url, 0);
      intent.setPackage(packageName);
      // startActivity() might throw ActivityNotFoundException error
      context.startActivity(intent);
    } catch (Exception e) {
      String errorMsg;
      if (e instanceof URISyntaxException) {
        errorMsg = context.getString(R.string.error_parsing);
      } else if (e instanceof ActivityNotFoundException) {
        errorMsg = context.getString(R.string.error_activity);
      } else {
        errorMsg = context.getString(R.string.error_unknown);
      }
      Utils.showMsg(errorMsg, context);
    }
  }

  /** Dialog for user to choose among given list values. */
  public static void popUpDialog(
      final Context context,
      final String title,
      List<CharSequence> list,
      DialogInterface.OnClickListener listener) {
    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
    materialAlertDialogBuilder.setTitle(title);
    CharSequence[] keys = new CharSequence[list.size()];
    materialAlertDialogBuilder.setItems(list.toArray(keys), listener).show();
  }

  // Jump to a deep link which has no parameter or jump into parameterActivity to select parameters
  // for the deeplink.
  public static void jumpByType(
      final Context context,
      final AppAction appAction,
      final Action action,
      final FulfillmentOption fulfillmentOption) {
    if (fulfillmentOption.getUrlTemplate().getTemplate().equals(Constant.URL_NO_LINK)
        || fulfillmentOption.getUrlTemplate().getParameterMapCount() > 0) {
      Intent intent = new Intent(context, ParameterActivity.class);
      intent.putExtra(Constant.FULFILLMENT_OPTION, fulfillmentOption);
      intent.putExtra(Constant.ACTION, action);
      intent.putExtra(Constant.APP_NAME, appAction);
      context.startActivity(intent);
    } else {
      String url = fulfillmentOption.getUrlTemplate().getTemplate();
      if (appAction.getPackageName().equals("com.dunkinbrands.otgo")) {
        url = url.substring(0, url.indexOf("?"));
      }
      Utils.jumpToApp(context, url, appAction.getPackageName());
    }
  }

  /**
   * @param appAction
   * @param action
   * @return
   */
  // All fulfillment options with "@url" require "feature" key in parameter list of actions(expect
  // url_filter)
  public static EntitySet checkUrlEntitySet(AppAction appAction, Action action) {
    for (Action.Parameter parameter : action.getParametersList()) {
      if (parameter.getName().equals(Constant.URL_KEY)) {
        if (parameter.getEntitySetReferenceCount() == 0) continue;
        String reference = parameter.getEntitySetReference(0).getEntitySetId();
        for (EntitySet set : appAction.getEntitySetsList()) {
          if (set.getItemList()
              .getFieldsOrThrow(Constant.ENTITY_FIELD_IDENTIFIER)
              .getStringValue()
              .equals(reference)) {
            return set;
          }
        }
      }
    }
    return null;
  }
}
