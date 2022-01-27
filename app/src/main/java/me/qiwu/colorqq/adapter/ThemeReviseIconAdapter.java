package me.qiwu.colorqq.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.fragment.BaseThemeAdapter;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.PicassoUtil;

/**
 * Created by Deng on 2019/1/21.
 */

public class ThemeReviseIconAdapter extends BaseThemeAdapter {
    private Context context;
    private List<String> files = new ArrayList<>();
    private String mThemePath;
    public ThemeReviseIconAdapter(Context context,String themePath){
        this.context = context;
        mThemePath = themePath;
        File file = new File(themePath + "/drawable-xhdpi/");
        if (file.exists() && file.isDirectory()){
            if (file.list() != null){
                files.addAll(Arrays.asList(file.list((dir, name) -> name.endsWith("png") || name.endsWith("jpg"))));
                Collections.sort(files, (o1, o2) -> {
                    File file0 = new File(file,o1);
                    File file1 = new File(file,o2);
                    if (file0.isDirectory() && file1.isFile())
                        return -1;
                    if (file0.isFile() && file1.isDirectory())
                        return 1;
                    return o1.toLowerCase().compareTo(o2.toLowerCase());
                });
            }

        }
    }
    @Override
    public int getCount() {
        return files.size();
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
        if (convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.theme_list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.pre = view.findViewById(R.id.theme_item_color);
            viewHolder.name =  view.findViewById(R.id.theme_item_name);
            viewHolder.value =  view.findViewById(R.id.theme_item_value);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        String path = mThemePath + "/drawable-xhdpi/"+files.get(position);
        File file = new File(path);
        if (file.exists() && file.isFile()){
            PicassoUtil.loadFile(viewHolder.pre,path);
            viewHolder.name.setText(files.get(position));
        } else {
            viewHolder.pre.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            viewHolder.name.setText("文件不存在");
        }
        viewHolder.value.setVisibility(View.GONE);
        view.setTag(R.id.tag_theme_icon_name,files.get(position));
        return view;
    }

    public void updateFile(String fileName){
        if (!this.files.contains(fileName)){
            this.files.add(fileName);
            File file = new File(mThemePath + "/drawable-xhdpi/");
            Collections.sort(this.files, (o1, o2) -> {
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
        if (this.files.contains(fileName)){
            this.files.remove(fileName);
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyThemeChanged() {
        files.clear();
        File file = new File(mThemePath + "/drawable-xhdpi/");
        if (file.exists() && file.isDirectory()){
            if (file.list() != null){
                files.addAll(Arrays.asList(file.list((dir, name) -> name.endsWith("png") || name.endsWith("jpg"))));
                Collections.sort(files, (o1, o2) -> {
                    File file0 = new File(file,o1);
                    File file1 = new File(file,o2);
                    if (file0.isDirectory() && file1.isFile())
                        return -1;
                    if (file0.isFile() && file1.isDirectory())
                        return 1;
                    return o1.toLowerCase().compareTo(o2.toLowerCase());
                });
            }

        }
        notifyDataSetChanged();
    }

    class ViewHolder{
        ImageView pre;
        TextView name;
        TextView value;
    }
}
