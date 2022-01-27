package me.qiwu.colorqq.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import bin.zip.ZipEntry;
import bin.zip.ZipFile;
import me.qiwu.colorqq.R;
import me.qiwu.colorqq.adapter.ThemeSelectAdapter;
import me.qiwu.colorqq.drawable.NinePatch;
import me.qiwu.colorqq.theme.ThemeUtil;
import me.qiwu.colorqq.util.BitmapUtil;
import me.qiwu.colorqq.util.DialogUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.SettingUtil;
import me.qiwu.colorqq.widget.TitleBar;

import static me.qiwu.colorqq.theme.ThemeUtil.hexStringToByte;
import static me.qiwu.colorqq.theme.ThemeUtil.sColor;
import static me.qiwu.colorqq.util.BitmapUtil.getTintDrawable;

public class ThemeSelectActivity extends BaseActivity implements Handler.Callback{
    private static final int SUCCESS = 0;
    private static final int ERROR = 1;
    private static final int ERROR_DIALOG = 2;
    private static final int UPDATE_LIST = 3;

    private RecyclerView mRecyclerView;
    private List<LocalThemeInfo> mThemeInfos = new ArrayList<>();
    private String mCurrentThemePath;
    private ThemeSelectAdapter mAdapter;
    private LocalThemeInfo defThemeInfo;
    private TitleBar mTitleBar;
    private ViewGroup mFunLayout;
    private TextView mDelTextView;
    private TextView mRenameTextView;
    private Button mSettingButton;
    private boolean isFunMode;
    private boolean isFirstLoad = true;
    private List<Integer> mCheckItem = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler(this);

    private final static String[] sTintColor = {
            "skin_blue.xml",
            "skin_blue_item.xml",
            "skin_blue_link.xml",
            "skin_color_title_immersive_bar.xml",
            "skin_float_btn.xml",
            "skin_search_button.xml",
            "skin_search_button_theme_version2.xml",
            "skin_chat_buble_link.xml"
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_select);
        mCurrentThemePath = SettingUtil.getInstance().getString("current_theme_path",new File(FileUtil.getDefThemePath()).getAbsolutePath());
        File currentFile = new File(mCurrentThemePath);
        if (!currentFile.exists()){
            mCurrentThemePath = new File(FileUtil.getDefThemePath()).getAbsolutePath();
            SettingUtil.getInstance().putString("current_theme_path",mCurrentThemePath);
        }
        mFunLayout = findViewById(R.id.theme_select_bottom_fun);
        mDelTextView = findViewById(R.id.theme_select_del);
        mDelTextView.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("提示")
                    .setMessage("是否删除选中主题？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (mCheckItem.size() > 0){
                            for (int i = 0; i < mCheckItem.size() ;i++){
                                LocalThemeInfo themeInfo = mThemeInfos.get(mCheckItem.get(i));
                                if (FileUtil.delAllFile(themeInfo.themePath)){
                                    mThemeInfos.remove(themeInfo);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getContext(),"删除主题：" + themeInfo.themeName + "失败",Toast.LENGTH_SHORT).show();
                                }
                            }

                            unInitFun();
                        }
                    })
                    .setNegativeButton("取消",null)
                    .create().show();

        });
        mRenameTextView = findViewById(R.id.theme_select_rename);
        mRenameTextView.setOnClickListener(v -> {
            if (mCheckItem.size() == 1){
                LocalThemeInfo themeInfo = mThemeInfos.get(mCheckItem.get(0));
                DialogUtil.showEditViewDialog(getContext(), "重命名", themeInfo.themeName, false, editText -> {
                    themeInfo.themeName = editText.getText().toString();
                    File file = new File(new File(themeInfo.themePath),"theme");
                    file.delete();
                    FileUtil.writeToFile(file,themeInfo.themeName);
                    unInitFun();
                });


            }
        });
        mSettingButton = findViewById(R.id.theme_select_add_button);
        mSettingButton.setOnClickListener(v -> {
            String[] names = new String[]{"新建主题","导入Zip主题","导入Theme主题"};
            new AlertDialog.Builder(getContext())
                    .setTitle("请选择")
                    .setItems(names, (dialog, which) -> {
                        if (which == 0){
                            final int[] defColor = {0xff009688};
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_theme_create,null);
                            EditText editText = view.findViewById(R.id.theme_create_name);
                            ImageView imageView = view.findViewById(R.id.theme_create_color_pre);
                            GradientDrawable drawable = (GradientDrawable) imageView.getBackground();
                            drawable.setColor(defColor[0]);
                            drawable.invalidateSelf();
                            ((ViewGroup)imageView.getParent()).setOnClickListener(v1 ->{
                                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                                DialogUtil.showColorDialog(getSupportFragmentManager(),true, new ColorPickerDialogListener() {
                                    @Override
                                    public void onColorSelected(int dialogId, int color) {
                                        defColor[0] = color;
                                        GradientDrawable drawable = (GradientDrawable) imageView.getBackground();
                                        drawable.setColor(defColor[0]);
                                        drawable.invalidateSelf();
                                    }

                                    @Override
                                    public void onDialogDismissed(int dialogId) {

                                    }
                                });
                            } );
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                    .setTitle("新建主题")
                                    .setView(view)
                                    .setPositiveButton("确定", null)
                                    .setNegativeButton("取消",null)
                                    .create();
                            alertDialog.show();
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v12 -> {
                                if (TextUtils.isEmpty(editText.getText())){
                                    Toast.makeText(getContext(),"请输入主题名称",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String themeName = editText.getText().toString();
                                String themePath = FileUtil.getThemePath() + System.currentTimeMillis();

                                File tempFile = new File(getExternalCacheDir(),"theme.zip");
                                try {
                                    FileUtil.copyFile(getAssets().open("theme.zip"),tempFile.getAbsolutePath());
                                    FileUtil.unZip2(tempFile.getAbsolutePath(),themePath);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                    DialogUtil.showTip(getContext(), Log.getStackTraceString(e));
                                    return;
                                }

                                loadTintColor(getContext(),defColor[0],themePath);
                                loadTintDrawable(getContext(),defColor[0],themePath);
                                FileUtil.writeToFile(new File(themePath + "/theme"),themeName);
                                LocalThemeInfo themeInfo = new LocalThemeInfo();
                                themeInfo.prePicPath = LocalThemeInfo.getPrePicPath(themePath);
                                themeInfo.themeName = themeName;
                                themeInfo.themePath = themePath;
                                mThemeInfos.add(themeInfo);
                                mAdapter.notifyDataSetChanged();
                                alertDialog.dismiss();
                            });

                        } else if (which == 1){
                            FileSelectActivity.start(ThemeSelectActivity.this,FileSelectActivity.ZIP);
                        } else if (which == 2){
                            FileSelectActivity.start(ThemeSelectActivity.this,FileSelectActivity.THEME);
                        }
                    }).create().show();
        });
        mTitleBar = findViewById(R.id.theme_select_titleBar);
        mTitleBar.setRightImage(getDrawable(R.drawable.ic_settings), v -> startActivity(new Intent(getContext(),ThemeSettingActivity.class)));
        mTitleBar.setRightImage2(getDrawable(R.drawable.ic_dashboard), v -> {
            if (mThemeInfos.size() > 1){
                initFun();
            } else {
                Toast.makeText(getContext(),"没有可操作的主题",Toast.LENGTH_SHORT).show();
            }

        });


        mRecyclerView = findViewById(R.id.theme_select_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));


        File themeDir = new File(FileUtil.getThemePath());
        String[] names = themeDir.list();
        if (names != null){
            for (String name : names){
                boolean isTheme = false;
                String themeName = "";
                boolean isDefTheme = false;
                if ("默认主题".equals(name)){
                    isTheme = true;
                    isDefTheme = true;
                    themeName = name;
                } else {
                    File themeNameFile = new File(new File(themeDir,name),"theme");
                    if (themeNameFile.exists()){
                        isTheme = true;
                        themeName = FileUtil.readTextFromFile(themeNameFile);
                    }
                }
                if (isTheme){
                    LocalThemeInfo themeInfo = new LocalThemeInfo();
                    themeInfo.isDefTheme = isDefTheme;
                    themeInfo.themeName = TextUtils.isEmpty(themeName) ? "未知主题" : themeName.replace("\n","");
                    themeInfo.themePath = new File(themeDir,name).getAbsolutePath();
                    themeInfo.isUse = mCurrentThemePath.equals(themeInfo.themePath);
                    themeInfo.prePicPath = LocalThemeInfo.getPrePicPath(themeInfo.themePath);
                    if (isDefTheme){
                        defThemeInfo = themeInfo;
                    } else {
                        mThemeInfos.add(themeInfo);
                    }

                }

            }
        }
        if (defThemeInfo == null){
            defThemeInfo = new LocalThemeInfo();
            defThemeInfo.themePath = new File(FileUtil.getDefThemePath()).getAbsolutePath();
            defThemeInfo.themeName = "默认主题";
            defThemeInfo.isDefTheme = true;
            defThemeInfo.isUse = mCurrentThemePath.equals(defThemeInfo.themePath);
            defThemeInfo.prePicPath = LocalThemeInfo.getPrePicPath(defThemeInfo.themePath);
        }
        mThemeInfos.add(0,defThemeInfo);

        mAdapter = new ThemeSelectAdapter(R.layout.item_theme_select,mThemeInfos);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter1, view, position) -> {
            if (!isFunMode){
                Intent intent = new Intent(view.getContext(), ThemePreviewActivity.class);
                intent.putExtra("theme_path",mThemeInfos.get(position).themePath);
                intent.putExtra("theme_name",mThemeInfos.get(position).themeName);
                startActivityForResult(intent,99);
            } else {
                if (position != 0){
                    if (mCheckItem.contains(position)){
                        mCheckItem.remove(Integer.valueOf(position));
                        mThemeInfos.get(position).isCheck = false;
                    } else {
                        mCheckItem.add(position);
                        mThemeInfos.get(position).isCheck = true;
                    }
                    mAdapter.notifyDataSetChanged();
                    mRenameTextView.setEnabled(mCheckItem.size() == 1);
                    mRenameTextView.setTextColor(mRenameTextView.isEnabled() ? 0xFF4286F3 : 0x664286F3);
                    mDelTextView.setEnabled(mCheckItem.size() > 0);
                    mDelTextView.setTextColor(mDelTextView.isEnabled() ? 0xffff0000 : 0x66ff0000);
                    mDelTextView.setText(mCheckItem.size() > 0 ? "删除(" + mCheckItem.size() + ")" : "删除");
                }
            }

        });
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            Intent intent = new Intent(getContext(),ThemeIconAndColorSettingActivity.class);
            LocalThemeInfo themeInfo = mThemeInfos.get(position);
            intent.putExtra("theme_path",themeInfo.themePath);
            intent.putExtra("theme_name",themeInfo.themeName);
            startActivity(intent);
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FileSelectActivity.SELECT_FILE_CODE && resultCode == Activity.RESULT_OK && data != null){
            String path = data.getStringExtra("path");
            if (!TextUtils.isEmpty(path)){
                mProgressDialog = ProgressDialog.show(getContext(),"提示","正在导入，请稍后",true);
                if (path.endsWith(".theme")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                HashMap hashMap = (HashMap) FileUtil.getObjectFromFile(new File(path));
                                JSONObject jsonObject = new JSONObject(ThemeUtil.decrypt("json",hashMap.get("theme").toString()));
                                String id = jsonObject.getString("id");
                                String name = jsonObject.getString("name");
                                byte[] bytes = ThemeUtil.hexStringToByte(hashMap.get("file").toString());
                                File file = new File(getCacheDir(),"temp");
                                FileUtil.copyFile(new ByteArrayInputStream(bytes),file.getAbsolutePath());
                                net.lingala.zip4j.ZipFile zipFile = new net.lingala.zip4j.ZipFile(file,ThemeUtil.encrypt("00",id).toCharArray());
                                //zipFile.setRunInThread(true);
                                String themePath = FileUtil.getThemePath() + System.currentTimeMillis();
                                zipFile.extractAll(themePath);
                                FileUtil.writeToFile(new File(themePath + "/theme"),name);
                                new File(themePath + "/theme.xml").delete();

                                LocalThemeInfo themeInfo = new LocalThemeInfo();
                                themeInfo.prePicPath = LocalThemeInfo.getPrePicPath(themePath);
                                themeInfo.themeName = name;
                                themeInfo.themePath = themePath;
                                mThemeInfos.add(themeInfo);
                                mHandler.sendEmptyMessage(UPDATE_LIST);
                            }catch (Throwable throwable){
                                Message message = Message.obtain();
                                message.what = ERROR_DIALOG;
                                message.obj = "读取主题失败\n" + Log.getStackTraceString(throwable);
                                mHandler.sendMessage(message);
                            }

                            mHandler.sendEmptyMessage(SUCCESS);
                        }
                    }).start();
                } else {
                    try {
                        ZipFile zipFile = new ZipFile(path);
                        ZipEntry zipEntry = zipFile.getEntry("theme");
                        if (zipEntry == null){
                            Toast.makeText(getContext(), "读取主题名称失败，不存在主题名称文件",Toast.LENGTH_SHORT).show();
                            DialogUtil.showEditViewDialog(getContext(), "请输入主题名称", "", false, new DialogUtil.OnEdittextClick() {
                                @Override
                                public void onClick(EditText editText) {
                                    String s = editText.getText().toString();
                                    if (TextUtils.isEmpty(s)){
                                        Toast.makeText(getContext(), "主题名称不能为空",Toast.LENGTH_SHORT).show();
                                    } else {
                                        String themePath = FileUtil.getThemePath() + System.currentTimeMillis();
                                        FileUtil.writeToFile(new File(themePath,"theme"),s);
                                        unTheme(path,s,themePath);
                                    }
                                }
                            });
                        } else {
                            String themeName = FileUtil.readTextFromInputStream(zipFile.getInputStream(zipEntry)).replace("\n","");
                            String themePath = FileUtil.getThemePath() + System.currentTimeMillis();
                            unTheme(path,themeName,themePath);
                        }

                        zipFile.close();

                    } catch (Throwable e) {
                        DialogUtil.showTip(getContext(),Log.getStackTraceString(e));
                    }
                }
            }
        } else if (requestCode == 99 && resultCode == Activity.RESULT_CANCELED && data != null){
            mCurrentThemePath = data.getStringExtra("theme_path");
            mAdapter.notifyDataSetChanged();
        }
    }

    private void unTheme(String zipPath,String themeName,String themePath){
        new Thread(() -> {
            try{
                FileUtil.unZip2(zipPath,themePath);
                LocalThemeInfo themeInfo = new LocalThemeInfo();
                themeInfo.prePicPath = LocalThemeInfo.getPrePicPath(themePath);
                themeInfo.themeName = themeName;
                themeInfo.themePath = themePath;
                mThemeInfos.add(themeInfo);
                mHandler.sendEmptyMessage(UPDATE_LIST);
            } catch (Throwable e){
                Message message = Message.obtain();
                message.what = ERROR_DIALOG;
                message.obj = "读取主题失败\n" + Log.getStackTraceString(e);
                mHandler.sendMessage(message);
            }
            mHandler.sendEmptyMessage(SUCCESS);
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstLoad){
            isFirstLoad = false;
            return;
        }
        for (int i = 0;i < mThemeInfos.size();i++){
            LocalThemeInfo themeInfo = mThemeInfos.get(i);
            themeInfo.isUse = themeInfo.themePath.equals(mCurrentThemePath);
            themeInfo.prePicPath = LocalThemeInfo.getPrePicPath(themeInfo.themePath);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initFun(){
        isFunMode = true;
        mAdapter.setFunMode(true);
        mSettingButton.setVisibility(View.GONE);
        mFunLayout.setVisibility(View.VISIBLE);
        mDelTextView.setEnabled(false);
        mRenameTextView.setEnabled(false);
        mTitleBar.setTitle("选择主题");
        mTitleBar.getLeftIcon().setOnClickListener(v -> unInitFun());
        mTitleBar.getRightIcon().setVisibility(View.GONE);
        mTitleBar.getRightIcon2().setVisibility(View.GONE);
    }

    private void unInitFun(){
        isFunMode = false;
        mAdapter.setFunMode(false);
        mSettingButton.setVisibility(View.VISIBLE);
        mFunLayout.setVisibility(View.GONE);
        mDelTextView.setText("删除");
        mRenameTextView.setTextColor(0x664286F3);
        mDelTextView.setTextColor(0x66ff0000);
        mTitleBar.setTitle("主题管理");
        mTitleBar.getLeftIcon().setOnClickListener(v -> finish());
        mTitleBar.getRightIcon().setVisibility(View.VISIBLE);
        mTitleBar.getRightIcon2().setVisibility(View.VISIBLE);
        mCheckItem.clear();
        for (int i = 0; i < mThemeInfos.size() ;i++){
            mThemeInfos.get(i).isCheck = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (isFunMode){
            unInitFun();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public Context getContext() {
        return this;
    }

    public static void loadTintColor(Context context,int color,String path){
        for (String name : sTintColor){
            createColorXmlFile(context,color,path,name);
        }
    }

    public static void loadTintDrawable(Context context,int color,String themePath){
        File tempFile = new File(context.getExternalCacheDir(),"baseTheme.zip");
        try {
            if (FileUtil.copyFile(context.getAssets().open("baseTheme.zip"),tempFile.getAbsolutePath())){
                ZipFile zipFile = new ZipFile(tempFile);
                Enumeration<ZipEntry> zipEntries =  zipFile.getEntries();
                while (zipEntries.hasMoreElements()){
                    ZipEntry zipEntry = zipEntries.nextElement();
                    String path = themePath + "/drawable-xhdpi/" + zipEntry.getName();
                    String iconName = zipEntry.getName();
                    Bitmap bitmap = BitmapFactory.decodeStream(zipFile.getInputStream(zipEntry));
                    if (BitmapUtil.isNinePatch(bitmap)) {
                        NinePatch ninePatch = null;
                        if ("skin_tips_newmessage.9.png".equals(iconName)){
                            ninePatch = new NinePatch(bitmap,true);
                            ninePatch.loadCompiled(getTintDrawable(bitmap,color),bitmap.getNinePatchChunk());
                        } else {
                            ninePatch = new NinePatch(getTintDrawable(bitmap,color), true);
                            ninePatch.sortRegions();
                        }
                        try {
                            File file = new File(path);
                            if (file.exists()) file.delete();
                            ninePatch.saveToFile(file, NinePatch.SAVE_COMPILED);
                        } catch (IOException e) {
                            new AlertDialog.Builder(context)
                                    .setTitle("错误")
                                    .setMessage(e.toString())
                                    .setPositiveButton("确定", null)
                                    .create().show();
                            break;
                        }
                    } else {
                        Bitmap newBitmap = getTintDrawable(bitmap, color);
                        NinePatch ninePatch = new NinePatch(newBitmap, false);
                        File file = new File(path);
                        if (file.exists()) file.delete();
                        try {
                            ninePatch.saveToFile(file, NinePatch.SAVE_RAW);
                        } catch (IOException e) {
                            new AlertDialog.Builder(context)
                                    .setTitle("错误")
                                    .setMessage(e.toString())
                                    .setPositiveButton("确定", null)
                                    .create().show();
                            break;
                        }
                    }
                }
            } else {
                Toast.makeText(context,"复制文件出错，解压失败",Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            DialogUtil.showTip(context, Log.getStackTraceString(e));
        }
    }


    public static void createColorXmlFile(Context context,int color,String path,String name){
        try {
            int[] hex = new int[ThemeUtil.sColor.length];
            System.arraycopy(sColor, 0, hex, 0, sColor.length);
            hex[264] = color & 0x000000ff;
            hex[265] = (color & 0x0000ff00) >> 8;
            hex[266] = (color & 0x00ff0000) >> 16;
            hex[267] = (color & 0xff000000) >>> 24;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(ThemeUtil.sColor.length);
            for (int i : hex){
                outputStream.write(i);
            }
            createFileWithByte(outputStream.toByteArray(),path,name);
        } catch (Throwable e) {
            e.printStackTrace();
            DialogUtil.showTip(context,Log.getStackTraceString(e));
        }

    }

    private static void createFileWithByte(byte[] bytes,String path,String fileName) {
        File file = new File(path + "/color/"+fileName);
        if (file.exists())file.delete();
        FileOutputStream outputStream = null;
        try {

            file.createNewFile();
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == SUCCESS){
            if (mProgressDialog != null){
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } else if (msg.what == ERROR){
            Toast.makeText(getContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
        } else if (msg.what == ERROR_DIALOG){
            DialogUtil.showTip(getContext(),msg.obj.toString());
        } else if (msg.what == UPDATE_LIST){
            mAdapter.notifyDataSetChanged();
        }
        return true;
    }

    public static class LocalThemeInfo{
        public boolean isDefTheme;
        public String themeName;
        public String themePath;
        public String prePicPath;
        public boolean isUse;
        public boolean isCheck;

        public static String getPrePicPath(String themeDic){
            File file = new File(themeDic + "/drawable-xhdpi/qq_setting_me_bg.png");
            if (file.exists())
                return file.getAbsolutePath();
            return null;
        }
    }
}
