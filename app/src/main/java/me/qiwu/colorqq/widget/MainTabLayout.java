package me.qiwu.colorqq.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.XHook.XSettingUtils;
import me.qiwu.colorqq.util.QQHelper;

public class MainTabLayout extends TabLayout implements TabLayout.OnTabSelectedListener {
    private int unreadTextColor;
    private int unreadBgColor = Color.WHITE;

    public MainTabLayout(Context context) {
        super(context);
        addOnTabSelectedListener(this);
    }

    @NonNull
    @Override
    public Tab newTab() {
        Tab tab = super.newTab();
        ViewGroup tabView = (ViewGroup) View.inflate(getContext(), R.layout.layout_tab_top, null);
        TextView textView = tabView.findViewById(R.id.tv_tab_num);
        textView.setTextColor(unreadTextColor);
        Drawable drawable = getContext().getDrawable(R.drawable.ic_tab_tips);
        drawable.setColorFilter(unreadBgColor, PorterDuff.Mode.SRC_IN);
        textView.setBackground(drawable);
        tab.setCustomView(tabView);
        return tab;
    }

    protected int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setIcon(int position, Drawable drawable){
        if (getTabAt(position)!=null){
            ImageView imageView = getTabAt(position).getCustomView().findViewById(R.id.iv_tab_icon);
            imageView.setImageDrawable(drawable);
            if (XSettingUtils.getBoolean("tab_tint")){
                imageView.setColorFilter(XSettingUtils.getInt("tab_tint_color",Color.WHITE));
            }
            getTabAt(position).getCustomView().findViewById(R.id.tv_tab_title).setVisibility(GONE);
        }

    }

    public void setText(int position, CharSequence title){
        if (getTabAt(position)!=null){
            TextView textView = getTabAt(position).getCustomView().findViewById(R.id.tv_tab_title);
            textView.setText(title);
            textView.setTextColor(XSettingUtils.getInt("tab_text_color",Color.WHITE));
            textView.setTextSize(Float.valueOf(XSettingUtils.getString("tab_text_size","15")));
            getTabAt(position).getCustomView().findViewById(R.id.iv_tab_icon).setVisibility(GONE);
        }

    }


    public void setUnreadTextColor(int color){
        this.unreadTextColor = color;
    }

    public void setUnreadBgColor(int color){
        unreadBgColor = color;
    }

    public void hideMsg(int position){
        getTabAt(position).getCustomView().findViewById(R.id.tv_tab_num).setVisibility(GONE);
    }

    public void showMsg(int position,int num){
        String s = "";
        if (num > 99){
            s = "99+";
        } else {
            s = String.valueOf(num);
        }
        TextView textView = getTabAt(position).getCustomView().findViewById(R.id.tv_tab_num);
        textView.setVisibility(VISIBLE);
        textView.setText(s);
    }


    @Override
    public void onTabSelected(Tab tab) {
        ImageView imageView = tab.getCustomView().findViewById(R.id.iv_tab_icon);
        if (imageView.getVisibility()==VISIBLE){
            imageView.setAlpha(1f);
        }
        TextView textView = tab.getCustomView().findViewById(R.id.tv_tab_title);
        if (textView.getVisibility()==VISIBLE){
            textView.setAlpha(1f);
        }
    }

    @Override
    public void onTabUnselected(Tab tab) {
        ImageView imageView = tab.getCustomView().findViewById(R.id.iv_tab_icon);
        if (imageView.getVisibility()==VISIBLE){
            imageView.setAlpha(0.85f);
        }

        TextView textView = tab.getCustomView().findViewById(R.id.tv_tab_title);
        if (textView.getVisibility()==VISIBLE){
            textView.setAlpha(0.85f);
        }
    }

    @Override
    public void onTabReselected(Tab tab) {

    }
}
