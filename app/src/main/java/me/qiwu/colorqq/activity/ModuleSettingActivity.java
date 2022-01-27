package me.qiwu.colorqq.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


import java.io.File;
import java.util.Enumeration;
import me.qiwu.colorqq.BuildConfig;
import me.qiwu.colorqq.R;
import me.qiwu.colorqq.manager.ActivityManager;
import me.qiwu.colorqq.util.DialogUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.SettingUtil;
import me.qiwu.colorqq.widget.SwitchPreference;


public class ModuleSettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_setting);
        SwitchPreference switchPreference = findViewById(R.id.module_hide_app);
        switchPreference.setChecked(isHideIcon(getContext()));
        switchPreference.setOnCheckedChangeListener(this);

        SwitchPreference switchPreference2 = findViewById(R.id.module_hide_close_button);
        switchPreference2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingUtil.getInstance().putBoolean("module_hide_close_qq",isChecked);
            Intent intent = new Intent();
            intent.putExtra("isHide",isChecked);
            setResult(RESULT_OK,intent);
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    /*public void donate(View view) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        String payUrl = "https://qr.alipay.com/fkx00337aktbgg6hgq64ae2?t=1542355035868";
        intent.setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + payUrl));
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
            return;
        }
        intent.setData(Uri.parse(payUrl.toLowerCase()));
        startActivity(intent);
    }*/

    public void jumpToAbout(View view){
        startActivity(new Intent(getContext(),AboutActivity.class));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        hideIcon(isChecked,buttonView.getContext());
        SettingUtil.getInstance().putBoolean("module_hideApp",isChecked);
    }

    public static void hideIcon(boolean b, Context context){
        ComponentName componentName = new ComponentName(context,"me.qiwu.colorqq.activity.SplashActivityAlias");
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(componentName,b ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,PackageManager.DONT_KILL_APP);

    }

    public static boolean isHideIcon(Context context){
        ComponentName componentName = new ComponentName(context,"me.qiwu.colorqq.activity.SplashActivityAlias");
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }

    public void resetRes(View view){
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("即将重置所有资源，请注意备份")
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
                    Toast.makeText(getContext(),"已重置",Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }


    public void leadSetting(View view){
        FileSelectActivity.start(this,FileSelectActivity.XML);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FileSelectActivity.SELECT_FILE_CODE && data != null) {
                String filePath = data.getStringExtra("path");
                if (!TextUtils.isEmpty(filePath)) {
                    if (FileUtil.copyFile(filePath,new File(Environment.getDataDirectory(), "data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + BuildConfig.APPLICATION_ID + ".xml").getAbsolutePath())){
                        FileUtil.copyFile(filePath,new File(FileUtil.getModulePath()+"setting.xml").getAbsolutePath());
                        new AlertDialog.Builder(getContext())
                                .setTitle("提示")
                                .setMessage("重启模块使设置生效")
                                .setCancelable(false)
                                .setPositiveButton("确定", (dialog, which) -> {
                                    ActivityManager.getInstance().finishAllActivity();
                                    System.exit(0);
                                }).create().show();
                    }
                }
            }
        }
    }
}
