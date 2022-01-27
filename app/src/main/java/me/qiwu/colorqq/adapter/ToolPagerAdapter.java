package me.qiwu.colorqq.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deng on 2018/10/16.
 */

public class ToolPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment>fragments=new ArrayList<>();
    private String[] title ;
    private FragmentManager fragmentManager;

    public ToolPagerAdapter(FragmentManager fm, String[] title) {
        super(fm);
        fragmentManager = fm;
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.isEmpty()?null:fragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    public void setFragments(List<Fragment> fragmentArrayList){
        fragments.clear();
        fragments.addAll(fragmentArrayList);
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragmentManager.beginTransaction().show(fragment).commit();
        return fragment;
    }





}
