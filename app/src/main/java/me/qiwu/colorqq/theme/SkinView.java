package me.qiwu.colorqq.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SkinView extends View implements ISkinWidget {
    private String skinName;
    private long lastModified;
    public SkinView(Context context) {
        super(context);
        loadSkin();
    }

    public SkinView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadSkin();
    }

    public SkinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
