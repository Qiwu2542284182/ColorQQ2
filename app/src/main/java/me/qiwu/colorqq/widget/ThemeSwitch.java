package me.qiwu.colorqq.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import com.kyleduo.switchbutton.SwitchButton;

import me.qiwu.colorqq.util.DensityUtil;

public class ThemeSwitch extends SwitchButton {
    public ThemeSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ThemeSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThemeSwitch(Context context) {
        super(context);
        init();
    }

    private void init(){
        setThumbDrawable(getThumbDrawable());
        setBackDrawable(getBackDrawable(Color.parseColor("#949cff")));
        setClickable(false);
    }

    public Drawable getThumbDrawable(){
        GradientDrawable normal = new GradientDrawable();
        normal.setColor(Color.parseColor("#fff6f6f6"));
        normal.setShape(GradientDrawable.OVAL);

        GradientDrawable press = new GradientDrawable();
        press.setColor(Color.parseColor("#ffeaeaea"));
        press.setShape(GradientDrawable.OVAL);


        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed}, press);
        bg.addState(new int[]{}, normal);

        return bg;
    }

    private Drawable getBackDrawable(int color){
        GradientDrawable normal = new GradientDrawable();
        normal.setColor(Color.parseColor("#eaeaea"));
        normal.setCornerRadius(DensityUtil.dip2px(getContext(),99));

        GradientDrawable checked = new GradientDrawable();
        checked.setColor(color);
        checked.setCornerRadius(DensityUtil.dip2px(getContext(),99));

        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_checked,-android.R.attr.enabled}, checked);
        bg.addState(new int[]{}, normal);

        return bg;
    }
}
