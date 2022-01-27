package me.qiwu.colorqq.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.util.DensityUtil;
import me.qiwu.colorqq.util.SettingUtil;

public class SwitchPreference extends RelativeLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private SwitchButton mSwitch;
    private TextView mTitle;
    private TextView mSummary;
    private String key;

    public SwitchPreference(Context context) {
        this(context,null);
    }

    public SwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_switch_preference,this);
        setOnClickListener(this);
        setBackgroundColor(context.getColor(R.color.white));
        mSwitch = view.findViewById(R.id.switch_switch);
        mSwitch.setOnCheckedChangeListener(this);
        mSwitch.setThumbDrawable(getThumbDrawable());
        mTitle = view.findViewById(R.id.switch_title);
        mSummary = view.findViewById(R.id.switch_summary);
        if (attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.SwitchPreference);
            key = typedArray.getString(R.styleable.SwitchPreference_key);
            mSwitch.setChecked(SettingUtil.getInstance().getBoolean(key,false));
            mTitle.setText(typedArray.getString(R.styleable.SwitchPreference_title));
            mSwitch.setBackDrawable(getBackDrawable(typedArray.getColor(R.styleable.SwitchPreference_switch_color,getContext().getColor(R.color.colorAccent))));
            mSummary.setText(typedArray.getString(R.styleable.SwitchPreference_summary));
            if (TextUtils.isEmpty(mSummary.getText())){
                mSummary.setVisibility(GONE);
            }
            typedArray.recycle();
        }
    }

    @Override
    public void onClick(View view) {
        mSwitch.toggle();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("Parcelable",super.onSaveInstanceState());
        bundle.putBoolean("isChecked",mSwitch.isChecked());
        return bundle;
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener checkedChangeListener){
        mSwitch.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable("Parcelable"));
            mSwitch.setChecked(bundle.getBoolean("isChecked"));

        }else {
            super.onRestoreInstanceState(state);
        }
    }

    public void setChecked(boolean isChecked){
        mSwitch.setChecked(isChecked);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        //super.dispatchRestoreInstanceState(container);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SettingUtil.getInstance().putBoolean(key,isChecked);
    }

    private Drawable getThumbDrawable(){
        GradientDrawable normal = new GradientDrawable();
        normal.setColor(Color.parseColor("#fff6f6f6"));
        normal.setShape(GradientDrawable.OVAL);

        GradientDrawable press = new GradientDrawable();
        press.setColor(Color.parseColor("#ffeaeaea"));
        press.setShape(GradientDrawable.OVAL);


        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed}, press);
        bg.addState(new int[]{}, normal);

        return bg;
    }

    private Drawable getBackDrawable(int color){
        GradientDrawable normal = new GradientDrawable();
        normal.setColor(Color.parseColor("#eaeaea"));
        normal.setCornerRadius(DensityUtil.dip2px(getContext(),99));

        GradientDrawable checked = new GradientDrawable();
        checked.setColor(color);
        checked.setCornerRadius(DensityUtil.dip2px(getContext(),99));

        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_checked,-android.R.attr.enabled}, checked);
        bg.addState(new int[]{}, normal);

        return bg;
    }
}
