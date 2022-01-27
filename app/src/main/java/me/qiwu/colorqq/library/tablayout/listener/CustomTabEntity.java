package me.qiwu.colorqq.library.tablayout.listener;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;

public interface CustomTabEntity {
    String getTabTitle();

    @DrawableRes
    int getTabSelectedIcon();

    @DrawableRes
    int getTabUnselectedIcon();

    Drawable getTabIcon();


    class TabCustomData implements CustomTabEntity {

        @Override
        public String getTabTitle() {
            return null;
        }

        @Override
        public int getTabSelectedIcon() {
            return 0;
        }

        @Override
        public int getTabUnselectedIcon() {
            return 0;
        }

        @Override
        public Drawable getTabIcon() {
            return null;
        }
    }
}