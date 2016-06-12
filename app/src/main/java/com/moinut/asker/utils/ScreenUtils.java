package com.moinut.asker.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScreenUtils {

    public static int getNavigationBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean hasSoftKeys(Context ctx){
        boolean hasSoftwareKeys;
        WindowManager manager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            Display d = manager.getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys =  (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        }else{
            boolean hasMenuKey = ViewConfiguration.get(ctx).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }

    public static void paddingToNavigationBar(View view) {
        if (!hasSoftKeys(view.getContext()) || !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT))
            return;
        Method method = null;
        try {
            method = view.getClass().getMethod("setClipToPadding", boolean.class);
        } catch (NoSuchMethodException e) {
            return;
        }
        try {
            method.invoke(view, false);
            view.setPadding(0, 0, 0, getNavigationBarHeight(view.getContext()));
        } catch (IllegalAccessException e) {
            return;
        } catch (InvocationTargetException e) {
            return;
        }
    }

    public static void paddingToNavigationBarWithStatusBar(View view) {
        if (!hasSoftKeys(view.getContext()) || !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT))
            return;
        Method method = null;
        try {
            method = view.getClass().getMethod("setClipToPadding", boolean.class);
        } catch (NoSuchMethodException e) {
            return;
        }
        try {
            method.invoke(view, false);
            view.setPadding(0, 0, 0, getNavigationBarHeight(view.getContext()) + getStatusBarHeight(view.getContext()));
        } catch (IllegalAccessException e) {
            return;
        } catch (InvocationTargetException e) {
            return;
        }
    }
}
