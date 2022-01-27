package me.qiwu.colorqq.widget;

import android.app.AndroidAppHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import androidx.cardview.widget.CardView;

import me.qiwu.colorqq.XHook.XSettingUtils;

public class MainCardView extends CardView {
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                if (intent.getAction().equals("SET_ELEVATION")){
                    setCardElevation(20f);
                } else if (intent.getAction().equals("SET_RADIUS")){
                    setRadius(Float.valueOf(XSettingUtils.getString("drawer_corner","0")));
                }
            }
        }
    };

    public MainCardView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SET_ELEVATION");
        intentFilter.addAction("SET_RADIUS");
        intentFilter.addAction("SET_ELEVATION_AND_RADIUS");
        AndroidAppHelper.currentApplication().registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidAppHelper.currentApplication().unregisterReceiver(broadcastReceiver);
    }
}
