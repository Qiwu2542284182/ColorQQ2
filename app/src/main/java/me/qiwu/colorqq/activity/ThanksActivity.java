package me.qiwu.colorqq.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import me.qiwu.colorqq.R;

public class ThanksActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
