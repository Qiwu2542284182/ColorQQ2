package me.qiwu.colorqq.widget;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import me.qiwu.colorqq.XHook.XUtils;
import me.qiwu.colorqq.manager.ActivityManager;
import me.qiwu.colorqq.util.DensityUtil;
import me.qiwu.colorqq.util.StatusBarUtil;

public class ThemePreView extends androidx.appcompat.widget.AppCompatImageView {
    private static int width;
    private static int height;
    static {
        WindowManager windowManager = ActivityManager.getInstance().getCurrentActivity().getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int d_width = displayMetrics.widthPixels;
        width = (int) (d_width * 0.33f) - DensityUtil.dip2px(ActivityManager.getInstance().getCurrentActivity(),10);
        height = (int) (d_width * 0.4f);
    }

    public ThemePreView(Context context) {
        super(context);
        init();
    }

    public ThemePreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), DensityUtil.dip2px(getContext(),5));
            }
        });
        setClipToOutline(true);
    }
    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.width = width;
        params.height = height;
        super.setLayoutParams(params);
    }
}
