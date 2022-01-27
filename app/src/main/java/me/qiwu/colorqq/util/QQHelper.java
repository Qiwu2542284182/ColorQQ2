package me.qiwu.colorqq.util;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.colorqq.BuildConfig;
import me.qiwu.colorqq.XHook.XField;
import me.qiwu.colorqq.XHook.XUtils;
import me.qiwu.colorqq.XHook.XposedCompact;

import static me.qiwu.colorqq.util.ClassUtil.SessionInfo;

/**
 * Created by Deng on 2019/3/11.
 */

public class QQHelper {
    private static String sQQAPP_GET_FACE_HEAD;
    private static int FLAG_TroopManager;

    public static void init(){
        Class<?> mQQAppInterface = XUtils.findClass(ClassUtil.QQAppInterface);
        try {
            mQQAppInterface.getMethod("getFaceBitmap",String.class,boolean.class);
            sQQAPP_GET_FACE_HEAD = "getFaceBitmap";
        } catch (Throwable throwable){
            sQQAPP_GET_FACE_HEAD = "a";
        }
    }


    public static Class<?>findClass(String className){
        return XposedHelpers.findClass(className, XposedCompact.classLoader);
    }

    public static Context getContext(){
        return AndroidAppHelper.currentApplication().getApplicationContext();
    }

    public static Context getBaseContext(){
        return AndroidAppHelper.currentApplication().getBaseContext();
    }

    public static Resources getResources(){
        return AndroidAppHelper.currentApplication().getApplicationContext().getResources();
    }

    public static SharedPreferences getSharedPreferences(){
        return AndroidAppHelper.currentApplication().getApplicationContext().getSharedPreferences(BuildConfig.APPLICATION_ID,0);
    }

    public static SharedPreferences getCurrentVersionClass(){
        try {
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo("com.tencent.mobileqq", 0);
            return getContext().getSharedPreferences("class_"+packageInfo.versionName + "_" + packageInfo.versionCode,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static int getQQVersionCode() {
        try {
            return getContext().getPackageManager()
                    .getPackageInfo("com.tencent.mobileqq", 0).versionCode;
        } catch (Throwable e) {
            return 0;
        }
    }


    public static String getQQVersionName() {
        try {
            return getContext().getPackageManager()
                    .getPackageInfo("com.tencent.mobileqq", 0).versionName;
        } catch (Throwable e) {
            return "0";
        }
    }

    public static Object getQQAppInterface(){
        Class<?> mBaseApplicationImpl = XposedHelpers.findClass("com.tencent.common.app.BaseApplicationImpl",XposedCompact.classLoader);
        return XposedHelpers.callMethod(XposedHelpers.callStaticMethod(mBaseApplicationImpl,"getApplication"),"getRuntime");
    }

    public static Object getTroopManager() {
        if (FLAG_TroopManager == 0){
            Class<?> clazz = XUtils.findClass("com.tencent.mobileqq.app.QQManagerFactory");
            if (clazz != null){
                FLAG_TroopManager = XposedHelpers.getStaticIntField(clazz,"TROOP_MANAGER");
            } else {
                Object troopManager = getManager(52);
                if (troopManager == null){
                    return null;
                } else if (!troopManager.getClass().getName().contains("TroopManager")){
                    FLAG_TroopManager = 51;
                    troopManager = getManager(51);
                } else {
                    FLAG_TroopManager = 52;
                }

                return troopManager;
            }

        }
        return getManager(FLAG_TroopManager);
    }


    public static Object getManager(int i){
        Object o = getQQAppInterface();
        if (o == null) return null;
        return XposedHelpers.callMethod(o,"getManager",i);
    }

    public static String getCurrentAccountUin(){
        return (String) XposedHelpers.callMethod(getQQAppInterface(),"getCurrentAccountUin");
    }

    public static Activity getActivity(){
        return (Activity) XposedHelpers.getStaticObjectField(XposedHelpers.findClass("com.tencent.mobileqq.app.BaseActivity",XposedCompact.classLoader),"sTopActivity");
    }

    public static String getCurrentNickname(){
        return (String) XposedHelpers.callMethod(getQQAppInterface(),"getCurrentNickname");
    }

    public static String getSignature(){
        try {
            Method method = null;
            try {
                method = XposedHelpers.findClass(ClassUtil.QQAppInterface,XposedCompact.classLoader).getMethod("getExtensionInfo",
                        String.class,boolean.class);
            } catch (Throwable throwable){
                method = findMethodIfExists(XposedHelpers.findClass(ClassUtil.QQAppInterface,XposedCompact.classLoader),
                        XposedHelpers.findClass(ClassUtil.ExtensionInfo,XposedCompact.classLoader),
                        "a",
                        String.class,boolean.class);
            }
            method.setAccessible(true);

            Object extensionInfo = method.invoke(getQQAppInterface(),getCurrentAccountUin(),true);
            if (extensionInfo!=null){
                ArrayList arrayList = (ArrayList)XposedHelpers.getObjectField(XposedHelpers.callMethod(extensionInfo,"getRichStatus"),"plainText");
                if (arrayList!=null&&!arrayList.isEmpty()){
                    return arrayList.get(0).toString();
                }
            }


        } catch (Throwable e){
            XposedBridge.log("错误"+ Log.getStackTraceString(e));
        }
        return "编辑个性签名";
    }


    public static Method findMethodIfExists(Class<?> clazz, Class<?> returnType, String methodName, Class<?>... parameterTypes) {
        if (clazz != null && returnType != null && !methodName.isEmpty()) {
            Class<?> clz = clazz;
            do {
                Method[] methods = XposedHelpers.findMethodsByExactParameters(clazz, returnType, parameterTypes);
                for (Method method : methods) {
                    if (method.getName()
                            .equals(methodName)) {
                        return method;
                    }
                }
            } while ((clz = clz.getSuperclass()) != null);
        }
        return null;
    }


    public static Drawable getFriendHead(String uin){
        return new BitmapDrawable(getContext().getResources(),(Bitmap)XposedHelpers.callMethod(getQQAppInterface(),sQQAPP_GET_FACE_HEAD, 1,uin, true,0));
    }

    public static Drawable getDiscussionHead(String uin){
        return new BitmapDrawable(getContext().getResources(),(Bitmap)XposedHelpers.callMethod(getQQAppInterface(),sQQAPP_GET_FACE_HEAD, 113, uin, true,0));
    }

    public static Drawable getTroopHead(String uin){
        return new BitmapDrawable(getContext().getResources(),(Bitmap)XposedHelpers.callMethod(getQQAppInterface(),sQQAPP_GET_FACE_HEAD, 4, uin, true,0));
    }

    public static Drawable getHead(String uin,int type){
        if (type == 0 || type ==1000){
            return QQHelper.getFriendHead(uin);
        } else if (type == 1 ){
            return QQHelper.getTroopHead(uin);
        } else if (type == 3000){
            return QQHelper.getDiscussionHead(uin);
        }
        return null;
    }

    public static Object getFragmentByName(Activity activity,String name){
        Object fragmentManager = XposedHelpers.callMethod(activity,"getSupportFragmentManager");
        ArrayList mAdded = (ArrayList) XposedHelpers.getObjectField(fragmentManager,"mAdded");
        Object mainFragment = null;
        for (int size = mAdded.size() - 1;size >= 0;size--){
            Object fragment = mAdded.get(size);
            String tag = (String) XposedHelpers.getObjectField(fragment,"mTag");
            if (tag.endsWith(name) || tag.equals(name)){
                mainFragment = fragment;
                break;
            }
        }
        if (mainFragment == null){
            ArrayList mActive = (ArrayList) XposedHelpers.getObjectField(fragmentManager,"mActive");
            for (int size = mActive.size() - 1;size >= 0;size--){
                Object fragment = mActive.get(size);
                String tag = (String) XposedHelpers.getObjectField(fragment,"mTag");
                if (tag.endsWith(name) || tag.equals(name)){
                    mainFragment = fragment;
                    break;
                }
            }
        }
        return mainFragment;
    }
}
