package me.qiwu.colorqq.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.File;
import java.util.Date;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.activity.FileSelectActivity;

public class FileSelectAdapter extends BaseAdapter {
    private List<FileSelectActivity.FileInfo> data;
    private Context context;
    public FileSelectAdapter(@Nullable List<FileSelectActivity.FileInfo> data,Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
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
            view = LayoutInflater.from(context).inflate(R.layout.item_file,null);
            viewHolder = new ViewHolder();
            viewHolder.fileIcon = view.findViewById(R.id.fileIcon);
            viewHolder.fileTime =  view.findViewById(R.id.fileTime);
            viewHolder.fileName =  view.findViewById(R.id.fileName);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        FileSelectActivity.FileInfo fileInfo = data.get(position);
        if (!fileInfo.isFile){
            viewHolder.fileIcon.setImageResource(R.drawable.file_icon_dir);
        } else {
            if (fileInfo.fileName.endsWith(".zip") || fileInfo.fileName.endsWith(".theme") || fileInfo.fileName.endsWith(".rar")){
                viewHolder.fileIcon.setImageResource(R.drawable.file_icon_zip);
            } else if (fileInfo.fileName.endsWith(".xml")){
                viewHolder.fileIcon.setImageResource(R.drawable.file_icon_xml);
            } else {
                viewHolder.fileIcon.setImageResource(R.drawable.file_icon_other);
            }
        }
        viewHolder.fileName.setText(fileInfo.fileName);
        viewHolder.fileTime.setText(fileInfo.time);
        return view;
    }

    class ViewHolder{
        ImageView fileIcon;
        TextView fileTime;
        TextView fileName;
    }
}
