package me.qiwu.colorqq.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import me.qiwu.colorqq.BuildConfig;
import me.qiwu.colorqq.R;
import me.qiwu.colorqq.util.DialogUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.SettingUtil;

public class MainSettingActivity extends BaseActivity {
    private final static String QQ = "com.tencent.mobileqq";
    private View mCloseQQButton;
    private Handler mHandler;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 9999){
                    DialogUtil.showTip(getContext(),msg.obj.toString());
                    return;
                }
                Toast.makeText(getContext(),"重启QQ失败，检查是否拥有root权限",Toast.LENGTH_SHORT).show();
            }
        };
        setContentView(R.layout.activity_main_setting);

        File file = new File(FileUtil.getModulePath());
        if (!file.exists()){
            new AlertDialog.Builder(getContext())
                    .setTitle("提示")
                    .setMessage("资源文件不存在，是否创建")
                    .setPositiveButton("确定", (dialog, which) -> {
                        File tempFile = new File(getExternalCacheDir(),"QQColor2.zip");
                        try {
                            if (FileUtil.copyFile(getAssets().open("QQColor2.zip"),tempFile.getAbsolutePath())){
                                FileUtil.unZip2(tempFile.getPath(),FileUtil.getModulePath());
                            } else {
                                Toast.makeText(getContext(),"复制文件出错，解压失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                            DialogUtil.showTip(getContext(), Log.getStackTraceString(e));
                        }
                    })
                    .setNegativeButton("取消",null)
                    .create().show();

        } else {
            FileUtil.checkFiles(getContext());
        }
        if (SettingUtil.getInstance().getBoolean("show_update_" + BuildConfig.VERSION_CODE,true)){
            new AlertDialog.Builder(getContext())
                    .setTitle("更新日志")
                    .setMessage(getUpdateMsg())
                    .setPositiveButton("确定",null)
                    .setOnDismissListener(dialog -> SettingUtil.getInstance().putBoolean("show_update_" + BuildConfig.VERSION_CODE,false))
                    .create()
                    .show();
        }
        mCloseQQButton = findViewById(R.id.main_close_qq);
        mCloseQQButton.setVisibility(SettingUtil.getInstance().getBoolean("module_hide_close_qq") ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public Context getContext() {
        return this;
    }

    public void module(View view){
        startActivityForResult(new Intent(this, ModuleSettingActivity.class),999);
    }

    public void fab(View view){
        startActivity(new Intent(this, FabSettingActivity.class));
    }

    public void theme(View view){
        Intent intent = new Intent(this, ThemeSelectActivity.class);
        intent.putExtra("theme_path",new File(Environment.getExternalStorageDirectory(),"测试主题").getAbsolutePath());
        intent.putExtra("theme_name","测试主题");
        startActivity(intent);
    }

    public void chat(View view){
        startActivity(new Intent(this, ChatSettingActivity.class));
    }

    public void top(View view){
        startActivity(new Intent(this, TopSettingActivity.class));
    }

    public void tab(View view){
        startActivity(new Intent(this, TabSettingActivity.class));
    }


    public void closeQQ(View view){
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("重启QQ")
                .setPositiveButton("确定", (dialog, which) -> new Thread(() -> {
                    String result = executeCmd("am force-stop " + QQ);
                    if (result.isEmpty()) {
                        startActivity(getPackageManager().getLaunchIntentForPackage(QQ));
                    } else {
                        Message msg = new Message();
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                }).start())
                .setNegativeButton("取消",null)
                .create().show();

    }

    private String executeCmd(String cmd) {
        DataOutputStream os = null;
        Process process = null;
        try {
            process = Runtime.getRuntime()
                    .exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            return output.toString()
                    .trim();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    private String getUpdateMsg(){
        return  "● 适配QQ8.8.55\n";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 999 && resultCode == RESULT_OK && data != null){
            mCloseQQButton.setVisibility(data.getBooleanExtra("isHide",false) ? View.INVISIBLE : View.VISIBLE);
        }
    }

    public static native byte[] getPublicKey();

    public static void log(String s){
        Log.d("qiwu",s);
    }

    public static void log(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (byte b : bytes) {
            sb.append(b);
            sb.append(',').append(' ');
        }
        sb.append(']');
        Log.d("qiwu",sb.toString());
    }

}
