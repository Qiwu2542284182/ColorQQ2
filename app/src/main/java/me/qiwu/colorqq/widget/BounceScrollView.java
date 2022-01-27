package me.qiwu.colorqq.widget;

/**
 * Created by Deng on 2019/4/11.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;

public class BounceScrollView extends ScrollView {
    // 这个值控制可以把ScrollView包裹的控件拉出偏离顶部或底部的距离。
    private static final int MAX_OVER_SCROLL_Y = 100;
    private int newMaxOverScrollY;

    public BounceScrollView(Context context) {
        super(context);
    }

    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BounceScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float density = metrics.density;
        newMaxOverScrollY = (int) (density * MAX_OVER_SCROLL_Y);
        //false:隐藏ScrollView的滚动条。
        this.setVerticalScrollBarEnabled(false);
        //不管装载的控件填充的数据是否满屏，都允许橡皮筋一样的弹性回弹。
        this.setOverScrollMode(ScrollView.OVER_SCROLL_ALWAYS);
    }
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        // Not consumed means it wasn't handled because ScrollView
        // doesn't take over scrolling bounds into scroll range,
        // so we fling it ourselves to get it bounce back
        if (getOverScrollMode() == OVER_SCROLL_ALWAYS && !consumed) {
            fling((int) velocityY);
            return true;
        } else {
            return super.dispatchNestedFling(velocityX, velocityY, consumed);
        }
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        //增加阻尼效果,使滑动有吃力感
        double ratio = 1d;
        //在顶部并且是向下拖动
        if (deltaY < 0 && scrollY + deltaY < 0) {
            ratio = 1.05d + scrollY / (newMaxOverScrollY * 1.2);
        } else if (deltaY > 0 && scrollY + deltaY > scrollRangeY) { //滑动到底部并且向下滑动
            ratio = 1.05d + (scrollRangeY - scrollY) / (newMaxOverScrollY * 1.2);
        }
        return super.overScrollBy(deltaX, (int) (deltaY * ratio), scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX, newMaxOverScrollY,
                isTouchEvent);
    }
}
