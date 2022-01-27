package me.qiwu.colorqq.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;

import me.qiwu.colorqq.util.FileUtil;

public class PanelImageView extends FrameLayout {
    private ImageView mImageView;
    private boolean isEnable = true;
    private boolean isEnableTint = true;
    public PanelImageView(Context context) {
        super(context);
        init();
    }

    private void init(){
        mImageView = new ImageView(getContext());
        mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mImageView,layoutParams);
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);

    }

    public void setBackgroundResource(int resid, String fileName) {
        File file = new File(FileUtil.getInputIcon(fileName));
        if (file.exists()){
            isEnableTint = false;
            mImageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } else {
            mImageView.setImageResource(resid);
        }

    }

    @Override
    public void setBackground(Drawable background) {
        mImageView.setImageDrawable(background);
    }

    public void setColorFilter(int color){
        if (isEnableTint)
            mImageView.setColorFilter(color);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isEnable){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setAlpha(0.3f);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    setAlpha(1.0f);
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        isEnable = enabled;
    }
}
