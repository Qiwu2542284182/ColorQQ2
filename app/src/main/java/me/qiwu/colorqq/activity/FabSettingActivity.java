package me.qiwu.colorqq.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.SettingUtil;
import me.qiwu.colorqq.widget.ColorPreference;
import me.qiwu.colorqq.widget.SelectItem;

public class FabSettingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab_setting);
        SelectItem selectItem = findViewById(R.id.fab_color_mode);
        final ColorPreference colorPreference = findViewById(R.id.fab_color);
        colorPreference.setVisibility(SettingUtil.getInstance().getInt("fab_mode")==0? View.GONE:View.VISIBLE);
        selectItem.setOnSelectChangeListener(new SelectItem.OnSelectChangeListener() {
            @Override
            public void onChange(int position) {
                colorPreference.setVisibility(position==0? View.GONE:View.VISIBLE);
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    public void jumpToMenuSetting(View view){
        startActivity(new Intent(getContext(),FabMenuSettingActivity.class));
    }

    public void jumpToButtonSetting(View view){
        startActivity(new Intent(getContext(),FabButtonSettingActivity.class));
        iii();
    }

    private void iii(){}

    public void resetFabData(View view){
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("确定重置")
                .setPositiveButton("确定", (dialog, which) -> {
                    File file = new File(FileUtil.getFabPath()+"config.json");
                    if (file.exists()){
                        file.delete();
                    }
                    Toast.makeText(getContext(),"已重置",Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消",null)
                .create()
                .show();

    }

    public void resetFabIcons(View view){

        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("确定重置")
                .setPositiveButton("确定", (dialog, which) -> {
                    ZipInputStream zipInputStream = null;
                    try {
                        InputStream inputStream = getContext().getAssets().open("QQColor2.zip");
                        zipInputStream = new ZipInputStream(inputStream, Charset.forName("GBK"));
                        ZipEntry zipEntry ;
                        while ((zipEntry = zipInputStream.getNextEntry()) != null){
                            if (zipEntry.getName().contains("fab")){
                                if (zipEntry.isDirectory()){
                                    File file = new File(FileUtil.getModulePath()+zipEntry.getName());
                                    file.mkdirs();
                                }else {
                                    File file = new File(FileUtil.getModulePath()+zipEntry.getName());
                                    file.getParentFile().mkdirs();
                                    if (file.exists())file.delete();
                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                                    int len;
                                    byte[] buffer = new byte[1024];
                                    while ((len = zipInputStream.read(buffer)) != -1) {
                                        fileOutputStream.write(buffer, 0, len);
                                        fileOutputStream.flush();
                                    }
                                    fileOutputStream.close();
                                }
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if (zipInputStream!=null){
                            try {
                                zipInputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    Toast.makeText(getContext(),"已重置",Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消",null)
                .create()
                .show();


    }
}
