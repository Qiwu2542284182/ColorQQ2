package me.qiwu.colorqq.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import me.qiwu.colorqq.activity.BaseApplication;
import me.qiwu.colorqq.manager.ActivityManager;

/**
 * Created by Deng on 2019/1/27.
 */

public class BitmapUtil {
    public static boolean isNinePatch(Bitmap bitmap) {
        if (bitmap==null){
            return false;
        }
        byte[] chunk = bitmap.getNinePatchChunk();
        return chunk != null && NinePatch.isNinePatchChunk(chunk);
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Bitmap getTintDrawable(Bitmap inBitmap,int color){
        // start with a Bitmap bmp
        Bitmap outBitmap = Bitmap.createBitmap (inBitmap.getWidth(), inBitmap.getHeight() , inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter( new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)) ;
        paint.setAntiAlias(true);
        canvas.drawBitmap(inBitmap , 0, 0, paint) ;
        return outBitmap ;
    }

    public static Bitmap getColorBitmap(Bitmap inBitmap,int color){
        // start with a Bitmap bmp
        Bitmap outBitmap = Bitmap.createBitmap (inBitmap.getWidth(), inBitmap.getHeight() , inBitmap.getConfig());
        outBitmap.eraseColor(color);
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        canvas.drawBitmap(inBitmap , 0, 0, paint) ;
        return outBitmap ;
    }

    public static void saveColorPictures(int color, File file){
        if (file.getName().endsWith(".9.png")){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(BaseApplication.getContext().getAssets().open("tint"));
            } catch (IOException e) {
                e.printStackTrace();
                DialogUtil.showTip(ActivityManager.getInstance().getCurrentActivity(),"生成错误\n"+e.getMessage());
                return;
            }
            me.qiwu.colorqq.drawable.NinePatch ninePatch = null;
            //这个圆形会变形，不知道为什么，所以使用原来的方法
            if ("skin_tips_newmessage.9.png".equals(file.getName())){
                ninePatch = new me.qiwu.colorqq.drawable.NinePatch(bitmap,true);
                ninePatch.loadCompiled(BitmapUtil.getTintDrawable(bitmap,color),bitmap.getNinePatchChunk());
            } else {
                ninePatch = new me.qiwu.colorqq.drawable.NinePatch(BitmapUtil.getTintDrawable(bitmap,color), true);
                ninePatch.sortRegions();
            }
            try {
                if (file.exists()) file.delete();
                ninePatch.saveToFile(file, me.qiwu.colorqq.drawable.NinePatch.SAVE_COMPILED);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bitmap oldBitmap = null;
            try {
                oldBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = Bitmap.createBitmap(oldBitmap==null?10:oldBitmap.getWidth(),oldBitmap==null ? 10 : oldBitmap.getHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(color);
            canvas.drawBitmap(bitmap, 0, 0, null);
            if (file.exists()){
                file.delete();
            }
            try {
                file.createNewFile();
                OutputStream stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
                stream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
