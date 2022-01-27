package me.qiwu.colorqq.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.adapter.FabButtonSettingAdapter;
import me.qiwu.colorqq.bean.FabInfo;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.SettingUtil;
import me.qiwu.colorqq.widget.TitleBar;

public class FabButtonSettingActivity extends BaseActivity implements View.OnClickListener {

    public static final String json = "[{\"title\":\"QQ空间\",\"icon\":\"ic_qzone.png\",\"onClick\":\"qzone\",\"buttonSize\":1},{\"title\":\"扫一扫\",\"icon\":\"ic_scan.png\",\"onClick\":\"jump_com.tencent.mobileqq.qrscan.activity.ScannerActivity\",\"buttonSize\":1},{\"title\":\"加好友\",\"icon\":\"ic_person_add.png\",\"onClick\":\"jump_com.tencent.mobileqq.activity.contact.addcontact.AddContactsActivity\",\"buttonSize\":1},{\"title\":\"收付款\",\"icon\":\"ic_money.png\",\"onClick\":\"money\",\"buttonSize\":1},{\"title\":\"退出QQ\",\"icon\":\"ic_exit.png\",\"onClick\":\"exit\",\"buttonSize\":1}]";
    private static final String[] titles = {"QQ空间","QQ钱包","个性装扮","我的收藏","我的相册","我的文件","面对面快传","设置","搜索","创建群聊","加好友","扫一扫","收付款","消息","联系人","看点","动态","打开ColorQQ","退出QQ"};
    private static final String[] onClicks = {
            "qzone",
            "wallet",
            "url_http://zb.vip.qq.com",
            "fav",
            "photo",
            "jump_com.tencent.mobileqq.filemanager.activity.fileassistant.FileAssistantActivity",
            "qlink",
            "jump_com.tencent.mobileqq.activity.QQSettingSettingActivity",
            "search",
            "jump_com.tencent.mobileqq.troop.createNewTroop.NewTroopCreateActivity",
            "jump_com.tencent.mobileqq.activity.contact.addcontact.AddContactsActivity",
            "jump_jump_com.tencent.mobileqq.qrscan.activity.ScannerActivity",
            "money",
            "message",
            "contact",
            "readInjoy",
            "leba",
            "app_me.qiwu.colorqq2",
            "exit"

    };
    private static final String[] scans = {
            "jump_com.tencent.biz.qrcode.activity.ScannerActivity",
            "jump_com.tencent.mobileqq.qrscan.activity.ScannerActivity"
    };
    private String localJson = "";
    private TitleBar titleBar;
    private RecyclerView recyclerView;
    private FabButtonSettingAdapter fabButtonSettingAdapter;
    private View dialog;
    private ArrayList<FabInfo> mdata =new ArrayList<>();
    private boolean isChange = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab_button_setting);
        titleBar = findViewById(R.id.fab_title_bar);
        titleBar.setRightImage(getDrawable(R.drawable.ic_add), this);
        dialog = LayoutInflater.from(getContext()).inflate(R.layout.item_fab_button_add,null,false);
        dialog.findViewById(R.id.fab_button_pic).setOnClickListener(this);
        dialog.findViewById(R.id.fab_button_preset).setOnClickListener(this);
        loadJson();

        if (SettingUtil.getInstance().getBoolean("fabButtonSettingShowTip",true)){
            View view = findViewById(R.id.fab_botton_layout);
            view.setVisibility(View.VISIBLE);
            View exit = view.findViewById(R.id.fab_button_setting_exit);
            exit.setOnClickListener(v -> {
                view.setVisibility(View.GONE);
                SettingUtil.getInstance().putBoolean("fabButtonSettingShowTip",false);
            });
        }


        recyclerView = findViewById(R.id.fab_botton_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabButtonSettingAdapter = new FabButtonSettingAdapter(R.layout.item_fab_button_setting,mdata);
        OnItemDragListener onItemDragListener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
                if (from!=to){
                    isChange = true;
                }
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {

            }
        };
        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
                isChange = true;
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {

            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

            }
        };


        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(fabButtonSettingAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fabButtonSettingAdapter.enableDragItem(itemTouchHelper,R.id.list_dehaze,false);
        fabButtonSettingAdapter.setOnItemDragListener(onItemDragListener);
        fabButtonSettingAdapter.enableSwipeItem();
        fabButtonSettingAdapter.setOnItemSwipeListener(onItemSwipeListener);

        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_empty,null);
        fabButtonSettingAdapter.setEmptyView(view1);
        fabButtonSettingAdapter.setOnItemClickListener((adapter, view, position) -> setFabMsg(getContext(),position));

        recyclerView.setAdapter(fabButtonSettingAdapter);

    }

    private void loadJson(){
        File fabFile = new File(FileUtil.getFabPath());
        if (fabFile.exists() && fabFile.isFile()){
            fabFile.delete();
            fabFile.mkdirs();
        }
        File file = new File(FileUtil.getFabPath()+"config.json");
        if (file.exists()){
            try{
                String s = "";
                InputStream instream = new FileInputStream(file);
                if (instream != null){
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //逐行读取
                    while (( line = buffreader.readLine()) != null){
                        s = s + line;
                    }
                    instream.close();
                }
                localJson = s;
            }catch(Exception e){
                e.printStackTrace();
                localJson = json;
            }
        } else {
            localJson = json;
            try {
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                FileUtil.copyFile(new ByteArrayInputStream(json.getBytes()),file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                localJson = json;
            }

        }

        try{
            JSONArray jsonArray = new JSONArray(localJson);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FabInfo fabInfo = new FabInfo();
                fabInfo.title = jsonObject.getString("title");
                fabInfo.icon = jsonObject.getString("icon");
                fabInfo.onClick = jsonObject.getString("onClick");
                fabInfo.bottonSize = jsonObject.getInt("buttonSize");
                mdata.add(fabInfo);
            }
        }catch (Exception e){
            new AlertDialog.Builder(getContext()).setTitle("错误").setMessage(e.toString()).setPositiveButton("确定",null).create().show();
        }
    }

    public void setFabMsg(final Context context, final int position){
        View view = dialog;
        final EditText editText0 = view.findViewById(R.id.fab_button_title);
        final EditText editText1 = view.findViewById(R.id.fab_button_icName);
        final EditText editText2 = view.findViewById(R.id.fab_button_onClick);
        final EditText editText3 = view.findViewById(R.id.fab_button_buttonSize);
        editText0.setText(mdata.get(position).title);
        editText1.setText(mdata.get(position).icon);
        editText2.setText(mdata.get(position).onClick);
        editText3.setText(String.valueOf(mdata.get(position).bottonSize));
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("配置按钮")
                .setView(view)
                .setPositiveButton("确定",null)
                .setNegativeButton("取消",null)
                .setNeutralButton(R.string.fab_order,null)
                .setOnDismissListener(dialog1 -> {
                    if (dialog.getParent()!=null){
                        ((ViewGroup)(dialog).getParent()).removeView(dialog);
                    }

                })
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText0.getText().toString().equals("")||editText1.getText().toString().equals("")||editText2.getText().toString().equals("")||editText3.getText().toString().equals("")){
                    Toast.makeText(v.getContext(),"请检查数据是否完整",Toast.LENGTH_SHORT).show();
                } else {
                    FabInfo fabInfo = new FabInfo();
                    fabInfo.title = editText0.getText().toString();
                    fabInfo.icon = editText1.getText().toString();
                    fabInfo.onClick = editText2.getText().toString();
                    fabInfo.bottonSize = Integer.valueOf(editText3.getText().toString());
                    mdata.remove(position);
                    mdata.add(position,fabInfo);
                    isChange = true;
                    fabButtonSettingAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(),ItemOrderActivity.class);
            intent.putExtra("file","fab_introduce.html");
            startActivity(intent);
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        if (isChange){
            new AlertDialog.Builder(getContext())
                    .setTitle("提示")
                    .setMessage("数据已改变，是否保存")
                    .setPositiveButton("保存", (dialog, which) -> {
                        FileUtil.copyFile(new ByteArrayInputStream(getJson().getBytes()),FileUtil.getFabPath()+"config.json");
                        finish();
                    })
                    .setNegativeButton("取消", (dialog, which) -> finish())
                    .create().show();
        } else {
            super.onBackPressed();
        }
    }

    public void save(View v){
        Toast.makeText(getContext(),FileUtil.copyFile(new ByteArrayInputStream(getJson().getBytes()),FileUtil.getFabPath()+"config.json") ? "保存成功" : "保存失败",Toast.LENGTH_SHORT).show();
        isChange = false;
    }

    private String getJson(){
        List<FabInfo> data = mdata;
        JSONArray jsonArray = new JSONArray();
        for (int i=0;i<data.size();i++){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title",data.get(i).title);
                jsonObject.put("icon",data.get(i).icon);
                jsonObject.put("onClick",data.get(i).onClick);
                jsonObject.put("buttonSize",data.get(i).bottonSize);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                new AlertDialog.Builder(getContext()).setTitle("错误").setMessage(e.toString()).setPositiveButton("确定",null).create().show();
            }
        }
        return jsonArray.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            isChange = true;
            ((EditText)dialog.findViewById(R.id.fab_button_icName)).setText(data.getStringExtra("pic"));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.fab_button_pic){
            Intent intent = new Intent(getContext(),SvgPicSelectActivity.class);
            intent.putExtra("path",FileUtil.getFabPath());
            startActivityForResult(intent,999);
        } else if (v.getId()==R.id.fab_button_preset){
            new AlertDialog.Builder(getContext())
                    .setTitle("预置功能")
                    .setItems(titles, (dialog, which) -> {
                        if (which == 11){
                             new AlertDialog.Builder(getContext())
                                     .setTitle("扫一扫")
                                     .setItems(new String[]{"QQ860以前","QQ860及之后"}, (dialog1, which1) -> {
                                         isChange = true;
                                         ((EditText)FabButtonSettingActivity.this.dialog.findViewById(R.id.fab_button_onClick)).setText(scans[which1]);
                                     }).create().show();
                        } else {
                            isChange = true;
                            ((EditText)this.dialog.findViewById(R.id.fab_button_onClick)).setText(onClicks[which]);
                        }
                    })
                    .setPositiveButton("取消",null)
                    .create().show();
        }else {
            resetDialog();
            final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle("创建按钮")
                    .setView(dialog)
                    .setPositiveButton(R.string.ok, null)
                    .setNegativeButton(R.string.cancel,null)
                    .setNeutralButton(R.string.fab_order,null)
                    .setOnDismissListener(dialog1 -> {
                        if (dialog.getParent()!=null){
                            ((ViewGroup)(dialog).getParent()).removeView(dialog);
                            dialog.setVisibility(View.VISIBLE);
                        }
                    })
                    .create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v13 -> {
                View view1 = dialog;
                final EditText editText0 = view1.findViewById(R.id.fab_button_title);
                final EditText editText1 = view1.findViewById(R.id.fab_button_icName);
                final EditText editText2 = view1.findViewById(R.id.fab_button_onClick);
                final EditText editText3 = view1.findViewById(R.id.fab_button_buttonSize);
                if (editText0.getText().toString().equals("")||editText1.getText().toString().equals("")||editText2.getText().toString().equals("")||editText3.getText().toString().equals("")){
                    Toast.makeText(v13.getContext(),"请检查数据是否完整",Toast.LENGTH_SHORT).show();
                } else {
                    FabInfo fabInfo1 = new FabInfo();
                    fabInfo1.title = editText0.getText().toString();
                    fabInfo1.icon = editText1.getText().toString();
                    fabInfo1.onClick = editText2.getText().toString();
                    fabInfo1.bottonSize = Integer.valueOf(editText3.getText().toString());
                    mdata.add(fabInfo1);
                    isChange = true;
                    fabButtonSettingAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            });
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v1 -> {
                Intent intent = new Intent(getContext(),ItemOrderActivity.class);
                intent.putExtra("file","fab_introduce.html");
                startActivity(intent);

            });
        }

    }


    private void resetDialog(){
        View view = dialog;
        final EditText editText0 = view.findViewById(R.id.fab_button_title);
        final EditText editText1 = view.findViewById(R.id.fab_button_icName);
        final EditText editText2 = view.findViewById(R.id.fab_button_onClick);
        final EditText editText3 = view.findViewById(R.id.fab_button_buttonSize);
        editText0.setText("");
        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
    }

}
