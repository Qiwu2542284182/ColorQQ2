package me.qiwu.colorqq.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import me.qiwu.colorqq.R;

public class ItemOrderActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_order);
        Intent intent = getIntent();
        if (intent!=null){
            WebView webView = findViewById(R.id.fab_web);
            webView.loadUrl("file:///android_asset/"+intent.getStringExtra("file"));
        }

    }

    @Override
    public Context getContext() {
        return this;
    }
}
