package me.qiwu.colorqq.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.library.RCLayout.RCImageView;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.SettingUtil;
import me.qiwu.colorqq.widget.ArrowItem;
import me.qiwu.colorqq.widget.ColorPreference;
import me.qiwu.colorqq.widget.SelectItem;


public class TabSettingActivity extends BaseActivity {
    private RCImageView mPreTabBg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_setting);

        SelectItem tabBg = findViewById(R.id.tab_bg_mode);
        ViewGroup tabIconBg = findViewById(R.id.tab_bg_icon_setting);
        mPreTabBg = (RCImageView) tabIconBg.getChildAt(0);
        tabIconBg.setOnClickListener(v ->
                PhotoPickActivity.startActivity(TabSettingActivity.this,1));
        LinearLayout tabColorBg = findViewById(R.id.tab_bg_color_setting);
        int tabBgMode = SettingUtil.getInstance().getInt("tab_bg_mode");
        if (tabBgMode == 0){
            tabColorBg.setVisibility(View.VISIBLE);
            tabIconBg.setVisibility(View.GONE);
        } else {
            tabColorBg.setVisibility(View.GONE);
            tabIconBg.setVisibility(View.VISIBLE);
        }
        tabBg.setOnSelectChangeListener(new SelectItem.OnSelectChangeListener() {
            @Override
            public void onChange(int position) {
                if (position == 0){
                    tabColorBg.setVisibility(View.VISIBLE);
                    tabIconBg.setVisibility(View.GONE);
                } else {
                    tabColorBg.setVisibility(View.GONE);
                    tabIconBg.setVisibility(View.VISIBLE);
                }
            }
        });

        SelectItem selectItem = findViewById(R.id.tab_color_mode);
        final ColorPreference colorPreference = findViewById(R.id.tab_color);
        LinearLayout linearLayout = findViewById(R.id.tab_content_icon);
        colorPreference.setVisibility(SettingUtil.getInstance().getInt("tab_mode") == 0 ? View.GONE : View.VISIBLE);
        selectItem.setOnSelectChangeListener(new SelectItem.OnSelectChangeListener() {
            @Override
            public void onChange(int position) {
                colorPreference.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

            }
        });
        final ArrowItem arrowItem = findViewById(R.id.tab_content_name);
        arrowItem.setVisibility(SettingUtil.getInstance().getInt("tab_content_mode") == 0 ? View.GONE : View.VISIBLE);
        linearLayout.setVisibility(SettingUtil.getInstance().getInt("tab_content_mode") == 0 ? View.VISIBLE : View.GONE);
        SelectItem selectItem1 = findViewById(R.id.tab_content_mode);
        selectItem1.setOnSelectChangeListener(position -> {
            arrowItem.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            linearLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        });
        arrowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_tab_content_names,null);
                final EditText editText = view.findViewById(R.id.tab_message_name);
                final EditText editText1 = view.findViewById(R.id.tab_contact_name);
                final EditText editText3 = view.findViewById(R.id.tab_leba_name);
                final EditText editText4 = view.findViewById(R.id.tab_text_size);
                editText.setText(SettingUtil.getInstance().getString("tab_message_name","消息"));
                editText1.setText(SettingUtil.getInstance().getString("tab_contact_name","联系人"));
                editText3.setText(SettingUtil.getInstance().getString("tab_leba_name","动态"));
                editText4.setText(SettingUtil.getInstance().getString("tab_text_size","15"));
                final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("设置名称")
                        .setView(view)
                        .setPositiveButton(R.string.ok,null)
                        .setNegativeButton(R.string.cancel,null)
                        .create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                    if (TextUtils.isEmpty(editText.getText()) || TextUtils.isEmpty(editText1.getText()) || TextUtils.isEmpty(editText3.getText())|| TextUtils.isEmpty(editText4.getText())){
                        Toast.makeText(getContext(),"请检查数据是否完整",Toast.LENGTH_SHORT).show();
                    } else {
                        SettingUtil.getInstance().getEditor()
                                .putString("tab_message_name",editText.getText().toString())
                                .putString("tab_contact_name",editText1.getText().toString())
                                .putString("tab_leba_name",editText3.getText().toString())
                                .putString("tab_text_size",editText4.getText().toString())
                                .commit();
                        FileUtil.setWorldReadable(getContext());
                        alertDialog.dismiss();
                    }
                });
            }
        });
        ColorPreference colorPreference1 = findViewById(R.id.tab_unread_color);
        colorPreference1.setVisibility(SettingUtil.getInstance().getInt("tab_unread_color_mode") == 0 ? View.GONE : View.VISIBLE);
        SelectItem selectItem2 = findViewById(R.id.tab_unread_color_mode);
        selectItem2.setOnSelectChangeListener(new SelectItem.OnSelectChangeListener() {
            @Override
            public void onChange(int position) {
                colorPreference1.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBg();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PhotoPickActivity.CODE && data!=null){
            List<String> paths = PhotoPickActivity.obtain(data);
            try {
                FileUtil.copyFile(new FileInputStream(new File(paths.get(0))),FileUtil.getTabIconPath("tab_bg.png"));
                loadBg();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void loadBg(){
        new Picasso.Builder(getContext()).build().load(new File(FileUtil.getTabIconPath("tab_bg.png"))).noFade().resize(100,100).centerCrop().into(mPreTabBg);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
