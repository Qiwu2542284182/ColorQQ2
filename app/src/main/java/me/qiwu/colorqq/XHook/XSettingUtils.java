package me.qiwu.colorqq.XHook;



import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XSharedPreferences;
import me.qiwu.colorqq.BuildConfig;
import me.qiwu.colorqq.util.FileUtil;

/**
 * Created by Deng on 2018/4/26.
 */

public class XSettingUtils {


    private static Map datas;
    private static boolean hasSettingFile = true;

    static {
        File file = getSettingFile();
        if (file.exists() && file.canRead() && file.isFile()){
            try {
                datas = new XSharedPreferences(file).getAll();
                //datas = NativeInterface.getSettingXml(new FileInputStream(file),XposedCompact.processName.contains(":") ? null : QQHelper.getTroopManager());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }  else {
            hasSettingFile = false;
        }
        if (datas == null){
            datas = new HashMap();
        }

    }

    public static boolean hasSettingFile() {
        return hasSettingFile;
    }

    public static boolean getBoolean(String key){
        return getBoolean(key,false);
    }

    public static boolean getBoolean(String key,boolean defValue){
        Boolean v = (Boolean)datas.get(key);
        return v != null ? v : defValue;
    }

    public static int getInt(String key){
        return getInt(key,0);
    }

    public static int getInt(String key,int defValue){
        Integer v = (Integer)datas.get(key);
        return v != null ? v : defValue;
    }

    public static String getString(String key){
        return getString(key,"");
    }

    public static String getString(String key,String defealtValue){
        String v = (String)datas.get(key);
        return v != null ? v : defealtValue;
    }



    private  static File getSettingFile(){
        File file = new File(FileUtil.getModulePath()+"setting.xml");
        if (file.exists()){
            return file;
        }
        return new File(Environment.getDataDirectory(), "data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + BuildConfig.APPLICATION_ID + ".xml");
    }
}
