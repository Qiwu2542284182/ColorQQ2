package me.qiwu.colorqq.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.adapter.FileSelectAdapter;
import me.qiwu.colorqq.util.SettingUtil;

public class FileSelectActivity extends BaseActivity {
    private TextView mPathText;
    private ListView mListView;
    private String mExternalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String mCurrentPath;
    private FileSelectAdapter mFileSelectAdapter;
    private List<FileInfo> mFileInfos = new ArrayList<>();
    private String mSuffix;
    private Map<String,Integer> mPositions = new HashMap<>();
    public final static int SELECT_FILE_CODE = 999;
    public final static String ZIP = ".zip";
    public final static String THEME = ".theme";
    public final static String XML = ".xml";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        mSuffix = getIntent().getStringExtra("filter");
        mListView = findViewById(R.id.file_list);
        mPathText = findViewById(R.id.file_path);
        mCurrentPath = SettingUtil.getInstance().getString("file_last_path",mExternalPath);
        loadCurrentPath();
        mFileSelectAdapter = new FileSelectAdapter(mFileInfos,getContext());
        mListView.setAdapter(mFileSelectAdapter);
        mListView.setBackgroundColor(Color.WHITE);
        mListView.setDividerHeight(0);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            FileInfo fileInfo = mFileInfos.get(position);
            if (fileInfo.isFile){
                String path = mCurrentPath + "/" + fileInfo.fileName;
                new AlertDialog.Builder(getContext())
                        .setTitle("选择")
                        .setMessage(path)
                        .setPositiveButton("确定", (dialog, which) -> {
                            Intent intent = new Intent();
                            intent.putExtra("path",path);
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        })
                        .setNegativeButton("取消",null)
                        .create()
                        .show();
            } else {
                mPositions.put(mCurrentPath,mListView.getFirstVisiblePosition());
                mCurrentPath = new File(new File(mCurrentPath),fileInfo.fileName).getAbsolutePath();
                loadCurrentPath();
                mFileSelectAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    private void loadCurrentPath(){
        mFileInfos.clear();
        String[] paths = new File(mCurrentPath).list((dir, name) -> TextUtils.isEmpty(mSuffix) || (new File(dir,name).isDirectory() || name.endsWith(mSuffix)));
        if (paths!=null&&paths.length!=0){
            for (String path : paths){
                File file = new File(new File(mCurrentPath),path);
                FileInfo fileInfo = new FileInfo();
                fileInfo.isFile = file.isFile();
                fileInfo.fileName = path;
                fileInfo.time = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(file.lastModified()));
                mFileInfos.add(fileInfo);
            }
        }
        Collections.sort(mFileInfos, (o1, o2) -> {
            if ((!o1.isFile) && o2.isFile)
                return -1;
            if (o1.isFile && !o2.isFile)
                return 1;
            return o1.fileName.toLowerCase().compareTo(o2.fileName.toLowerCase());
        });
        mPathText.setText(mCurrentPath);
        SettingUtil.getInstance().putString("file_last_path",mCurrentPath);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentPath.equals(mExternalPath)){
            super.onBackPressed();
        } else {
            mPositions.remove(mCurrentPath);
            mCurrentPath = new File(mCurrentPath).getParent();
            loadCurrentPath();
            mFileSelectAdapter.notifyDataSetChanged();
            if (mPositions.containsKey(mCurrentPath)){
                mListView.setSelection(mPositions.get(mCurrentPath));
            }
        }


    }

    public static void start(Fragment fragment, String type){
        Intent intent = new Intent(fragment.getContext(),FileSelectActivity.class);
        intent.putExtra("filter",type);
        fragment.startActivityForResult(intent,SELECT_FILE_CODE);
    }

    public static void start(Activity activity, String type){
        Intent intent = new Intent(activity,FileSelectActivity.class);
        intent.putExtra("filter",type);
        activity.startActivityForResult(intent,SELECT_FILE_CODE);
    }

    public class FileInfo{
        public boolean isFile;
        public String fileName;
        public String time;
    }

}
