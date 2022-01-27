package me.qiwu.colorqq.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.qiwu.colorqq.R;

public class DrawerMenuItem extends LinearLayout {
    private TextView textView;
    private ImageView imageView;
    public DrawerMenuItem(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.item_drawer,this);
        imageView = view.findViewById(R.id.drawer_item_img);

        textView = view.findViewById(R.id.drawer_item_text);
    }

    public void setTitle(CharSequence charSequence){
        textView.setText(charSequence);
    }

    public void setImg(Drawable drawable){
        imageView.setImageDrawable(drawable);
    }

    public void setColorFilter(int color){
        imageView.setColorFilter(color);
    }

    public void setTitleColor(int color){
        textView.setTextColor(color);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setAlpha(0.5f);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setAlpha(1.0f);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
