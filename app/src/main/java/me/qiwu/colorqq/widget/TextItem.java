package me.qiwu.colorqq.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.util.DialogUtil;
import me.qiwu.colorqq.util.SettingUtil;

public class TextItem extends RelativeLayout implements View.OnClickListener {
    private TextView mLeftText;
    private TextView mRightText;
    private String key;
    private String unit;

    public TextItem(Context context) {
        this(context,null);
    }

    public TextItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_text_item,this);
        setOnClickListener(this);
        mLeftText = view.findViewById(R.id.text_item_left);
        mRightText = view.findViewById(R.id.text_item_right);
        setBackgroundColor(context.getColor(R.color.white));
        if (attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.TextItem);
            if (typedArray!=null){
                mLeftText.setText(typedArray.getString(R.styleable.TextItem_left_text));
                unit = typedArray.getString(R.styleable.TextItem_unit);
                mRightText.setText(hasUnit() ? typedArray.getString(R.styleable.TextItem_right_text) + unit :typedArray.getString(R.styleable.TextItem_right_text));
                key = typedArray.getString(R.styleable.TextItem_text_key);
                if (!TextUtils.isEmpty(key)){
                    setRightText(SettingUtil.getInstance().getString(key,getDefaultValue()));
                } else {
                    setOnClickListener(v -> { });
                }
                typedArray.recycle();
            }
        }
    }

    public void setLeftText(CharSequence charSequence){
        mLeftText.setText(charSequence);
    }

    public void setRightText(CharSequence charSequence){
        mRightText.setText(hasUnit() ? charSequence +unit: charSequence);
    }

    public void setLeftTextColor(@ColorInt int color){
        mLeftText.setTextColor(color);
    }

    public void setRightTextColor(@ColorInt int color){
        mRightText.setTextColor(color);
    }

    public String getDefaultValue(){
        return hasUnit() ? mRightText.getText().toString().replace(unit,"") : mRightText.getText().toString() ;
    }

    public boolean hasUnit(){
        return !TextUtils.isEmpty(unit);
    }

    public String getKey(){
        return key;
    }

    @Override
    public void onClick(View v) {
        DialogUtil.showEditViewDialog(getContext(),mLeftText.getText().toString(),key,this);
    }
}
