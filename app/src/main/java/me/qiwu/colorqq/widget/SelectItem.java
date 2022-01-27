package me.qiwu.colorqq.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.util.SettingUtil;

public class SelectItem extends RelativeLayout implements View.OnClickListener {
    private TextView mLeftText;
    private TextView mRightText;
    private String key;
    private String[] options;
    private int position;
    private OnSelectChangeListener onSelectChangeListener;
    public SelectItem(Context context) {
        this(context,null);
    }

    public SelectItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_text_item,this);
        setOnClickListener(this);
        mLeftText = view.findViewById(R.id.text_item_left);
        mRightText = view.findViewById(R.id.text_item_right);
        setBackgroundColor(context.getColor(R.color.white));
        if (attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.SelectItem);
            mLeftText.setText(typedArray.getString(R.styleable.SelectItem_select_text));
            options = typedArray.getString(R.styleable.SelectItem_select_options).split(",");
            key = typedArray.getString(R.styleable.SelectItem_select_key);
            position = SettingUtil.getInstance().getInt(key);
            mRightText.setText(options[position]);
            typedArray.recycle();
        }
    }

    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(getContext())
                .setTitle(mLeftText.getText())
                .setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        position = which;
                        SettingUtil.getInstance().putInt(key,which);
                        if (onSelectChangeListener!=null){
                            onSelectChangeListener.onChange(which);
                        }
                        setRightText(position);
                    }
                })
                .setPositiveButton(R.string.cancel,null)
                .create()
                .show();
    }

    public void setRightText(int position){
        mRightText.setText(options[position]);
    }

    public void setOnSelectChangeListener(OnSelectChangeListener onSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener;
    }

    public interface OnSelectChangeListener{
        void onChange(int position);
    }
}
