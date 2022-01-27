package me.qiwu.colorqq.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import me.qiwu.colorqq.R;

public class TitleBar extends RelativeLayout implements View.OnClickListener {
    private TextView mTitleBarText;
    private ImageView mExit;
    private ImageView mTips;
    private String mTipText;
    private TextView mRightText;
    private ImageView mRightIcon2;

    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_title_bar,this);
        mTitleBarText = view.findViewById(R.id.title_bar_text);
        mExit = view.findViewById(R.id.title_bar_exit);
        mTips = view.findViewById(R.id.title_bar_tip);
        mRightText = view.findViewById(R.id.title_bar_right_text);
        mRightIcon2 = view.findViewById(R.id.title_bar_icon2);
        mTips.setOnClickListener(this);
        mExit.setOnClickListener(this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        if (typedArray != null){
            mTipText = typedArray.getString(R.styleable.TitleBar_tip_text);
            mTitleBarText.setText(typedArray.getString(R.styleable.TitleBar_title_text));
            if (typedArray.getBoolean(R.styleable.TitleBar_is_show_tip,false)){
                mTips.setVisibility(VISIBLE);
            } else {
                mTips.setVisibility(GONE);
            }
            if (typedArray.getBoolean(R.styleable.TitleBar_is_show_exit,true)){
                mExit.setVisibility(VISIBLE);
            } else {
                mExit.setVisibility(GONE);
            }
            if (!typedArray.getBoolean(R.styleable.TitleBar_is_show_driver,true)){
                view.findViewById(R.id.title_bar_driver).setVisibility(GONE);
            }
            typedArray.recycle();
        }

    }


    @Override
    public void onClick(View view) {
        if (view == mExit){
            ((Activity)getContext()).finish();
        } else if (view == mTips){
            new AlertDialog.Builder(getContext())
                    .setTitle("提示")
                    .setMessage(mTipText)
                    .setPositiveButton("确定",null)
                    .create().show();

        }
    }

    public void setTitle(CharSequence charSequence){
        mTitleBarText.setText(charSequence);
    }

    public void setRightText(CharSequence charSequence,OnClickListener onClickListener){
        mRightText.setVisibility(VISIBLE);
        mTips.setVisibility(GONE);
        mRightText.setText(charSequence);
        mRightText.setOnClickListener(onClickListener);
    }

    public void setRightImage(Drawable drawable,OnClickListener onClickListener){
        mRightText.setVisibility(GONE);
        mTips.setVisibility(VISIBLE);
        mTips.setImageDrawable(drawable);
        mTips.setOnClickListener(onClickListener);
    }

    public void setRightImage2(Drawable drawable,OnClickListener onClickListener){
        mRightText.setVisibility(GONE);
        mRightIcon2.setVisibility(VISIBLE);
        mRightIcon2.setImageDrawable(drawable);
        mRightIcon2.setOnClickListener(onClickListener);
    }

    public void setRightTextEnable(boolean enable){
        mRightText.setEnabled(enable);
        mRightText.setTextColor(enable ? Color.BLACK : Color.GRAY);
    }

    public ImageView getRightIcon2() {
        return mRightIcon2;
    }

    public ImageView getLeftIcon() {
        return mExit;
    }

    public ImageView getRightIcon() {
        return mTips;
    }
}
