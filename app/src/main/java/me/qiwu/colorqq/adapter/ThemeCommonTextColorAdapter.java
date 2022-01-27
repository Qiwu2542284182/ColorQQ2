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

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.fragment.BaseThemeAdapter;
import me.qiwu.colorqq.theme.SkinnableColorStateList;
import me.qiwu.colorqq.util.ColorUtil;
import me.qiwu.colorqq.util.DensityUtil;
import me.qiwu.colorqq.util.FileUtil;

/**
 * Created by Deng on 2019/1/20.
 */

public class ThemeCommonTextColorAdapter extends BaseThemeAdapter {
    private String mThemePath;
    private String[] names = {
            "skin_color_title_immersive_bar.xml",
            "skin_black_group_item_theme_version2.xml",
            "skin_aio_send_button.xml",
            "skin_bar_text.xml",
            "skin_bar_btn.xml",
            "skin_chat_buble.xml",
            "skin_chat_buble_link.xml",
            "skin_chat_buble_mine.xml",
            "skin_chat_buble_link_mine.xml",
            "skin_black_theme_version2.xml",
            "skin_gray2_theme_version2.xml",
            "qq_setting_me_nightmode_color_white.xml"
    };
    private String[] introductions = {
            "主题色",
            "联系人界面好友分组字体颜色",
            "发送按钮颜色",
            "顶栏中间标题颜色",
            "顶栏两侧标题颜色",
            "对方聊天字体颜色",
            "对方聊天下划线颜色",
            "自己聊天字体颜色",
            "自己聊天下划线颜色",
            "消息列表好友名称颜色",
            "消息列表好友消息颜色",
            "侧滑项目文字颜色"
    };
    private Context context;
    public ThemeCommonTextColorAdapter(Context context,String themePath){
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
            viewHolder.colorIntroduce = (TextView) view.findViewById(R.id.theme_item_introduce);
            viewHolder.colorIntroduce.setVisibility(View.VISIBLE);
            viewHolder.colorValue = (TextView) view.findViewById(R.id.theme_item_value);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        File file = new File(mThemePath + "/color/"+names[position]);
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
            viewHolder.colorIntroduce.setText(introductions[position]);
            viewHolder.colorValue.setText(colorName);
            viewHolder.colorName.setText(names[position]);
            view.setTag(R.id.tag_theme_color_name,names[position]);
            view.setTag(R.id.tag_theme_color_value,"#"+colorName);
        } else {
            viewHolder.colorPic.setBackgroundColor(0x33aaaaaa);
            viewHolder.colorIntroduce.setText(introductions[position]);
            viewHolder.colorValue.setText("默认");
            viewHolder.colorName.setText(names[position]);
            view.setTag(R.id.tag_theme_color_name,names[position]);
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
        TextView colorIntroduce;
        TextView colorValue;
    }
}
