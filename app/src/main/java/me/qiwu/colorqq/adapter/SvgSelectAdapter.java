package me.qiwu.colorqq.adapter;

import android.content.Context;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.qiwu.colorqq.R;

public class SvgSelectAdapter extends BaseAdapter {
    private List<Picture>pictures = new ArrayList<>();
    private List<String>titles = new ArrayList<>();
    private List<Picture> originPic = new ArrayList<>();
    private List<String> originTitle = new ArrayList<>();
    private Context context;
    public SvgSelectAdapter(Context context, List<Picture>pictures, List<String>titles){
        this.pictures.addAll(pictures);
        this.titles.addAll(titles);
        originPic.addAll(pictures);
        originTitle.addAll(titles);
        this.context = context;
    }
    @Override
    public int getCount() {
        return pictures.size();
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
            view = LayoutInflater.from(context).inflate(R.layout.item_svg_gridview,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.svg = view.findViewById(R.id.svg_pic);
            viewHolder.title = view.findViewById(R.id.svg_title);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.title.setText(titles.get(position));
        viewHolder.svg.setImageDrawable(new PictureDrawable(pictures.get(position)));
        return view;
    }

    public void search(String msg){
        if (TextUtils.isEmpty(msg)){
            titles.clear();
            pictures.clear();
            titles.addAll(originTitle);
            pictures.addAll(originPic);
        } else {
            List<String> title = new ArrayList<>();
            List<Picture> pic = new ArrayList<>();
            for (int i=0;i<originTitle.size();i++){
                String name = originTitle.get(i);
                if (name.contains(msg)){
                    Picture picture = originPic.get(i);
                    title.add(name);
                    pic.add(picture);
                }
            }
            titles.clear();
            pictures.clear();
            titles.addAll(title);
            pictures.addAll(pic);
        }
        notifyDataSetChanged();
    }

    public Picture getPic(int position){
        return pictures.get(position);
    }

    public String getPicName(int position){
        return titles.get(position);
    }

    class ViewHolder{
        ImageView svg;
        TextView title;
    }
}
