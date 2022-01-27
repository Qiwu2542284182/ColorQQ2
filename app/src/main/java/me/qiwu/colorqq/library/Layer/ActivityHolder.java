package me.qiwu.colorqq.library.Layer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Stack;

/**
 * @author CuiZhen
 * @date 2019/3/10
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
final class ActivityHolder implements Application.ActivityLifecycleCallbacks {
    private static ActivityHolder INSTANCE = null;

    private Stack<Activity> mActivityStack = new Stack<>();

    private ActivityHolder( Application application){
        application.registerActivityLifecycleCallbacks(this);
    }

    static void init( Application application){
        if (INSTANCE == null) {
            INSTANCE = new ActivityHolder(application);
        }
    }

    static Activity currentActivity(){
        if (INSTANCE == null) {
            return null;
        }
        if (INSTANCE.mActivityStack.empty()) {
            return null;
        }
        return INSTANCE.mActivityStack.peek();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityStack.push(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityStack.pop();
    }
}
