package me.qiwu.colorqq.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.activity.ThemeSelectActivity;

public class ThemeSelectAdapter extends BaseItemDraggableAdapter {
    private boolean isFunMode;

    public ThemeSelectAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Object item) {
        ThemeSelectActivity.LocalThemeInfo themeInfo = (ThemeSelectActivity.LocalThemeInfo) item;
        helper.setText(R.id.item_select_name,themeInfo.themeName);
        helper.setTextColor(R.id.item_select_name,themeInfo.isUse && !isFunMode ? 0xff4286F3 : Color.BLACK);
        helper.setVisible(R.id.item_select_is_use,themeInfo.isUse && !isFunMode);
        Bitmap bitmap = null;
        try {
            if (themeInfo.prePicPath != null)
                bitmap = BitmapFactory.decodeFile(themeInfo.prePicPath);
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
        if (bitmap != null){
            helper.setImageBitmap(R.id.item_select_pre,bitmap);
        } else {
            helper.setImageDrawable(R.id.item_select_pre,new ColorDrawable(0xff4286F3));
        }
        helper.setVisible(R.id.item_select_check,isFunMode && !themeInfo.isDefTheme);
        helper.setBackgroundRes(R.id.item_select_check,themeInfo.isCheck ? R.drawable.ic_checked : R.drawable.ic_unchecked);
    }


    public void setFunMode(boolean isMode){
        isFunMode = isMode;
        notifyDataSetChanged();
    }
}
