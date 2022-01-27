package me.qiwu.colorqq.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.adapter.SvgSelectAdapter;
import me.qiwu.colorqq.library.sharp.Sharp;
import me.qiwu.colorqq.util.DialogUtil;
import me.qiwu.colorqq.util.FileUtil;

public class SvgPicSelectActivity extends BaseActivity implements AdapterView.OnItemClickListener, ColorPickerDialogListener,SearchView.OnQueryTextListener {
    private SvgSelectAdapter svgSelectAdapter;
    private TextView dp;
    private TextView picName;
    private ImageView colorImage;
    private int color = Color.WHITE;
    private int size = 72;
    private String path;
    private String precutName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = getIntent().getStringExtra("path");
        precutName = getIntent().getStringExtra("name");
        setContentView(R.layout.activity_svg_select);
        SearchView searchView = findViewById(R.id.svg_search);
        searchView.onActionViewExpanded();
        searchView.setQueryHint("搜索图标");
        searchView.setOnQueryTextListener(this);
        View view = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (view!=null){
            view.clearFocus();
        }
        searchView.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        GridView gridView = findViewById(R.id.svg_gridView);
        gridView.setOnItemClickListener(this);
        try {
            List<Picture>pictures = new ArrayList<>();
            File file = new File(getExternalCacheDir(),"icons.zip");
            if (!file.exists()){
                InputStream inputStream = getAssets().open("icons.zip");
                FileUtil.copyFile(inputStream,file.getAbsolutePath());
            }
            List<String> titles = new ArrayList<>();
            ZipFile zipFile = new ZipFile(file);
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
            ZipEntry ze;
            while ((ze = zipInputStream.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    //Do nothing
                } else {
                    titles.add(ze.getName().replace(".svg",""));
                    pictures.add(Sharp.loadInputStream(zipFile.getInputStream(ze)).getSharpPicture().getPicture());
                }
            }
            zipInputStream.closeEntry();
            zipInputStream.close();

            svgSelectAdapter = new SvgSelectAdapter(getContext(),pictures,titles);
            gridView.setAdapter(svgSelectAdapter);
        } catch (IOException e) {
            DialogUtil.showTip(getContext(),e.toString());
        }

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_svg_select,null);
        dp = view1.findViewById(R.id.svg_dp_text);
        colorImage = view1.findViewById(R.id.svg_color);
        reloadColorImage();
        picName = view1.findViewById(R.id.svg_pic_text);
        picName.setText(TextUtils.isEmpty(precutName) ? "ic_"+svgSelectAdapter.getPicName(position)+".png" : precutName);
        new AlertDialog.Builder(getContext())
                .setTitle("保存图标")
                .setView(view1)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (saveTo(svgSelectAdapter.getPic(position),size,color,path+picName.getText().toString())){
                            Toast.makeText(getContext(),"图标已保存",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("pic",picName.getText().toString());
                            setResult(999,intent);
                            finish();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    public void setSvgDp(View view){
        DialogUtil.showEditViewDialog(getContext(), "尺寸", "72", true, editText -> {
            try {
                size = Integer.valueOf(editText.getText().toString());
                dp.setText(size + "x" + size);
            } catch (Exception e){
                Toast.makeText(getContext(),"只能输入数字",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reloadColorImage(){
        GradientDrawable gradientDrawable = (GradientDrawable)colorImage.getBackground();
        gradientDrawable.setColor(color);
        gradientDrawable.invalidateSelf();
        colorImage.invalidate();
    }

    public void setSvgColor(View view){
        DialogUtil.showColorDialog(getSupportFragmentManager(),false,color,this);
    }

    public void setSvgName(View view){
        DialogUtil.showEditViewDialog(getContext(), "图片名称", picName.getText().toString(), false, editText -> picName.setText(editText.getText()));
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        this.color = color;
        reloadColorImage();
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private boolean saveTo(Picture picture,int size,int color,String savePath){
        Bitmap bitmap = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
        new Canvas(bitmap).drawPicture(picture,new Rect(0,0,size,size));
        Bitmap bitmap1 = createTintedBitmap(bitmap,color);
        File file = new File(savePath);
        if (file.exists()){
            file.delete();
        }
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            if (bitmap1.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream)){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Bitmap createTintedBitmap(Bitmap bitmap, int tintColor) {
        Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawColor(tintColor);
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        tempCanvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        Bitmap resultBitmap = Bitmap.createBitmap(tempBitmap.getWidth(), tempBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas resultCanvas = new Canvas(resultBitmap);
        resultCanvas.drawColor(tintColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        resultCanvas.drawBitmap(tempBitmap, 0.0f, 0.0f, paint);
        tempBitmap.recycle();
        return resultBitmap;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        svgSelectAdapter.search(newText);
        return false;
    }
}
