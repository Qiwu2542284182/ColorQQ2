package me.qiwu.colorqq.library.NineGridView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import me.qiwu.colorqq.R;

public class NineGridImagePreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nine_grid_preview);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();
        ArrayList<String> paths = bundle.getStringArrayList("paths");
        int position = bundle.getInt("position");
        ViewPager viewPager = findViewById(R.id.nine_grid_viewPager);
        NineGridPreviewAdapter adapter = new NineGridPreviewAdapter(getSupportFragmentManager());
        adapter.addAll(paths);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }
}
