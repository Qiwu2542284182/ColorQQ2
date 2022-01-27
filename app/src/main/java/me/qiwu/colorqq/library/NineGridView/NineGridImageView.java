package me.qiwu.colorqq.library.NineGridView;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.scwang.smart.refresh.layout.util.DesignUtil;

import me.qiwu.colorqq.library.RCLayout.RCImageView;
import me.qiwu.colorqq.util.DensityUtil;

/**
 * ImageView which has click effect
 */
public class NineGridImageView extends AppCompatImageView {
    public NineGridImageView(Context context) {
        super(context);
        init();
    }

    public NineGridImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setClickable(true);
        setScaleType(ScaleType.CENTER_CROP);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), DensityUtil.dip2px(getContext(),10));
            }
        });
        setClipToOutline(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setAlpha(0.7f);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setAlpha(1.0f);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
