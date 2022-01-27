package me.qiwu.colorqq.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.activity.BaseActivity;
import me.qiwu.colorqq.activity.PhotoPickActivity;
import me.qiwu.colorqq.activity.SvgPicSelectActivity;
import me.qiwu.colorqq.adapter.ThemeAllIconAdapter;
import me.qiwu.colorqq.adapter.ThemeCommonIconAdapter;
import me.qiwu.colorqq.adapter.ThemeReviseIconAdapter;
import me.qiwu.colorqq.manager.ActivityManager;
import me.qiwu.colorqq.util.BitmapUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.widget.FlexLayout;

public class ThemeMergeIconFragment extends BaseFragment implements AdapterView.OnItemClickListener, ColorPickerDialogListener {
    private String[] mTitles = {"已修改","常用","全部"};
    private String[] names = {"恢复默认","选择图片","使用透明图片","使用纯色图片"};
    private String[] names2 = {"恢复默认","选择图片","使用透明图片","使用纯色图片","使用预置图标"};
    private static final String XHPDI = "/drawable-xhdpi/";
    private String mCurrentPicName;

    private String mThemePath;
    private List<BaseThemeAdapter> mAdapters = new ArrayList<>();
    private List<View> mListViews = new ArrayList<>();
    private View mTempView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mTempView != null){
            if (mTempView.getParent() != null){
                ((ViewGroup)mTempView.getParent()).removeView(mTempView);
            }
            return mTempView;
        }

        mThemePath = getArguments().getString("theme_path");
        View view = inflater.inflate(R.layout.activity_theme_icon,container,false);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("已修改"));
        tabLayout.addTab(tabLayout.newTab().setText("常用"));
        tabLayout.addTab(tabLayout.newTab().setText("全部"));

        View reviseLayout = inflater.inflate(R.layout.fragment_base_list,null);
        ListView reviseListView = reviseLayout.findViewById(R.id.fragment_list);
        reviseListView.setOnItemClickListener(this);
        ThemeReviseIconAdapter themeReviseIconAdapter = new ThemeReviseIconAdapter(getContext(),mThemePath);
        mListViews.add(reviseLayout);
        mAdapters.add(themeReviseIconAdapter);
        reviseListView.setAdapter(themeReviseIconAdapter);

        View commonLayout = inflater.inflate(R.layout.fragment_base_list,null);
        ListView commonListView  = commonLayout.findViewById(R.id.fragment_list);
        commonListView.setOnItemClickListener(this);
        ThemeCommonIconAdapter themeCommonIconAdapter = new ThemeCommonIconAdapter(getContext(),mThemePath);
        mListViews.add(commonLayout);
        mAdapters.add(themeCommonIconAdapter);
        commonListView.setAdapter(themeCommonIconAdapter);

        View allLayout = inflater.inflate(R.layout.fragment_base_list,null);
        ListView allListView  = allLayout.findViewById(R.id.fragment_list);
        allListView.setOnItemClickListener(this);
        ThemeAllIconAdapter themeAllIconAdapter = new ThemeAllIconAdapter(getContext(),mThemePath);
        mListViews.add(allLayout);
        mAdapters.add(themeAllIconAdapter);
        allListView.setAdapter(themeAllIconAdapter);

        viewPager.setAdapter(new IconAdapter());
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        mTempView = view;
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = view.getTag(R.id.tag_theme_icon_name).toString();
        mCurrentPicName = name;
        final File file = new File(mThemePath + XHPDI + name);
        new AlertDialog.Builder(getContext())
                .setTitle(name)
                .setItems(name.endsWith(".9.png") ? names : names2, (dialog, which) -> {
                    if (which==0){
                        if (file.exists()){
                            file.delete();
                            updatePic(mCurrentPicName,false);
                        }
                    } else if (which==1){
                        PhotoPickActivity.startActivity(this,1);
                    } else if (which == 2){
                        BitmapUtil.saveColorPictures(Color.TRANSPARENT,file);
                        updatePic(mCurrentPicName,true);
                    } else if (which == 3){
                        showColorDialog(Color.BLACK);
                    } else if (which == 4){
                        Intent intent = new Intent(getContext(), SvgPicSelectActivity.class);
                        intent.putExtra("path",mThemePath + XHPDI);
                        intent.putExtra("name",mCurrentPicName);
                        startActivityForResult(intent,999);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PhotoPickActivity.CODE && data!=null){
            List<String> paths = PhotoPickActivity.obtain(data);
            try {
                FileUtil.copyFile(new FileInputStream(new File(paths.get(0))),mThemePath + XHPDI + mCurrentPicName);
                updatePic(mCurrentPicName,true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 999){
            updatePic(mCurrentPicName,true);
        }
    }

    private void showColorDialog(int color){
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newBuilder().setColor(color)
                .setDialogTitle(R.string.select_color)
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(false)
                .setDialogId(0)
                .setShowAlphaSlider(true)
                .create();
        colorPickerDialog.setColorPickerDialogListener(this);
        colorPickerDialog.show(((BaseActivity) ActivityManager.getInstance().getCurrentActivity()).getSupportFragmentManager(),"color-picker-dialog");
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        File file = new File(mThemePath + XHPDI + mCurrentPicName);
        BitmapUtil.saveColorPictures(color,file);
        updatePic(mCurrentPicName,true);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public void updatePic(String name,boolean isAdd){
        if (isAdd){
            ((ThemeReviseIconAdapter)mAdapters.get(0)).updateFile(name);
        } else {
            ((ThemeReviseIconAdapter)mAdapters.get(0)).delFile(name);
        }
        mAdapters.get(1).notifyDataSetChanged();
        mAdapters.get(2).notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        for (int i = 0;i < mAdapters.size();i++){
            mAdapters.get(i).notifyThemeChanged();
        }
    }

    class IconAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mListViews.get(position));
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = mListViews.get(position);
            container.addView(view);
            return view;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

}
