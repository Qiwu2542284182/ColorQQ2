package me.qiwu.colorqq.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import me.qiwu.colorqq.R;

public class HeaderPreference extends LinearLayout {
    private MaterialCardView mCard;
    private TextView mTitle;

    public HeaderPreference(Context context) {
        this(context,null);
    }

    public HeaderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.widget_header_preference,this);
        mCard = view.findViewById(R.id.header_title_card);
        mTitle = view.findViewById(R.id.header_title);
        setBackgroundColor(context.getColor(R.color.white));
        if (attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.HeaderPreference);
            if (typedArray!=null){
                mCard.setCardBackgroundColor(typedArray.getColor(R.styleable.HeaderPreference_header_color,context.getColor(R.color.colorAccent)));
                mTitle.setText(typedArray.getString(R.styleable.HeaderPreference_header));
            }
        }
    }
}
