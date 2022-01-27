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

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.fragment.BaseThemeAdapter;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.PicassoUtil;

/**
 * Created by Deng on 2019/1/21.
 */

public class ThemeCommonIconAdapter extends BaseThemeAdapter {
    private Context context;
    private String mThemePath;
    private String[] names = {
            "skin_header_bar_bg.9.png",
            "skin_aio_friend_bubble_nor.9.png",
            "skin_aio_friend_bubble_pressed.9.png",
            "skin_aio_user_bubble_nor.9.png",
            "skin_aio_user_bubble_pressed.9.png",
            "skin_aio_send_button_normal.9.png",
            "skin_aio_send_button_pressed.9.png",
            "skin_aio_send_button_disabled.9.png",
            "skin_aio_input_bar_bg_theme_version2.9.png",
            "skin_aio_input_bg.9.png",
            "skin_aio_panel_icon_bg.9.png",
            "skin_bottom_bar_background_theme_version2.png",
            "skin_tab_icon_contact_selected.png",
            "skin_tab_icon_contact_normal.png",
            "skin_tab_icon_conversation_selected.png",
            "skin_tab_icon_conversation_normal.png",
            "skin_tab_icon_now_selected.png",
            "skin_tab_icon_now_normal.png",
            "skin_tab_icon_plugin_selected.png",
            "skin_tab_icon_plugin_normal.png",
            "skin_tab_icon_see_selected.png",
            "skin_tab_icon_see_normal.png",
            "skin_bottom_bar_background_theme_version2.png"
    };

    private String[] introductions = {
            "顶栏图片",
            "好友气泡正常状态",
            "好友气泡按压状态",
            "自己气泡正常状态",
            "自己气泡按压状态",
            "发送按钮正常状态",
            "发送按钮按压状态",
            "发送按钮不可用状态",
            "聊天界面输入框发送键整体背景",
            "聊天界面输入框背景",
            "聊天工具栏整体背景",
            "主界面底栏背景",
            "底栏联系人选择状态图标",
            "底栏联系人正常状态图标",
            "底栏消息选择状态图标",
            "底栏消息正常状态图标",
            "底栏日迹选择状态图标",
            "底栏日迹正常状态图标",
            "底栏动态选择状态图标",
            "底栏动态正常状态图标",
            "底栏看点选择状态图标",
            "底栏看点正常状态图标",
            "底栏背景"
    };

    public ThemeCommonIconAdapter(Context context,String themePath){
        this.context = context;
        mThemePath = themePath;
    }
    @Override
    public int getCount() {
        return names.length;
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
        String path = mThemePath + "/drawable-xhdpi/"+names[position];
        File file = new File(path);
        PicassoUtil.loadFile(viewHolder.pre,path);
        viewHolder.name.setText(names[position]);
        String normal = file.exists() ? "" : "(默认)";
        viewHolder.value.setText(introductions[position] + normal);
        view.setTag(R.id.tag_theme_icon_name,names[position]);
        return view;
    }

    @Override
    public void notifyThemeChanged() {
        notifyDataSetChanged();
    }

    class ViewHolder{
        ImageView pre;
        TextView name;
        TextView value;
    }
}
