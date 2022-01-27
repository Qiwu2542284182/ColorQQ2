package me.qiwu.colorqq.theme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SkinSplash extends androidx.appcompat.widget.AppCompatImageView implements ISkinWidget{
    private long lastModified;
    public SkinSplash(Context context) {
        super(context);
        loadSkin();
    }

    public SkinSplash(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadSkin();
    }

    public SkinSplash(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadSkin();
    }

    @Override
    public int getType() {
        return ISkinWidget.TYPE_IMAGE;
    }

    @Override
    public String getSkinName() {
        return "splash.jpg";
    }

    @Override
    public void loadSkin() {
        Bitmap bitmap = null;
        if (BaseConstantState.THEME_PATH != null){
            File file = new File(BaseConstantState.THEME_PATH + "/assets/" + getSkinName());
            if (file.exists()){
                if (file.lastModified() == lastModified)return;
                lastModified = file.lastModified();
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }
        if (bitmap == null){
            InputStream inputStream = null;
            try {
                inputStream = getResources().getAssets().open("skin/" + getSkinName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(inputStream);
        }
        if (bitmap == null) return;
        setImageBitmap(bitmap);
    }
}
