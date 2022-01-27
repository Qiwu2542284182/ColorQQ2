package me.qiwu.colorqq.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;

public class FlexLayout extends SmartRefreshLayout {
    public FlexLayout(Context context) {
        super(context);
    }

    public FlexLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {

        //setDragRate(0.4f);
        setReboundDuration(400);
        setRefreshHeader(new RefreshHeader() {
            @NonNull
            @Override
            public View getView() {
                return new View(getContext());
            }

            @NonNull
            @Override
            public SpinnerStyle getSpinnerStyle() {
                return SpinnerStyle.FixedBehind;
            }

            @Override
            public void setPrimaryColors(int... colors) {

            }

            @Override
            public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

            }

            @Override
            public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

            }

            @Override
            public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

            }

            @Override
            public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

            }

            @Override
            public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
                return 0;
            }

            @Override
            public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

            }

            @Override
            public boolean isSupportHorizontalDrag() {
                return false;
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

            }
        });
        setRefreshFooter(new RefreshFooter() {
            @Override
            public boolean setNoMoreData(boolean noMoreData) {
                return false;
            }

            @NonNull
            @Override
            public View getView() {
                return new View(getContext());
            }

            @NonNull
            @Override
            public SpinnerStyle getSpinnerStyle() {
                return SpinnerStyle.FixedBehind;
            }

            @Override
            public void setPrimaryColors(int... colors) {

            }

            @Override
            public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

            }

            @Override
            public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

            }

            @Override
            public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

            }

            @Override
            public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

            }

            @Override
            public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
                return 0;
            }

            @Override
            public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

            }

            @Override
            public boolean isSupportHorizontalDrag() {
                return false;
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

            }
        });
        /*
        setEnableLoadMore(false);
        setEnableRefresh(false);
        setEnableAutoLoadMore(false);
        setEnableOverScrollBounce(true);*/
        setEnableOverScrollDrag(true);
        setEnablePureScrollMode(true);

        super.onFinishInflate();
    }
}
