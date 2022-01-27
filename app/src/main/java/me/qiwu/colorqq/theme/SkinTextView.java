package me.qiwu.colorqq.theme;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SkinTextView extends androidx.appcompat.widget.AppCompatTextView implements ISkinWidget{
    private long lastModified;
    public SkinTextView(Context context) {
        super(context);
        loadSkin();
    }

    public SkinTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadSkin();
    }

    public SkinTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadSkin();
    }


    @Override
    public int getType() {
        return ISkinWidget.TYPE_COLOR;
    }

    @Override
    public String getSkinName() {
        Object tag = getTag();
        return tag == null ? null : (tag.toString() + ".xml");
    }

    @Override
    public void loadSkin() {
        if (BaseConstantState.THEME_PATH != null && getSkinName() != null){
            File file = new File(BaseConstantState.THEME_PATH + "/color/" + getSkinName());
            if (file.exists()){
                if (file.lastModified() == lastModified)return;
                lastModified = file.lastModified();
                try {
                    setTextColor(SkinnableColorStateList.createFromFile(getResources(),new FileInputStream(file)));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
