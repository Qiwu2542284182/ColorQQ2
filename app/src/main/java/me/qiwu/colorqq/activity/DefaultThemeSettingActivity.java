package me.qiwu.colorqq.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.drawable.NinePatch;
import me.qiwu.colorqq.theme.ThemeUtil;
import me.qiwu.colorqq.util.BitmapUtil;
import me.qiwu.colorqq.util.DialogUtil;
import me.qiwu.colorqq.util.FileUtil;

import static me.qiwu.colorqq.theme.ThemeUtil.hexStringToByte;
import static me.qiwu.colorqq.util.BitmapUtil.getTintDrawable;

public class DefaultThemeSettingActivity extends BaseActivity {
    private String[] mTintColor = {
            "skin_blue.xml",
            "skin_blue_item.xml",
            "skin_blue_link.xml",
            "skin_color_title_immersive_bar.xml",
            "skin_float_btn.xml",
            "skin_search_button.xml",
            "skin_search_button_theme_version2.xml",
            "skin_chat_buble_link.xml"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_theme_setting);

    }

    @Override
    public Context getContext() {
        return this;
    }

    public void createTheme(View v){
        DialogUtil.showColorDialog(getSupportFragmentManager(), true, new ColorPickerDialogListener() {
            @Override
            public void onColorSelected(int dialogId, int color) {
                FileUtil.delAllFile(FileUtil.getThemePath()+"默认主题");
                FileUtil.unAssetTheme(getContext(),"theme.zip");
                loadTintColor(color);
                loadTintPic(color);
            }

            @Override
            public void onDialogDismissed(int dialogId) {

            }
        });
    }

    public void diyIcon(View v){
        //startActivity(new Intent(getContext(),ThemeIconSettingActivity.class));
    }

    public void diyTextColor(View v){
        //startActivity(new Intent(getContext(),ThemeTextColorSettingActivity.class));
    }

    public void diyBg(View v){
        //startActivity(new Intent(getContext(),ThemeBackgroundSettingActivity.class));
    }

    public void backupThemeZip(View v){
        ProgressDialog progressDialog = ProgressDialog.show(getContext(),"提示","正在备份",false,false);
        String savePath = FileUtil.getThemePath()+System.currentTimeMillis()+".zip";
        if (FileUtil.zip(FileUtil.getDefThemePath(),savePath)){
            DialogUtil.showTip(getContext(),"备份文件保存在："+savePath);
        } else {
            Toast.makeText(getContext(),"保存失败",Toast.LENGTH_SHORT).show();
        }
        progressDialog.cancel();
    }

    @SuppressLint("StaticFieldLeak")
    public void backupTheme(View v){

    }



    private void loadTintPic(int color){
        try {
            FileUtil.copyFile(getAssets().open("baseTheme.zip"),FileUtil.getThemePath()+"baseTheme.zip");
        } catch (IOException e) {
            e.printStackTrace();
            DialogUtil.showTip(getContext(),"导入失败\n"+e.getLocalizedMessage());
            return;
        }
        try {
            ZipFile zipFile = new ZipFile(FileUtil.getThemePath()+"baseTheme.zip");
            Enumeration<? extends ZipEntry> zes = zipFile.entries();
            while (zes.hasMoreElements()) {
                ZipEntry zipEntry = zes.nextElement();
                if (!zipEntry.isDirectory()){
                    String name = zipEntry.getName();
                    Bitmap bitmap = BitmapFactory.decodeStream(zipFile.getInputStream(zipEntry));
                    if (BitmapUtil.isNinePatch(bitmap)) {
                        NinePatch ninePatch = null;
                        if ("skin_tips_newmessage.9.png".equals(name)){
                            ninePatch = new NinePatch(bitmap,true);
                            ninePatch.loadCompiled(getTintDrawable(bitmap,color),bitmap.getNinePatchChunk());
                        } else {
                            ninePatch = new NinePatch(getTintDrawable(bitmap,color), true);
                            ninePatch.sortRegions();
                        }
                        try {
                            File file1 = new File(FileUtil.getThemePath() +  "默认主题/drawable-xhdpi/" + name);
                            if (file1.exists()) file1.delete();
                            ninePatch.saveToFile(file1, NinePatch.SAVE_COMPILED);
                        } catch (IOException e) {
                            new AlertDialog.Builder(this)
                                    .setTitle("错误")
                                    .setMessage(e.toString())
                                    .setPositiveButton("确定", null)
                                    .create().show();
                            break;
                        }
                    } else {
                        Bitmap newBitmap = getTintDrawable(bitmap, color);
                        NinePatch ninePatch = new NinePatch(newBitmap, false);
                        File file1 = new File(FileUtil.getThemePath() +  "默认主题/drawable-xhdpi/" + name);
                        if (file1.exists()) file1.delete();
                        try {
                            ninePatch.saveToFile(file1, NinePatch.SAVE_RAW);
                        } catch (IOException e) {
                            new AlertDialog.Builder(this)
                                    .setTitle("错误")
                                    .setMessage(e.toString())
                                    .setPositiveButton("确定", null)
                                    .create().show();
                            break;
                        }
                    }
                }
            }
            Toast.makeText(getContext(),"已生成",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.deleteFile(FileUtil.getThemePath()+"baseTheme.zip");
        }
    }

    private void loadTintColor(int color){
        for (String name : mTintColor){
            createColorXmlFile(color,name);
        }
    }

    public static void createColorXmlFile(int color,String name){

    }

    private static void createFileWithByte(byte[] bytes,String fileName) {
        File file = new File(FileUtil.getThemePath()+"默认主题/color/"+fileName);
        if (file.exists())file.delete();
        FileOutputStream outputStream = null;
        try {

            file.createNewFile();
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getHexColor(int color){
        int a = (color & 0xff000000) >>> 24;
        int r = (color & 0x00ff0000) >> 16;
        int g = (color & 0x0000ff00) >> 8;
        int b = (color & 0x000000ff);

        String aa = Integer.toHexString(a).length()==1? "0" + Integer.toHexString(a) : Integer.toHexString(a);
        String rr = Integer.toHexString(r).length()==1? "0" + Integer.toHexString(r) : Integer.toHexString(r);
        String gg = Integer.toHexString(g).length()==1? "0" + Integer.toHexString(g) : Integer.toHexString(g);
        String bb = Integer.toHexString(b).length()==1? "0" + Integer.toHexString(b) : Integer.toHexString(b);
        return bb + gg + rr + aa;
    }
}
