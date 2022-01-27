package me.qiwu.colorqq.library.NineGridView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.io.File;

import me.qiwu.colorqq.R;

public class NineGirdPreviewFragment extends Fragment {

    public static NineGirdPreviewFragment newInstance(String path){
        NineGirdPreviewFragment fragment = new NineGirdPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path",path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nine_grid_preview,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String path = getArguments().getString("path");
        if (TextUtils.isEmpty(path)) return;
        ImageView image = view.findViewById(R.id.nine_grid_img);
        Picasso.get().load(new File(path)).noFade().into(image);
    }
}
