package com.btc.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;

import com.gsk.franchise.utils.ConstantsKt;

import java.io.File;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static void printHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(ConstantsKt.TAG, "HashKey: " + hashKey);
            }
        } catch (Exception e) {
            Log.e(ConstantsKt.TAG, "Exception", e);
        }
    }

    private static Activity scanForActivity(Context context) {
        if (context instanceof Activity) return (Activity) context;
        else return scanForActivity(((ContextWrapper) context).getBaseContext());

    }

/*    public static void redirectUsingCustomTab(Activity activity, String url) {
        Uri uri = Uri.parse(url);
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(activity, uri);
    }*/

    @SuppressLint("SimpleDateFormat")
    public static String timeToAgo(String dateValue) {
        String convTime = null;
        String suffix = "";
    try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pasTime = dateFormat.parse(dateValue);
            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + " Seconds" + suffix;
            } else if (minute < 60) {
                convTime = minute + " Minutes" + suffix;
            } else if (hour < 24) {
                convTime = hour + " Hours" + suffix;
            } else if (day < 8) {
                convTime = day + " Days" + suffix;
            } else {
                if (day > 360) {
                    convTime = (day / 360) + " Years" + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " Months" + suffix;
                } else {
                    convTime = (day / 7) + " Week" + suffix;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            convTime = dateValue;
        }

        return convTime;
    }

    public static String getHtmlData(Context context, String html) {
        String text = "<html><head><style type=\"text/css\">@font-face {font-family: roboto ; src: url(\"file:///android_asset/normal.ttf\")}body {font-family: roboto ;font-size: 17px; color: #25233C; line-height: 1.5;}" + "ul { list-style: none;}" + "ul li::before {" + " content: \"\\2022\";" + " color: #CCD2E3;" + "font-weight: bold;\n" + "display: inline-block; \n" + "width: 1em;\n" + "margin-left: -1em;\n" + "}" + "" + "</style></head><body>";
        String pas = "</body></html>";
        String string = text + html + pas;
        return string;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable = view.getBackground();
        if (drawable != null) {
            drawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return bitmap;
    }

    public static int getScreenWidth(@NonNull Context context) {
        Activity activity = scanForActivity(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

  /*  public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        int resultCode = availability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }*/

    public static void openAppSetting(Context context, String message) {
        ConstantsKt.showToastShort(message);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        Intent i = new Intent();
        i.setData(uri);
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(i);
    }
}
