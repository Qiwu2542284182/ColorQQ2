package me.qiwu.colorqq.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;


import me.qiwu.colorqq.R;
import me.qiwu.colorqq.widget.TextItem;
import me.qiwu.colorqq.BuildConfig;


public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ((TextItem)findViewById(R.id.about_version)).setRightText(BuildConfig.VERSION_NAME);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
