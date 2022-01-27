package me.qiwu.colorqq.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.activity.BaseActivity;
import me.qiwu.colorqq.activity.DefaultThemeSettingActivity;
import me.qiwu.colorqq.activity.ThemeSelectActivity;
import me.qiwu.colorqq.adapter.ThemeAllTextColorListAdapter;
import me.qiwu.colorqq.adapter.ThemeCommonTextColorAdapter;
import me.qiwu.colorqq.adapter.ThemeReviseTextColorAdapter;
import me.qiwu.colorqq.manager.ActivityManager;
import me.qiwu.colorqq.theme.ThemeUtil;
import me.qiwu.colorqq.util.FileUtil;

import static me.qiwu.colorqq.theme.ThemeUtil.hexStringToByte;

public class ThemeMergeTextColorFragment extends BaseFragment implements AdapterView.OnItemClickListener, ColorPickerDialogListener {
    private String[] mTitles = {"已修改","常用","全部"};
    private String[] names = {"恢复默认","选择颜色"};

    private String mThemePath;
    private String mCurrentColorName;
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
        ThemeReviseTextColorAdapter themeReviseTextColorAdapter = new ThemeReviseTextColorAdapter(getContext(),mThemePath);
        mListViews.add(reviseLayout);
        mAdapters.add(themeReviseTextColorAdapter);
        reviseListView.setAdapter(themeReviseTextColorAdapter);

        View commonLayout = inflater.inflate(R.layout.fragment_base_list,null);
        ListView commonListView  = commonLayout.findViewById(R.id.fragment_list);
        commonListView.setOnItemClickListener(this);
        ThemeCommonTextColorAdapter themeCommonTextColorAdapter = new ThemeCommonTextColorAdapter(getContext(),mThemePath);
        mListViews.add(commonLayout);
        mAdapters.add(themeCommonTextColorAdapter);
        commonListView.setAdapter(themeCommonTextColorAdapter);

        View allLayout = inflater.inflate(R.layout.fragment_base_list,null);
        ListView allListView  = allLayout.findViewById(R.id.fragment_list);
        allListView.setOnItemClickListener(this);
        ThemeAllTextColorListAdapter themeAllTextColorListAdapter = new ThemeAllTextColorListAdapter(getContext(),mThemePath);
        mListViews.add(allLayout);
        mAdapters.add(themeAllTextColorListAdapter);
        allListView.setAdapter(themeAllTextColorListAdapter);

        viewPager.setAdapter(new TextColorAdapter());
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        mTempView = view;
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String name = view.getTag(R.id.tag_theme_color_name).toString();
        final String value = view.getTag(R.id.tag_theme_color_value).toString();
        mCurrentColorName = name;
        if ("默认".equals(value)) {
            showColorDialog(Color.BLACK);
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(name)
                    .setItems(names, (dialog, which) -> {
                        if (which == 0) {
                            new File(mThemePath + "/color/" + name).delete();
                            ((ThemeReviseTextColorAdapter)mAdapters.get(0)).delFile(name);
                            mAdapters.get(1).notifyDataSetChanged();
                            mAdapters.get(2).notifyDataSetChanged();
                        } else {
                            showColorDialog(Color.parseColor(value));
                        }
                    })
                    .create()
                    .show();
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
        ThemeSelectActivity.createColorXmlFile(getContext(),color,mThemePath,mCurrentColorName);
        ((ThemeReviseTextColorAdapter)mAdapters.get(0)).updateFile(mCurrentColorName);
        mAdapters.get(1).notifyDataSetChanged();
        mAdapters.get(2).notifyDataSetChanged();
    }


    @Override
    public void onDialogDismissed(int dialogId) {

    }

    @Override
    public void notifyDataSetChanged() {
        for (int i = 0;i < mAdapters.size();i++){
            mAdapters.get(i).notifyThemeChanged();
        }
    }


    class TextColorAdapter extends PagerAdapter {

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
            container.addView(view,-1,-1);
            return view;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }
}
