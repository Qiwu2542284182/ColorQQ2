package me.qiwu.colorqq.util;

import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

public class PicassoUtil {
    private static HashMap<String,String> sFileTimes = new HashMap<>();

    public static void loadFile(ImageView imageView,String filePath){
        File file = new File(filePath);
        if (!file.exists()){
            imageView.setImageDrawable(null);
            return;
        }
        if (file.isDirectory())return;
        String time = String.valueOf(file.lastModified());
        String tempTime = sFileTimes.getOrDefault(filePath,"");
        if (TextUtils.isEmpty(tempTime)){
            sFileTimes.put(filePath,time);
        } else if (!time.equals(tempTime)){
            Picasso.get().invalidate(file);
            sFileTimes.put(filePath,time);
        }
        Picasso.get().load(file).fit().centerCrop().into(imageView);
    }
}
