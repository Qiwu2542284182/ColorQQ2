package me.qiwu.colorqq.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.activity.BaseActivity;
import me.qiwu.colorqq.manager.ActivityManager;
import me.qiwu.colorqq.util.DialogUtil;
import me.qiwu.colorqq.util.SettingUtil;

public class ColorPreference extends RelativeLayout implements View.OnClickListener , ColorPickerDialogListener {
    private int color;
    private String key;
    private boolean useAlpha;
    private ImageView colorBg;

    public ColorPreference(Context context) {
        this(context,null);
    }

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_color_preference,this);
        TextView textView = view.findViewById(R.id.color_item_text);
        colorBg = view.findViewById(R.id.color_item_color);
        view.setOnClickListener(this);
        setBackgroundColor(context.getColor(R.color.white));
        if (attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.ColorPreference);
            textView.setText(typedArray.getString(R.styleable.ColorPreference_color_text));
            key = typedArray.getString(R.styleable.ColorPreference_color_key);
            useAlpha = typedArray.getBoolean(R.styleable.ColorPreference_color_use_alpha,true);
            setColor(SettingUtil.getInstance().getInt(key, typedArray.getColor(R.styleable.ColorPreference_color_default_color,Color.parseColor("#ff009688"))));
            typedArray.recycle();
        }
    }

    @Override
    public void onClick(View v) {
        DialogUtil.showColorDialog(((BaseActivity) ActivityManager.getInstance().getCurrentActivity()).getSupportFragmentManager(),false,useAlpha,color,this);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        this.color = color;
        setColor(color);
        SettingUtil.getInstance().putInt(key,color);
    }

    private void setColor(int color){
        this.color = color;
        GradientDrawable drawable = (GradientDrawable)colorBg.getBackground();
        drawable.setColor(color);
        drawable.invalidateSelf();
        invalidate();
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
