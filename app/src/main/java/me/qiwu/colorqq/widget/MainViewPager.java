package me.qiwu.colorqq.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;


import androidx.viewpager.widget.ViewPager;

import java.lang.ref.WeakReference;

import de.robv.android.xposed.XposedHelpers;

public class MainViewPager extends ViewPager {
    public static WeakReference<Object> sDrawerCache;

    public MainViewPager(Context context) {
        super(context);
    }


   @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN){
            //
            int x = (int) (event.getX() + 0.5f);
            if (x >getWidth()*0.75 && getCurrentItem()==0){
                return false;
            } else {
                if (x >getWidth() * 0.3 && getCurrentItem() == 2 ){
                    setDrawerEnable(false);
                } else if (x <getWidth() * 0.3 && getCurrentItem() == 1){
                    setDrawerEnable(true);
                } else if (getCurrentItem() == 2){
                    setDrawerEnable(true);
                }
            }
        } else {
            setDrawerEnable(true);
        }
        return super.onInterceptTouchEvent(event);
    }



    //修复QQ860以上的滑动冲突
    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
       if (getCurrentItem() == 1){
           if (v instanceof HorizontalScrollView){
               return true;
           } else if (v.getClass().getName().endsWith("ViewPager")){
               return false;
           }
       }
        return super.canScroll(v, checkV, dx, x, y);
    }

    private void setDrawerEnable(boolean enable){
        if (sDrawerCache != null){
            Object drawer = sDrawerCache.get();
            if (drawer != null){
               // XposedBridge.log("setDrawerEnable " + (enable ? "true" : "false"));
                XposedHelpers.callMethod(drawer,"setDrawerEnabled",enable);
            }
        }
    }


}


