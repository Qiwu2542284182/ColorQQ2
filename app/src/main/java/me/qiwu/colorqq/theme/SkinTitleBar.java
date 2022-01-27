package me.qiwu.colorqq.theme;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;

public class SkinTitleBar extends View implements ISkinWidget {
    private long lastModified;
    public SkinTitleBar(Context context) {
        super(context);
        loadSkin();
    }

    public SkinTitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadSkin();
    }

    public SkinTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                try {
                    if (file.lastModified() == lastModified)return;
                    lastModified = file.lastModified();
                    setBackgroundColor(SkinnableColorStateList.createFromFile(getResources(),new FileInputStream(file)).getDefaultColor());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
