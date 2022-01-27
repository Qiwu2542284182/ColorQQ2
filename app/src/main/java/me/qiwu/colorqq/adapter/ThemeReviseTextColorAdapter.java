package me.qiwu.colorqq.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.fragment.BaseThemeAdapter;
import me.qiwu.colorqq.theme.SkinnableColorStateList;
import me.qiwu.colorqq.util.ColorUtil;
import me.qiwu.colorqq.util.DensityUtil;
import me.qiwu.colorqq.util.FileUtil;

/**
 * Created by Deng on 2019/1/20.
 */

public class ThemeReviseTextColorAdapter extends BaseThemeAdapter {
    private Context context;
    private List<String> fileName ;
    private String mThemePath;
    public ThemeReviseTextColorAdapter(Context context,String themePath){
        this.context = context;
        mThemePath = themePath;
        File file = new File(mThemePath + "/color/");
        if (file.exists()&&file.isDirectory()){
            fileName = new ArrayList<>(Arrays.asList(file.list()));
        } else {
            fileName = new ArrayList<>();
        }
        Collections.sort(fileName, (o1, o2) -> {
            File file0 = new File(file,o1);
            File file1 = new File(file,o2);
            if (file0.isDirectory() && file1.isFile())
                return -1;
            if (file0.isFile() && file1.isDirectory())
                return 1;
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        });
    }
    @Override
    public int getCount() {
        return fileName.size();
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
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        File file = new File(mThemePath + "/color/"+fileName.get(position));
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
            viewHolder.colorName.setText(fileName.get(position));
            view.setTag(R.id.tag_theme_color_name,fileName.get(position));
            view.setTag(R.id.tag_theme_color_value,"#" + colorName);
        } else {
            viewHolder.colorPic.setBackgroundColor(0x33aaaaaa);
            viewHolder.colorValue.setText("默认");
            viewHolder.colorName.setText(fileName.get(position));
            view.setTag(R.id.tag_theme_color_name,fileName.get(position));
            view.setTag(R.id.tag_theme_color_value,"默认");
        }
        return view;
    }

    public void updateFile(String fileName){
        if (!this.fileName.contains(fileName)){
            this.fileName.add(fileName);
            File file = new File(mThemePath + "/color/");
            Collections.sort(this.fileName, (o1, o2) -> {
                File file0 = new File(file,o1);
                File file1 = new File(file,o2);
                if (file0.isDirectory() && file1.isFile())
                    return -1;
                if (file0.isFile() && file1.isDirectory())
                    return 1;
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            });
        }
        notifyDataSetChanged();
    }

    public void delFile(String fileName){
        if (this.fileName.contains(fileName)){
            this.fileName.remove(fileName);
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyThemeChanged() {
        fileName.clear();
        File file = new File(mThemePath + "/color/");
        if (file.exists()&&file.isDirectory()){
            fileName = new ArrayList<>(Arrays.asList(file.list()));
        } else {
            fileName = new ArrayList<>();
        }
        Collections.sort(fileName, (o1, o2) -> {
            File file0 = new File(file,o1);
            File file1 = new File(file,o2);
            if (file0.isDirectory() && file1.isFile())
                return -1;
            if (file0.isFile() && file1.isDirectory())
                return 1;
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        });
        notifyDataSetChanged();
    }

    public class ViewHolder{
        ImageView colorPic;
        TextView colorName;
        TextView colorValue;
    }
}
