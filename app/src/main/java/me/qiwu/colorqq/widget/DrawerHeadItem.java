package me.qiwu.colorqq.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.qiwu.colorqq.util.DensityUtil;

public class DrawerHeadItem extends LinearLayout {
    private TextView textView;
    public DrawerHeadItem(Context context) {
        super(context);
        setOrientation(VERTICAL);
        View view = new View(context);
        view.setBackgroundColor(Color.parseColor("#d8d8d8"));
        textView = new TextView(context);
        textView.setTextColor(Color.BLACK);
        textView.setAlpha(0.8f);
        textView.setPadding(DensityUtil.dip2px(context,10),DensityUtil.dip2px(context,5),DensityUtil.dip2px(context,10),DensityUtil.dip2px(context,5));
        addView(view,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
        addView(textView);
    }

    public void setTitle(CharSequence charSequence){
        textView.setText(charSequence);
    }

    public void setTitleColor(int color){
        textView.setTextColor(color);
    }

    public void hideTextView(){
        textView.setTextSize(0);
    }
}
