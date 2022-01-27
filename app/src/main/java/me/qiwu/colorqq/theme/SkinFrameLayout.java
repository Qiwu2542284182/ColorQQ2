package me.qiwu.colorqq.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SkinFrameLayout extends FrameLayout implements ISkinWidget {
    private String skinName;
    private long lastModified;
    public SkinFrameLayout(@NonNull Context context) {
        super(context);
        loadSkin();
    }

    public SkinFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadSkin();
    }

    public SkinFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadSkin();
    }

    @Override
    public int getType() {
        return ISkinWidget.TYPE_IMAGE;
    }

    @Override
    public String getSkinName() {
        if (skinName != null){
            return skinName;
        }
        Object tag = getTag();
        skinName = tag == null ? null : (tag.toString() + ".png");
        return skinName;
    }

    @Override
    public void loadSkin() {
        String tag = (String) getTag();
        if (!TextUtils.isEmpty(tag)){
            try {
                Bitmap bitmap = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inTargetDensity = 320;
                if (BaseConstantState.THEME_PATH != null){
                    File file = new File(BaseConstantState.THEME_PATH + "/drawable-xhdpi/" + tag + ".png");
                    if (file.exists()){
                        if (file.lastModified() == lastModified)return;
                        lastModified = file.lastModified();
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
                    }
                }
                if (bitmap == null){
                    InputStream inputStream = getResources().getAssets().open("skin/" + tag + ".png");
                    bitmap = BitmapFactory.decodeStream(inputStream,new Rect(),options);
                }
                if (bitmap == null) return;
                if (getSkinName().endsWith(".9.png")){
                    setBackground(new SkinnableNinePatchDrawable(getResources(),bitmap,bitmap.getNinePatchChunk(),getSkinName()));
                } else {
                    setBackground(new SkinnableBitmapDrawable(getResources(),bitmap));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
