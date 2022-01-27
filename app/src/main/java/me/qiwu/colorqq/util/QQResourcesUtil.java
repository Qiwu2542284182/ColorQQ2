package me.qiwu.colorqq.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.qiwu.colorqq.manager.ActivityManager;

public class QQResourcesUtil {
    private static QQResourcesUtil mQQResourcesUtil;
    private List<QQResItem> mTextColorItems;
    private List<QQResItem> mIconItems;
    private Resources mQQResources;
    private static int qq_code = 0;


    public static QQResourcesUtil getInstance(){
        synchronized (QQResourcesUtil.class){
            if (mQQResourcesUtil == null){
                mQQResourcesUtil = new QQResourcesUtil();
            }
        }
        return mQQResourcesUtil;
    }

    public void checkUpdate(){
        try {
            int code = ActivityManager.getInstance().getCurrentActivity().getPackageManager().getPackageInfo("com.tencent.mobileqq", PackageManager.GET_SIGNATURES).versionCode;
            if (code != qq_code){
                mQQResources = null;
                mTextColorItems = null;
                mIconItems = null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Resources getQQResources() {
        if (mQQResources == null){
            Context qqContext = null;
            try {
                qqContext = ActivityManager.getInstance().getCurrentActivity().createPackageContext("com.tencent.mobileqq",Context.CONTEXT_INCLUDE_CODE|Context.CONTEXT_IGNORE_SECURITY);
            }catch (Exception e){
                return null;
            }
            mQQResources = qqContext.getResources();
            Configuration configuration = mQQResources.getConfiguration();
            configuration.uiMode = Configuration.UI_MODE_NIGHT_YES;
            mQQResources.updateConfiguration(configuration,mQQResources.getDisplayMetrics());
        }
        return mQQResources;
    }

    public List<QQResItem> getIconItems() {
        if (mIconItems == null){
            mIconItems = new ArrayList<>();
            int startId = -1;
            Resources resources = getQQResources();
            int skin_header_bar_bg = resources.getIdentifier("skin_header_bar_bg","drawable","com.tencent.mobileqq");
            String hex = String.valueOf((skin_header_bar_bg & 0x00ff0000) >> 16);
            String hexId = "7F" + (hex.length() > 1 ? hex : "0") + hex + "0000";
            startId = Integer.valueOf(hexId,16);
            if (startId != -1){
                TypedValue typedValue = new TypedValue();
                for (int i=0;i < 30000;i++){
                    try {
                        resources.getValue(startId,typedValue,true);
                        if (typedValue.type < 28 || typedValue.type >31){
                            String filePath = typedValue.string.toString();
                            if (filePath.endsWith(".png") || filePath.endsWith(".jpg")){
                                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                                mIconItems.add(new QQResItem(startId,fileName));
                            }
                        }
                        startId++;
                    }catch (Throwable t){
                        break;
                    }
                }
            }
            Collections.sort(mIconItems, (o1, o2) -> o1.resName.toLowerCase().compareTo(o2.resName.toLowerCase()));
        }
        return mIconItems;
    }

    public List<QQResItem> getTextColorItems() {
        if (mTextColorItems == null){
            mTextColorItems = new ArrayList<>();
            int startId = -1;
            Resources resources = getQQResources();
            int skin_color_title_immersive_bar = resources.getIdentifier("skin_color_title_immersive_bar","color","com.tencent.mobileqq");
            String hex = String.valueOf((skin_color_title_immersive_bar & 0x00ff0000) >> 16);
            String hexId = "7F" + (hex.length() > 1 ? hex : "0") + hex + "0000";
            startId = Integer.valueOf(hexId,16);
            if (startId != -1){
                TypedValue typedValue = new TypedValue();
                for (int i=0;i < 10000;i++){
                    try {
                        resources.getValue(startId,typedValue,true);
                        if (typedValue.type < 28 || typedValue.type >31){
                            String filePath = typedValue.string.toString();
                            if (filePath.endsWith(".xml")){
                                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                                mTextColorItems.add(new QQResItem(startId,fileName));
                            }
                        }
                        startId++;
                    }catch (Throwable t){
                       break;
                    }
                }
            }
            Collections.sort(mTextColorItems, (o1, o2) -> o1.resName.toLowerCase().compareTo(o2.resName.toLowerCase()));
        }
        return mTextColorItems;
    }

    public class QQResItem{
        public int id;
        public String resName;
        public QQResItem(int id,String resName){
            this.id = id;
            this.resName = resName;

        }
    }
}
