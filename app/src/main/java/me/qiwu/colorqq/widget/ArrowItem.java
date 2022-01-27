package me.qiwu.colorqq.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.qiwu.colorqq.R;

public class ArrowItem extends RelativeLayout {
    private TextView mTitle;
    private TextView mSummary;
    private RelativeLayout mParent;
    public ArrowItem(Context context) {
        this(context,null);
    }

    public ArrowItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_arrow_item,this);
        setBackgroundColor(context.getColor(R.color.white));
        mParent = view.findViewById(R.id.arrow_parent);
        mTitle = view.findViewById(R.id.arrow_title);
        mSummary = view.findViewById(R.id.arrow_summary);
        if (attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.ArrowItem);
            if (typedArray!=null){
                mTitle.setText(typedArray.getString(R.styleable.ArrowItem_arrow_title));
                mSummary.setText(typedArray.getString(R.styleable.ArrowItem_arrow_summary));
                if (!typedArray.getBoolean(R.styleable.ArrowItem_arrow_show_ic,true)){
                    view.findViewById(R.id.arrow_arrow).setVisibility(GONE);
                }
                if (TextUtils.isEmpty(mSummary.getText())){
                    mSummary.setVisibility(GONE);
                }
            }
            typedArray.recycle();
        }

    }

    public void setTitle(CharSequence charSequence){
        mTitle.setText(charSequence);
    }

    public void setSummary(CharSequence charSequence){
        mSummary.setVisibility(VISIBLE);
        mSummary.setText(charSequence);
    }

    public CharSequence getSummary(){
        return mSummary.getText();
    }
    public void setSummaryMaxLine(){
        mSummary.setMaxLines(1);
    }

}
