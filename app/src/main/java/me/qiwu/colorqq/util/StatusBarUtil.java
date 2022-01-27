package me.qiwu.colorqq.util;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by Remix on 2016/7/28.
 */
public class StatusBarUtil {


    public static void setStatusBarMode(Window window, boolean isDarkMode) {
        if (window == null) return;
        //获得miui版本
        String miui = "";
        int miuiVersion = 0;
        if (Build.MANUFACTURER.equals("Xiaomi")) {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class, String.class);
                miui = (String) (get.invoke(c, "ro.miui.ui.version.name", "unknown"));
                if (!TextUtils.isEmpty(miui) && miui.length() >= 2 && TextUtils
                        .isDigitsOnly(miui.substring(1, 2))) {
                    miuiVersion = Integer.valueOf(miui.substring(1, 2));
                }
            } catch (Exception e) {
            }
        }
        if (Build.MANUFACTURER.equals("Meizu")) {
            MeizuStatusbar.setStatusBarDarkMode(window,isDarkMode);
        } else if (Build.MANUFACTURER.equals("Xiaomi") && miuiVersion >= 6 && miuiVersion < 9) {
            XiaomiStatusbar.setStatusBarDarkMode(isDarkMode, window);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            if (isDarkMode) {
                systemUiVisibility |= SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                systemUiVisibility &= ~SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }


    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
    // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 魅族状态栏工具类
     */
    public static class MeizuStatusbar {
        public static void setStatusBarDarkMode(Window window, boolean dark) {
            try {
                WindowManager.LayoutParams attributes = window.getAttributes();
                Field declaredField = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field declaredField2 = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                declaredField.setAccessible(true);
                declaredField2.setAccessible(true);
                int i = declaredField.getInt((Object) null);
                int i2 = declaredField2.getInt(attributes);
                declaredField2.setInt(attributes, dark ? i2 | i : (~i) & i2);
                window.setAttributes(attributes);
            } catch (Exception unused) {
                unused.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && window.getDecorView()!=null) {
                int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
                if (dark) {
                    systemUiVisibility |= SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    systemUiVisibility &= ~SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                window.getDecorView().setSystemUiVisibility(systemUiVisibility);
            }
        }
    }

    public static class XiaomiStatusbar {
        public static void setStatusBarDarkMode(boolean darkmode, Window window) {
            Class<? extends Window> clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void hideQQSystemBar(Activity activity){
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();
        for (int i = 0;i < viewGroup.getChildCount();i++){
            View view = viewGroup.getChildAt(i);
            if (view.getId() == -1 && view.getClass() == View.class){
                view.setAlpha(0);
                break;
            }
        }
    }
}
