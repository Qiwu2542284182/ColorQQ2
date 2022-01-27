package me.qiwu.colorqq.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.method.DigitsKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import me.qiwu.colorqq.R;
import me.qiwu.colorqq.widget.TextItem;

public class DialogUtil {

    public static void showTip(Context context,String msg){
        showTip(context, msg,null);
    }

    /*
    显示简单的文本dialog弹窗
    msg：提示的文本
    onClickListener：点击后的事件
     */
    public static void showTip(Context context, String msg, DialogInterface.OnClickListener onClickListener){
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(msg)
                .setPositiveButton("确定",onClickListener)
                .setCancelable(onClickListener==null)
                .create()
                .show();
    }

    /*
    显示一个选择颜色的dialog
    isCreateTheme：是否为生成主题时的弹窗
    colorPickerDialogListener：选择颜色后的事件
     */
    public static void showColorDialog(FragmentManager fragmentManager, boolean isCreateTheme,boolean showAlpha, int defaultColor, ColorPickerDialogListener colorPickerDialogListener){
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.newBuilder()
                .setDialogTitle(isCreateTheme ? R.string.select_theme_color : R.string.select_color)
                .setSelectedButtonText(R.string.ok)
                .setCustomButtonText(R.string.diy_color)
                .setPresetsButtonText(R.string.preset)
                .setShowAlphaSlider(showAlpha)
                .setColor(defaultColor)
                .create();
        colorPickerDialog.setColorPickerDialogListener(colorPickerDialogListener);
        colorPickerDialog.show(fragmentManager,"color");
    }

    public static void showColorDialog(FragmentManager fragmentManager, boolean isCreateTheme, int defaultColor, ColorPickerDialogListener colorPickerDialogListener){
        showColorDialog(fragmentManager, isCreateTheme,!isCreateTheme, defaultColor, colorPickerDialogListener);
    }

    public static void showColorDialog(FragmentManager fragmentManager, ColorPickerDialogListener colorPickerDialogListener){
        showColorDialog(fragmentManager,false,colorPickerDialogListener);
    }

    public static void showColorDialog(FragmentManager fragmentManager, boolean isCreateTheme,  ColorPickerDialogListener colorPickerDialogListener){
        showColorDialog(fragmentManager, isCreateTheme, Color.BLACK, colorPickerDialogListener);
    }

    /*
    显示一个带输入框的弹窗
    title：弹窗的标题
    key：从sp获取文本的key，获取到文本后显示在输入框上，点击确定保存到sp
     */
    public static void showEditViewDialog(final Context context, String title, final String key, final TextItem textItem){
        LinearLayout linearLayout = getEditView(context);
        final EditText editText = (EditText) linearLayout.getChildAt(0);
        String text = SettingUtil.getInstance().getString(key,textItem != null ? textItem.getDefaultValue() : "");
        editText.setText(text);
        editText.setSelection(text.length());
        if (textItem!=null&&textItem.hasUnit()){
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        }
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(linearLayout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if (textItem!=null&&textItem.hasUnit()){
                            try {
                                Integer.valueOf(text);
                            } catch (Exception e){
                                Toast.makeText(context,"只能输入数字",Toast.LENGTH_SHORT).show();
                                return;
                            }

                        }
                        SettingUtil.getInstance().putString(key,text);
                        if (textItem!=null){
                            textItem.setRightText(text);
                        }

                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        InputMethodManager mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .create()
                .show();

    }

    public static void showEditViewDialog(final Context context, String title, String defValue, final boolean isLimitNum, final OnEdittextClick onEdittextClick){
        LinearLayout linearLayout = getEditView(context);
        final EditText editText = (EditText) linearLayout.getChildAt(0);
        editText.setText(defValue);
        editText.setSelection(defValue.length());
        if (isLimitNum){
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        }
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(linearLayout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onEdittextClick!=null){
                            onEdittextClick.onClick(editText);
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .create()
                .show();

    }

    private static LinearLayout getEditView(Context context){
        EditText editText = new EditText(context);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(DensityUtil.dip2px(context,20.0f),DensityUtil.dip2px(context,10.0f),DensityUtil.dip2px(context,20.0f),0);
        editText.setLayoutParams(layoutParams);
        linearLayout.addView(editText);
        return linearLayout;
    }

    public interface OnEdittextClick{
        void onClick(EditText editText);
    }
}
