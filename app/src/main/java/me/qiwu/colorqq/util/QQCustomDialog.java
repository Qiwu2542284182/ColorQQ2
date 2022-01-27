package me.qiwu.colorqq.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;
import me.qiwu.colorqq.XHook.XUtils;

public class QQCustomDialog {
    private static Class<?> sQQCustomDialog;
    private static Method mSetTitle;
    private static Method mSetMessage;
    private static Method mSetPositiveButton;
    private static Method mSetNegativeButton;
    private static Method mSetItems;

    private Dialog dialog;
    public QQCustomDialog(Context context){
        try {
            ClassLoader classLoader = context.getClassLoader();
            if (sQQCustomDialog == null){
                sQQCustomDialog = XUtils.findClass("com.tencent.mobileqq.utils.QQCustomDialog");
            }
            if (sQQCustomDialog == null){
                Class<?> clazz = classLoader.loadClass(ClassUtil.LiteActivity);
                for (Field field : clazz.getDeclaredFields()){
                    Class<?> type = field.getType();
                    Class<?> superType = type.getSuperclass();
                    if (superType == null || superType == Object.class)
                        continue;
                    if (superType == Dialog.class || superType.getSuperclass() == Dialog.class){
                        try {
                            Method method = type.getDeclaredMethod("setTitle",String.class);
                            sQQCustomDialog = type;
                            mSetTitle = method;
                            break;
                        } catch (Throwable throwable){

                        }
                    }
                }
            }
            if (mSetTitle == null)
                mSetTitle = sQQCustomDialog.getDeclaredMethod("setTitle",String.class);
            if (mSetMessage == null)
                mSetMessage = sQQCustomDialog.getDeclaredMethod("setMessage",CharSequence.class);
            if (mSetPositiveButton == null)
                mSetPositiveButton = sQQCustomDialog.getDeclaredMethod("setPositiveButton",String.class, DialogInterface.OnClickListener.class);
            if (mSetNegativeButton == null)
                mSetNegativeButton = sQQCustomDialog.getDeclaredMethod("setNegativeButton",String.class, DialogInterface.OnClickListener.class);
            if (mSetItems == null)
                mSetItems = sQQCustomDialog.getDeclaredMethod("setItems",String[].class, DialogInterface.OnClickListener.class);
            QQInputDialog qqInputDialog = new QQInputDialog(context).init(null,null,android.R.string.ok,android.R.string.cancel,null,null);
            EditText editText = qqInputDialog.getEditText();
            ((ViewGroup)editText.getParent()).removeView(editText);
            dialog = qqInputDialog.getDialog();
        } catch (Throwable e){
            e.printStackTrace();
        }

    }

    public QQCustomDialog setTitle(String title){
        try {
            mSetTitle.invoke(dialog,title);
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
        return this;
    }

    public QQCustomDialog setMessage(String msg){
        try {
            mSetMessage.invoke(dialog,msg);
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
        return this;
    }

    public QQCustomDialog setPositionButton(String name,DialogInterface.OnClickListener onClickListener){
        try {
            mSetPositiveButton.invoke(dialog,name,onClickListener);
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
        return this;
    }

    public QQCustomDialog setNegativeButton(String name,DialogInterface.OnClickListener onClickListener){
        try {
            mSetNegativeButton.invoke(dialog,name,onClickListener);
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
        return this;
    }

    public QQCustomDialog setCanceledOnTouchOutside(boolean z){
        dialog.setCanceledOnTouchOutside(z);
        return this;
    }

    public QQCustomDialog setItems(String[] names,DialogInterface.OnClickListener onClickListener){
        try {
            mSetItems.invoke(dialog,names,onClickListener);
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
        return this;
    }

    public void show(){
        dialog.show();
    }

}
