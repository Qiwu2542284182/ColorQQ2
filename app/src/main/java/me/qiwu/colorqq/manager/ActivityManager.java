package me.qiwu.colorqq.manager;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by  on 2018/10/18.
 */

public class ActivityManager {
    private static ActivityManager sInstance ;
    private static List<Activity> activities =new ArrayList<>();
    private WeakReference<Activity> sCurrentActivityWeakRef;
    private ActivityManager() {

    }
    public static ActivityManager getInstance() {
        if (null == sInstance){
            sInstance = new ActivityManager();
        }
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
        activities.add(activity);
    }

    public void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public void finishAllActivity(){
        for (Activity activity : activities){
            activity.finish();
        }
    }

}
