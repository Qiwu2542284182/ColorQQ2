package me.qiwu.colorqq.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Outline;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.fragment.BaseThemeAdapter;
import me.qiwu.colorqq.theme.SkinnableColorStateList;
import me.qiwu.colorqq.util.ColorUtil;
import me.qiwu.colorqq.util.DensityUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.QQResourcesUtil;

/**
 * Created by Deng on 2019/1/20.
 */

public class ThemeAllTextColorListAdapter extends BaseThemeAdapter {
    private List<QQResourcesUtil.QQResItem> mFileNames;
    private Context context;
    private String mThemePath;

    public ThemeAllTextColorListAdapter(Context context, String themePath){
        this.context = context;
        mThemePath = themePath;
        mFileNames = QQResourcesUtil.getInstance().getTextColorItems();
    }

    @Override
    public int getCount() {
        return mFileNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.theme_list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.colorPic = (ImageView) view.findViewById(R.id.theme_item_color);
            viewHolder.colorPic.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), DensityUtil.dip2px(context,5));
                }
            });
            viewHolder.colorPic.setClipToOutline(true);
            viewHolder.colorName = (TextView) view.findViewById(R.id.theme_item_name);
            viewHolder.colorValue = (TextView) view.findViewById(R.id.theme_item_value);
            view.setTag(R.id.tag_theme_color_view,viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(R.id.tag_theme_color_view);
        }
        File file = new File(mThemePath + "/color/"+ mFileNames.get(position));
        if (file.exists()&&file.isFile()){
            String colorName = "00000000";
            int color = 0;
            try {
                color = SkinnableColorStateList.createFromFile(context.getResources(),new FileInputStream(file)).getDefaultColor();
                colorName = ColorUtil.getHexColor(color);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            viewHolder.colorPic.setBackgroundColor(color);
            viewHolder.colorValue.setText(colorName);
            viewHolder.colorName.setText(mFileNames.get(position).resName);
            view.setTag(R.id.tag_theme_color_name,mFileNames.get(position));
            view.setTag(R.id.tag_theme_color_value,"#" + colorName);
        } else {
            viewHolder.colorPic.setBackgroundColor(0x33aaaaaa);
            viewHolder.colorValue.setText("默认");
            viewHolder.colorName.setText(mFileNames.get(position).resName);
            view.setTag(R.id.tag_theme_color_name,mFileNames.get(position));
            view.setTag(R.id.tag_theme_color_value,"默认");
        }

        return view;
    }

    @Override
    public void notifyThemeChanged() {
        notifyDataSetChanged();
    }

    class ViewHolder{
        ImageView colorPic;
        TextView colorName;
        TextView colorValue;
    }
}
