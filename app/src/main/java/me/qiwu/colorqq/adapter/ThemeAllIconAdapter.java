package me.qiwu.colorqq.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import me.qiwu.colorqq.BuildConfig;
import me.qiwu.colorqq.R;
import me.qiwu.colorqq.XHook.XposedCompact;
import me.qiwu.colorqq.fragment.BaseThemeAdapter;
import me.qiwu.colorqq.manager.ActivityManager;
import me.qiwu.colorqq.theme.SkinnableColorStateList;
import me.qiwu.colorqq.util.ColorUtil;
import me.qiwu.colorqq.util.DensityUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.PicassoUtil;
import me.qiwu.colorqq.util.QQResourcesUtil;

public class ThemeAllIconAdapter extends BaseThemeAdapter {
    private List<QQResourcesUtil.QQResItem> mFileNames;
    private Resources mQQResources;
    private Context context;
    private String mThemePath;

    public ThemeAllIconAdapter(Context context,String themePath){
        this.context = context;
        mThemePath = themePath;
        QQResourcesUtil.getInstance().checkUpdate();
        mQQResources = QQResourcesUtil.getInstance().getQQResources();
        mFileNames = QQResourcesUtil.getInstance().getIconItems();
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
            viewHolder.iconPic = view.findViewById(R.id.theme_item_color);
            viewHolder.iconName = view.findViewById(R.id.theme_item_name);
            viewHolder.iconValue = view.findViewById(R.id.theme_item_value);
            view.setTag(R.id.tag_theme_icon_view,viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(R.id.tag_theme_icon_view);
        }
        String path = mThemePath + "/drawable-xhdpi/"+ mFileNames.get(position).resName;
        File file = new File(path);
        if (file.exists() && file.isFile()){
            PicassoUtil.loadFile(viewHolder.iconPic,path);
            viewHolder.iconValue.setText("已修改");
        } else {
            viewHolder.iconPic.setImageDrawable(mQQResources.getDrawable(mFileNames.get(position).id));
            viewHolder.iconValue.setText("默认");
        }
        viewHolder.iconName.setText(mFileNames.get(position).resName);
        view.setTag(R.id.tag_theme_icon_name,mFileNames.get(position).resName);

        return view;
    }

    @Override
    public void notifyThemeChanged() {
        notifyDataSetChanged();
    }

    class ViewHolder{
        ImageView iconPic;
        TextView iconName;
        TextView iconValue;
    }
}
