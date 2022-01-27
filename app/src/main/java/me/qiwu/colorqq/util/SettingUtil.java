package me.qiwu.colorqq.util;

import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

import me.qiwu.colorqq.BuildConfig;
import me.qiwu.colorqq.activity.BaseApplication;

public class SettingUtil {
    private static WeakReference<SharedPreferences> sharedPreferences = new WeakReference<>(null);
    private static SettingUtil settingUtil;

    public SettingUtil(){

    }

    public static SettingUtil getInstance(){
        synchronized (SettingUtil.class){
            if (settingUtil==null){
                settingUtil = new SettingUtil();

            }
            if (sharedPreferences.get()==null){
                sharedPreferences = new WeakReference<>(BaseApplication.getContext().getSharedPreferences(BuildConfig.APPLICATION_ID,0));
            }
        }
        return settingUtil;
    }

    public String getString(String key,String value){
        return sharedPreferences.get().getString(key, value);
    }

    public String getString(String key){
        return sharedPreferences.get().getString(key,"");
    }

    public int getInt(String key,int value){
        return sharedPreferences.get().getInt(key,value);
    }

    public int getInt(String key){
        return sharedPreferences.get().getInt(key,0);
    }

    public boolean getBoolean(String key,boolean value){
        return sharedPreferences.get().getBoolean(key, value);
    }

    public boolean getBoolean(String key){
        return sharedPreferences.get().getBoolean(key,false);
    }

    public SharedPreferences.Editor getEditor(){
        return sharedPreferences.get().edit();
    }

    public void putString(String key,String newValue){
        sharedPreferences.get().edit().putString(key, newValue).apply();
        FileUtil.setWorldReadable(BaseApplication.getContext());
    }

    public void putBoolean(String key,boolean newValue){
        sharedPreferences.get().edit().putBoolean(key, newValue).apply();
        FileUtil.setWorldReadable(BaseApplication.getContext());
    }

    public void putInt(String key,int newValue){
        sharedPreferences.get().edit().putInt(key, newValue).apply();
        FileUtil.setWorldReadable(BaseApplication.getContext());
    }

}
