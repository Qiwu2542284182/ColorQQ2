package me.qiwu.colorqq.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.android.material.card.MaterialCardView;

import me.qiwu.colorqq.util.DensityUtil;

public class LimitCardView extends MaterialCardView {
    public LimitCardView(Context context) {
        super(context);
    }

    public LimitCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LimitCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getTag()!=null){
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DensityUtil.dip2px(getContext(),Integer.valueOf(getTag().toString())), MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
