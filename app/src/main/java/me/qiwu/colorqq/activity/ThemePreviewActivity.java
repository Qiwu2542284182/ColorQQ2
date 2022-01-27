package me.qiwu.colorqq.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Space;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.theme.BaseConstantState;
import me.qiwu.colorqq.theme.ISkinWidget;
import me.qiwu.colorqq.util.DensityUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.SettingUtil;
import me.qiwu.colorqq.util.StatusBarUtil;
import me.qiwu.colorqq.widget.TitleBar;

public class ThemePreviewActivity extends BaseActivity {
    private static int width;
    private static int height;
    private static float scale;
    private static boolean isInit = false;
    private String mCurrentThemePath;

    private Button mSetThemeButton;
    private RecyclerView mRecyclerView;
    private TitleBar mTitleBar;
    private ViewGroup mQQSettingme;
    private ViewGroup mMainView;
    private ViewGroup mChatView;
    private ViewGroup mSplashView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_pre);
        mCurrentThemePath = SettingUtil.getInstance().getString("current_theme_path",new File(FileUtil.getDefThemePath()).getAbsolutePath());
        mSetThemeButton = findViewById(R.id.theme_pre_set_button);
        mSetThemeButton.setOnClickListener(v -> {
            v.setEnabled(false);
            mSetThemeButton.setText("使用中");
            Toast.makeText(getContext(),"已应用",Toast.LENGTH_SHORT).show();
            SettingUtil.getInstance().putString("current_theme_path",BaseConstantState.THEME_PATH);
            setResult(Activity.RESULT_CANCELED,new Intent().putExtra("theme_path",BaseConstantState.THEME_PATH));
        });
        mRecyclerView = findViewById(R.id.theme_pre_RecyclerView);
        mTitleBar = findViewById(R.id.theme_pre_titleBar);
        Intent intent = getIntent();
        mTitleBar.setTitle(intent.getStringExtra("theme_name"));
        BaseConstantState.THEME_PATH = intent.getStringExtra("theme_path");
        if (mCurrentThemePath.equals(BaseConstantState.THEME_PATH)){
            mSetThemeButton.setEnabled(false);
            mSetThemeButton.setText("使用中");
        }
        initWidthAndHeight();
        initView();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setItemViewCacheSize(4);
        mRecyclerView.setAdapter(new ThemePreViewAdapter2(mSplashView,mQQSettingme,mMainView,mChatView));
        mTitleBar.setRightText("编辑主题", v -> {
            Intent intent1 = new Intent(getContext(),ThemeIconAndColorSettingActivity.class);
            intent1.putExtra("theme_name",intent.getStringExtra("theme_name"));
            intent1.putExtra("theme_path",BaseConstantState.THEME_PATH);
            startActivityForResult(intent1,998);
        });
    }

    private void initView(){
        Context context = getContext();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        DisplayMetrics systemdisplayMetrics = new DisplayMetrics();
        systemdisplayMetrics.setTo(displayMetrics);
        displayMetrics.density = systemdisplayMetrics.density * scale;
        displayMetrics.scaledDensity = systemdisplayMetrics.scaledDensity * scale;
        displayMetrics.densityDpi = (int) (scale * ((float)systemdisplayMetrics.densityDpi));

        mQQSettingme = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.skin_pre_qqsettingme,null);
        mMainView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.skin_pre_main,null);
        mChatView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.skin_pre_chat,null);
        mSplashView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.skin_pre_splash,null);

        displayMetrics.setTo(systemdisplayMetrics);
    }

    private void initWidthAndHeight(){
        if (!isInit){
            WindowManager windowManager = getWindowManager();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int d_width = displayMetrics.widthPixels;
            int d_height = displayMetrics.heightPixels;
            if (d_height - StatusBarUtil.getStatusBarHeight(getContext()) - DensityUtil.dip2px(getContext(),134f) > (int) (d_height * 0.7)){
                scale = 0.7f;
            } else {
                scale = 0.6f;
            }
            height = (int) (d_height * scale);
            width = (int) (d_width * scale);

            isInit = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        reloadTheme(mQQSettingme);
        reloadTheme(mMainView);
        reloadTheme(mChatView);
        reloadTheme(mSplashView);
    }

    private void reloadTheme(ViewGroup viewGroup){
        if (viewGroup instanceof ISkinWidget){
            ((ISkinWidget)viewGroup).loadSkin();
        }
        for (int i = 0;i < viewGroup.getChildCount();i++){
            View view = viewGroup.getChildAt(i);
            if (view instanceof ISkinWidget){
                ((ISkinWidget)view).loadSkin();
            }
            if (view instanceof ViewGroup){
                reloadTheme((ViewGroup)view);
            }
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        setResult(-1);
        super.onDestroy();
    }

    public static class ThemePreViewAdapter2 extends RecyclerView.Adapter<ThemePreViewAdapter2ItemHolder>{
        private ViewGroup mQQSettingme;
        private ViewGroup mMainView;
        private ViewGroup mChatView;
        private ViewGroup mSplashView;
        public ThemePreViewAdapter2(ViewGroup mSplashView,ViewGroup mQQSettingme,ViewGroup mMainView,ViewGroup mChatView){
            this.mSplashView = mSplashView;
            this.mQQSettingme = mQQSettingme;
            this.mMainView = mMainView;
            this.mChatView = mChatView;

        }

        @NonNull
        @Override
        public ThemePreViewAdapter2ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            return new ThemePreViewAdapter2ItemHolder(frameLayout);
        }

        @Override
        public void onBindViewHolder(@NonNull ThemePreViewAdapter2ItemHolder holder, int position) {
            View view = position == 0  ? mQQSettingme : position == 1 ? mMainView :  position == 2 ? mChatView : mSplashView;
            if (view.getParent() != null){
                ((ViewGroup)view.getParent()).removeView(view);
            }
            ViewGroup viewGroup = holder.getViewGroup();
            viewGroup.removeAllViews();
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width,height);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            layoutParams.leftMargin = DensityUtil.dip2px(view.getContext(),10f);
            layoutParams.rightMargin = DensityUtil.dip2px(view.getContext(),10f);
            viewGroup.addView(view,layoutParams);
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    public static class ThemePreViewAdapter2ItemHolder extends RecyclerView.ViewHolder {
        private ViewGroup viewGroup;
        public ThemePreViewAdapter2ItemHolder(@NonNull View itemView) {
            super(itemView);
            viewGroup = (ViewGroup) itemView;
        }


        public ViewGroup getViewGroup() {
            return viewGroup;
        }
    }
}
