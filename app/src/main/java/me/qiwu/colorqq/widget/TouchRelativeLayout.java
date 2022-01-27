package me.qiwu.colorqq.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class TouchRelativeLayout extends RelativeLayout implements View.OnTouchListener {
    public TouchRelativeLayout(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public TouchRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public TouchRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_CANCEL){
            v.setAlpha(1.0f);
            v.invalidate();
        } else if (action==MotionEvent.ACTION_DOWN){
            v.setAlpha(0.7f);
            v.invalidate();
        }
        return false;
    }
}
