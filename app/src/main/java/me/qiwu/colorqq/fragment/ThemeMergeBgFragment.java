package me.qiwu.colorqq.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.activity.BaseActivity;
import me.qiwu.colorqq.activity.PhotoPickActivity;
import me.qiwu.colorqq.activity.ThemeIconAndColorSettingActivity;
import me.qiwu.colorqq.annotation.BindView;
import me.qiwu.colorqq.manager.ActivityManager;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.PicassoUtil;
import me.qiwu.colorqq.util.SettingUtil;

public class ThemeMergeBgFragment extends BaseFragment implements View.OnClickListener {
    @BindView.ViewResId(R.id.theme_bg_main)
    private ImageView mainBg;
    @BindView.ViewResId(R.id.theme_bg_setting)
    private ImageView settingBg;
    @BindView.ViewResId(R.id.theme_bg_splash)
    private ImageView splashBg;
    @BindView.ViewResId(R.id.theme_bg_drawer)
    private ImageView drawerBg;
    @BindView.ViewResId(R.id.theme_bg_chat)
    private ImageView chatBg;
    @BindView.ViewResId(R.id.theme_bg_setting_exit)
    private TextView exitTip;
    @BindView.ViewResId(R.id.theme_bg_tip)
    private RelativeLayout tipLayout;

    private String mTempPath;
    private String mThemePath;
    private View mTempView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        if (mTempView != null){
            if (mTempView.getParent() != null){
                ((ViewGroup)mTempView.getParent()).removeView(mTempView);
            }
            return mTempView;
        }
        View view = inflater.inflate(R.layout.activity_theme_bg_setting,container,false);
        BindView.load(this,view);
        mThemePath = getArguments().getString("theme_path");
        initLayout(mainBg,"/drawable-xhdpi/skin_background_theme_version2.png");
        initLayout(drawerBg,"/drawable-xhdpi/qq_setting_me_bg.png");
        initLayout(chatBg,"/drawable-xhdpi/skin_chat_background.png");
        initLayout(settingBg,"/drawable-xhdpi/skin_setting_background.png");
        initLayout(splashBg,"/assets/splash.jpg");

        if (SettingUtil.getInstance().getBoolean("theme_show_tip",true)){
            tipLayout.setVisibility(View.VISIBLE);
            exitTip.setOnClickListener(this);
        } else {
            tipLayout.setVisibility(View.GONE);
        }
        loadBg();
        mTempView = view;
        return view;
    }

    private void initLayout(ImageView imageView,String path){
        ViewGroup viewGroup = (ViewGroup) imageView.getParent();
        String tempPath = mThemePath + path;
        viewGroup.setOnClickListener(v -> {
            String[] names = {"恢复默认", "选择图片"};
            new AlertDialog.Builder(getContext())
                    .setTitle("请选择")
                    .setItems(names, (dialog, which) -> {
                        if (which == 0){
                            new File(tempPath).delete();
                            imageView.setImageDrawable(null);
                            ThemeIconAndColorSettingActivity.isReviseBgChange = true;
                        } else {
                            mTempPath = tempPath;
                            PhotoPickActivity.startActivity(ThemeMergeBgFragment.this,1);
                        }
                    }).create().show();
        });
    }

    @Override
    public void onClick(View v) {
        if (v == exitTip){
            SettingUtil.getInstance().putBoolean("theme_show_tip",false);
            tipLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PhotoPickActivity.CODE && data!=null){
            List<String> paths = PhotoPickActivity.obtain(data);
            FileUtil.copyFile(paths.get(0),mTempPath);
            loadBg();
            ThemeIconAndColorSettingActivity.isReviseBgChange = true;
        }
    }

    private void loadBg(){
        PicassoUtil.loadFile(mainBg,mThemePath + "/drawable-xhdpi/skin_background_theme_version2.png");
        PicassoUtil.loadFile(settingBg,mThemePath + "/drawable-xhdpi/skin_setting_background.png");
        PicassoUtil.loadFile(splashBg,mThemePath + "/assets/splash.jpg");
        PicassoUtil.loadFile(drawerBg,mThemePath + "/drawable-xhdpi/qq_setting_me_bg.png");
        PicassoUtil.loadFile(chatBg,mThemePath + "/drawable-xhdpi/skin_chat_background.png");

    }

    @Override
    public void notifyDataSetChanged() {
        loadBg();
    }
}
