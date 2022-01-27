package me.qiwu.colorqq.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bin.zip.ZipOutputStream;
import me.qiwu.colorqq.R;
import me.qiwu.colorqq.fragment.BaseFragment;
import me.qiwu.colorqq.fragment.ThemeMergeBgFragment;
import me.qiwu.colorqq.fragment.ThemeMergeIconFragment;
import me.qiwu.colorqq.fragment.ThemeMergeTextColorFragment;
import me.qiwu.colorqq.util.DialogUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.widget.TitleBar;

public class ThemeIconAndColorSettingActivity extends BaseActivity {

    private List<TabItem> mTableItemList = new ArrayList<>();
    private String mThemePath;
    private String mThemeName;
    private String[] mBackupBg = new String[]{
            "/drawable-xhdpi/skin_background_theme_version2.png",
            "/drawable-xhdpi/skin_setting_background.png",
            "/assets/splash.jpg",
            "/drawable-xhdpi/qq_setting_me_bg.png",
            "/drawable-xhdpi/skin_chat_background.png"
    };

    public static boolean isReviseBgChange = false;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_color_and_icon_setting);
        Intent intent = getIntent();
        if (intent != null){
            mThemePath = intent.getStringExtra("theme_path");
            mThemeName = intent.getStringExtra("theme_name");
        }
        TitleBar titleBar = findViewById(R.id.theme_icon_text_titleBar);
        titleBar.setTitle(mThemeName);
        titleBar.setRightImage(getDrawable(R.drawable.ic_color_len), v -> {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_theme_reset,null);
            RadioGroup radioGroup = view.findViewById(R.id.theme_reset_radioGroup);
            View preView = view.findViewById(R.id.theme_reset_pre);
            TextView textView = view.findViewById(R.id.theme_reset_color);

            final String[] colorValue = {"009688"};
            textView.setText(colorValue[0]);
            GradientDrawable drawable = (GradientDrawable) preView.getBackground();
            drawable.setColor(Color.parseColor("#" + colorValue[0]));
            drawable.invalidateSelf();

            ((View)preView.getParent()).setOnClickListener(v1 -> DialogUtil.showColorDialog(getSupportFragmentManager(),true, new ColorPickerDialogListener() {
                @Override
                public void onColorSelected(int dialogId, int color) {
                    colorValue[0] = getHexColor(color);
                    GradientDrawable drawable1 = (GradientDrawable) preView.getBackground();
                    drawable1.setColor(Color.parseColor("#" + colorValue[0]));
                    drawable1.invalidateSelf();
                }

                @Override
                public void onDialogDismissed(int dialogId) {

                }
            }));

            new AlertDialog.Builder(getContext())
                    .setTitle("重置主题")
                    .setView(view)
                    .setPositiveButton("确定", (dialog, which) -> {
                        int id = radioGroup.getCheckedRadioButtonId();
                        if (id == R.id.theme_reset_radioButton1){
                            FileUtil.delAllFile(mThemePath);
                        } else if (id == R.id.theme_reset_radioButton2){
                            for (String name : mBackupBg){
                                backupFile(mThemePath + name);
                            }
                            FileUtil.delAllFile(mThemePath);
                        }

                        if (id != R.id.theme_reset_radioButton3){
                            File tempFile = new File(getExternalCacheDir(),"theme.zip");
                            try {
                                FileUtil.copyFile(getAssets().open("theme.zip"),tempFile.getAbsolutePath());
                                FileUtil.unZip2(tempFile.getAbsolutePath(),mThemePath);
                            } catch (Throwable e) {
                                e.printStackTrace();
                                DialogUtil.showTip(getContext(), Log.getStackTraceString(e));
                                return;
                            }
                        } else {
                            new File(mThemePath + "/assets").mkdirs();
                            new File(mThemePath + "/color").mkdirs();
                            new File(mThemePath + "/drawable-xhdpi").mkdirs();
                        }

                        ThemeSelectActivity.loadTintColor(getContext(),Color.parseColor("#" + colorValue[0]),mThemePath);
                        ThemeSelectActivity.loadTintDrawable(getContext(),Color.parseColor("#" + colorValue[0]),mThemePath);

                        File themeNameFile = new File(mThemePath + "/theme");
                        if ((!themeNameFile.exists()) && !mThemeName.endsWith("/默认主题")){
                            FileUtil.writeToFile(themeNameFile,mThemeName);
                        }

                        if (id == R.id.theme_reset_radioButton2){
                            for (String name : mBackupBg){
                                recFile(mThemePath + name);
                            }
                        }

                        List<Fragment> fragments = getSupportFragmentManager().getFragments();
                        for (int i = 0;i < fragments.size();i++){
                            ((BaseFragment)fragments.get(i)).notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("取消",null)
                    .create().show();
        });
        titleBar.setRightImage2(getDrawable(R.drawable.ic_archive), v -> {
            DialogUtil.showEditViewDialog(getContext(), "保存主题", FileUtil.getThemePath() + "保存的主题/" + mThemeName + "_" + System.currentTimeMillis() +  ".zip", false, new DialogUtil.OnEdittextClick() {
                @Override
                public void onClick(EditText editText) {
                    String path = editText.getText().toString();
                    File file = new File(path);
                    file.getParentFile().mkdirs();
                    if (file.exists()){
                        Toast.makeText(getContext(),"存在同名文件",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mProgressDialog = ProgressDialog.show(getContext(),"提示","正在压缩");
                    new Thread(() -> {
                        try {
                            ZipOutputStream outputStream = new ZipOutputStream(file);
                            outputStream.setLevel(1);
                            outputStream.putNextEntry("theme");
                            outputStream.write(mThemeName.getBytes());
                            outputStream.closeEntry();

                            addZipEntry(outputStream,"assets");
                            addZipEntry(outputStream,"color");
                            addZipEntry(outputStream,"drawable-xhdpi");
                            outputStream.close();
                            runOnUiThread(() -> {
                                if (mProgressDialog != null){
                                    mProgressDialog.dismiss();
                                    mProgressDialog = null;
                                }
                                Toast.makeText(getContext(),"保存完成",Toast.LENGTH_SHORT).show();
                            });
                        } catch (Throwable throwable){
                            throwable.printStackTrace();
                            runOnUiThread(() -> {
                                if (mProgressDialog != null){
                                    mProgressDialog.dismiss();
                                    mProgressDialog = null;
                                }
                                DialogUtil.showTip(getContext(),Log.getStackTraceString(throwable));
                            });
                        }
                    }).start();

                }
            });

        });
        initTabData();
        initTabHost();
    }

    private void addZipEntry(ZipOutputStream outputStream,String dicName) throws IOException {
        File dicFile = new File(mThemePath + "/" + dicName);
        if (dicFile.exists() && dicFile.isDirectory()){
            String[] names = dicFile.list();
            if (names != null){
                for (String name : names){
                    File assetInFile = new File(dicFile,name);
                    if (assetInFile.isFile()){
                        outputStream.putNextEntry(dicName + "/" + name);
                        outputStream.write(FileUtil.toByteArray(new FileInputStream(assetInFile)));
                        outputStream.closeEntry();
                    }

                }
            }
        }
    }

    private static String getHexColor(int color){
        int r = (color & 0x00ff0000) >> 16;
        int g = (color & 0x0000ff00) >> 8;
        int b = (color & 0x000000ff);

        String rr = Integer.toHexString(r).length()==1? "0" + Integer.toHexString(r) : Integer.toHexString(r);
        String gg = Integer.toHexString(g).length()==1? "0" + Integer.toHexString(g) : Integer.toHexString(g);
        String bb = Integer.toHexString(b).length()==1? "0" + Integer.toHexString(b) : Integer.toHexString(b);
        return rr + gg + bb;
    }

    private void backupFile(String filePath){
        File file = new File(filePath);
        if (file.exists() && file.isFile()){
            File tempFile = new File(getExternalCacheDir(),filePath.substring(filePath.lastIndexOf("/") + 1));
            FileUtil.copyFile(filePath,tempFile.getAbsolutePath());
        }
    }

    private void recFile(String filePath){
        File tempFile = new File(getExternalCacheDir(),filePath.substring(filePath.lastIndexOf("/") + 1));
        if (tempFile.exists()){
            FileUtil.copyFile(tempFile.getAbsolutePath(),filePath);
        }
    }

    //初始化Tab数据
    private void initTabData() {
        mTableItemList.add(new TabItem(R.drawable.ic_theme_setting_bottom_icon,"图标", ThemeMergeIconFragment.class));
        mTableItemList.add(new TabItem(R.drawable.ic_theme_setting_bottom_text_color,"字体颜色", ThemeMergeTextColorFragment.class));
        mTableItemList.add(new TabItem(R.drawable.ic_theme_setting_bottom_pic,"常用背景", ThemeMergeBgFragment.class));

    }

    //初始化主页选项卡视图
    private void initTabHost() {
        //实例化FragmentTabHost对象
        FragmentTabHost fragmentTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);

        //去掉分割线
        fragmentTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i<mTableItemList.size(); i++) {
            TabItem tabItem = mTableItemList.get(i);
            //实例化一个TabSpec,设置tab的名称和视图
            TabHost.TabSpec tabSpec = fragmentTabHost.newTabSpec(tabItem.getTitleString()).setIndicator(tabItem.getView());
            fragmentTabHost.addTab(tabSpec,tabItem.getFragmentClass(),getIntent().getExtras());

            //给Tab按钮设置背景
            fragmentTabHost.getTabWidget().getChildAt(i).setBackground(getDrawable(R.drawable.bg_water2));

            //默认选中第一个tab
            if(i == 0) {
                tabItem.setChecked(true);
            }
        }


        fragmentTabHost.setOnTabChangedListener(tabId -> {
            //重置Tab样式
            for (int i = 0; i< mTableItemList.size(); i++) {
                TabItem tabitem = mTableItemList.get(i);
                if (tabId.equals(tabitem.getTitleString())) {
                    tabitem.setChecked(true);
                }else {
                    tabitem.setChecked(false);
                }
            }
            if ("图标".equals(tabId) && isReviseBgChange){
                isReviseBgChange = false;
                BaseFragment fragment = (BaseFragment)getSupportFragmentManager().findFragmentByTag("图标");
                if (fragment != null)
                    fragment.notifyDataSetChanged();
            } else if ("常用背景".equals(tabId)){
                BaseFragment fragment = (BaseFragment)getSupportFragmentManager().findFragmentByTag("常用背景");
                if (fragment != null)
                    fragment.notifyDataSetChanged();
            }

        });
    }

    @Override
    protected void onDestroy() {
        setResult(-1);
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }

    class TabItem {
        //正常情况下显示的图片
        private int imageNormal;
        //tab的名字
        private String titleString;

        //tab对应的fragment
        public Class<? extends Fragment> fragmentClass;

        public View view;
        public ImageView imageView;
        public TextView textView;

        public TabItem(int imageNormal,String title,Class<? extends Fragment> fragmentClass) {
            this.imageNormal = imageNormal;
            this.titleString = title;
            this.fragmentClass =fragmentClass;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public String getTitleString() {
            return titleString;
        }

        public View getView() {
            if(view == null) {
                view = getLayoutInflater().inflate(R.layout.layout_theme_tab_indicator, null);
                imageView = (ImageView) this.view.findViewById(R.id.tab_theme_iv_image);
                textView = (TextView) this.view.findViewById(R.id.tab_theme_tv_text);
                textView.setText(getTitleString());
                imageView.setImageResource(imageNormal);
            }
            return view;
        }

        //切换tab的方法
        public void setChecked(boolean isChecked) {
            if(imageView != null) {
                imageView.setColorFilter(getColor(isChecked ? R.color.colorAccent : R.color.grey));
            }
            if(textView != null) {
                textView.setTextColor(getColor(isChecked ? R.color.colorAccent : R.color.grey));
            }
        }
    }


}
