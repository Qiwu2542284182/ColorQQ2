package me.qiwu.colorqq.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import me.qiwu.colorqq.R;

public class CrashActivity extends BaseActivity implements View.OnClickListener {
    private TextView mCrashTextView;
    private TextView mCrashButton;
    private String mCrashMsg ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        mCrashTextView = findViewById(R.id.crash_text);
        mCrashButton = findViewById(R.id.crash_button);
        mCrashButton.setOnClickListener(this);
        mCrashMsg = "手机型号：" + Build.BRAND + "-" + Build.MODEL + "\n" +
                "系统版本：" + Build.VERSION.RELEASE + "\n" +
                "日志：" + CustomActivityOnCrash.getStackTraceFromIntent(getIntent());;
        mCrashTextView.setText(mCrashMsg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onClick(View v) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("label",mCrashMsg);
        cm.setPrimaryClip(mClipData);
        Toast.makeText(getContext(),"已复制到剪贴板",Toast.LENGTH_SHORT).show();
    }
}
