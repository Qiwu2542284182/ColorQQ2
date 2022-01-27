package me.qiwu.colorqq.library.NineGridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class NineGridPreviewAdapter extends FragmentPagerAdapter {
    private List<String> paths = new ArrayList<>();
    public NineGridPreviewAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return NineGirdPreviewFragment.newInstance(paths.get(position));
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    public void addAll(List<String> paths){
        this.paths.clear();
        this.paths.addAll(paths);
    }
}
