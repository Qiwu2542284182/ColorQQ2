package me.qiwu.colorqq.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import me.qiwu.colorqq.R;

public class TopSettingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_setting);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
