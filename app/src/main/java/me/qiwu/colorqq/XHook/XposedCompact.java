package me.qiwu.colorqq.XHook;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;

import de.robv.android.xposed.XposedBridge;
import me.qiwu.colorqq.BuildConfig;
import me.qiwu.colorqq.R;
import me.qiwu.colorqq.util.QQHelper;

public class XposedCompact {
    public static ClassLoader classLoader;
    public static String processName;

    public static Context getModuleContext(Context context){
        Context moduleContext = null;
        try {
            moduleContext = context.createPackageContext(BuildConfig.APPLICATION_ID,Context.CONTEXT_INCLUDE_CODE|Context.CONTEXT_IGNORE_SECURITY);
        }catch (Exception e){
            XposedBridge.log(e);
        }
        return moduleContext;
    }

    public static Context getModuleContextThemeWrapper(Context context){
        Context moduleContext = getModuleContext(context);
        if (moduleContext != null){
            return new ContextThemeWrapper(moduleContext, R.style.AppTheme);
        }
        return null;
    }

    public static Drawable getModuleDrawable(int id){
        return getModuleContext(QQHelper.getContext()).getDrawable(id);
    }
}
