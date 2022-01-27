package me.qiwu.colorqq.library.NineGridView;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import me.qiwu.colorqq.R;

public class NineGirdPicassoLoader implements INineGridImageLoader {
    @Override
    public void displayNineGridImage(Context context, String url, ImageView imageView) {
        Picasso.get().load(new File(url)).resize(100,100).centerCrop().noFade().error(R.drawable.format_picture).into(imageView);
    }

    @Override
    public void displayNineGridImage(Context context, String url, ImageView imageView, int width, int height) {
        Picasso.get().load(new File(url)).resize(width,height).centerCrop().noFade().error(R.drawable.format_picture).into(imageView);
    }
}
