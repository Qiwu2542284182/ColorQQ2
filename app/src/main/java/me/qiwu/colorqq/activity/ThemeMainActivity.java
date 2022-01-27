package me.qiwu.colorqq.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import me.qiwu.colorqq.R;

public class ThemeMainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_theme);
    }

    @Override
    public Context getContext() {
        return this;
    }

    public void themeManager(View view){

    }

    public void themeSetting(View view){
        startActivity(new Intent(this, ThemeSettingActivity.class));
    }

    public void themeDef(View view){
        startActivity(new Intent(this, DefaultThemeSettingActivity.class));
    }
}
