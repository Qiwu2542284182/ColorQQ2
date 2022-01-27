package me.qiwu.colorqq.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;


import me.qiwu.colorqq.R;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.SettingUtil;

public class FabMenuSettingActivity extends BaseActivity implements TextWatcher{
    private EditText mIcName;
    private EditText mMarginRight;
    private EditText mMarginBottom;
    private boolean isChange = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab_menu_setting);
        mIcName = findViewById(R.id.fab_menu_icName);
        mMarginRight = findViewById(R.id.fab_menu_marginEnd);
        mMarginBottom = findViewById(R.id.fab_menu_marginBottom);
        mIcName.setText(SettingUtil.getInstance().getString("fab_menu_icName","ic_add.png"));
        mMarginRight.setText(SettingUtil.getInstance().getString("fab_menu_marginEnd","80"));
        mMarginBottom.setText(SettingUtil.getInstance().getString("fab_menu_marginBottom","50"));
        mMarginBottom.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        mMarginBottom.addTextChangedListener(this);
        mMarginRight.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        mMarginRight.addTextChangedListener(this);
        mIcName.addTextChangedListener(this);
    }

    @Override
    public Context getContext() {
        return this;
    }

    public void selectIcon(View view){
        Intent intent = new Intent(getContext(),SvgPicSelectActivity.class);
        intent.putExtra("path",FileUtil.getFabPath());
        startActivityForResult(intent,999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            mIcName.setText(data.getStringExtra("pic"));
            isChange = true;
        }

    }

    @Override
    public void finish() {
        if (isChange){
            new AlertDialog.Builder(getContext())
                    .setTitle("提示")
                    .setMessage("数据已改变，是否保存？")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (SettingUtil.getInstance().getEditor().putString("fab_menu_icName",mIcName.getText().toString())
                            .putString("fab_menu_marginEnd",mMarginRight.getText().toString())
                            .putString("fab_menu_marginBottom",mMarginBottom.getText().toString()).commit()){
                                FileUtil.setWorldReadable(BaseApplication.getContext());
                                exit();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exit();
                        }
                    })
                    .create()
                    .show();
        } else {
            exit();
        }
    }

    private void exit(){
        super.finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isChange = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
