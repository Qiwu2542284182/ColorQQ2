package me.qiwu.colorqq.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.qiwu.colorqq.XHook.XUtils;

public class QQInputDialog {
    private static Class<?> sInputDialog;
    private static Method mGetDialog;
    private static Method mGetEditView;
    private Dialog dialog;
    private Context context;
    public QQInputDialog(Context context){
        try {
            ClassLoader classLoader = context.getClassLoader();
            this.context = context;
            if (sInputDialog == null){
                sInputDialog = XUtils.findClass("com.tencent.biz.widgets.InputDialog");
            }
            if (sInputDialog == null){
                Class<?> clazz = classLoader.loadClass("com.tencent.mobileqq.activity.ForwardFriendListActivity");
                for (Field field : clazz.getDeclaredFields()){
                    Class<?> type = field.getType();
                    if (type == Dialog.class)
                        continue;
                    if (Dialog.class.isAssignableFrom(type)){
                        try {
                            mGetEditView = type.getDeclaredMethod("getEditText");
                            mGetDialog = type.getDeclaredMethod("a",Context.class,String.class,String.class,int.class,int.class, DialogInterface.OnClickListener.class,DialogInterface.OnClickListener.class);
                            sInputDialog = type;
                            break;
                        } catch (Throwable throwable){

                        }

                    }
                }
            }

        } catch (Throwable throwable){
            throw new NullPointerException("构造弹窗失败：" + throwable.getMessage());
        }

    }

    public QQInputDialog init(String title,String message,int cancel,int ok,DialogInterface.OnClickListener onClickListener,DialogInterface.OnClickListener onClickListener1){
        try {
            dialog = (Dialog) mGetDialog.invoke(null,context,title,message,cancel,ok,onClickListener,onClickListener1);
        } catch (Throwable throwable){
            throw new NullPointerException("弹窗初始化失败");
        }
        return this;
    }

    public EditText getEditText(){
        try {
            return (EditText) mGetEditView.invoke(dialog);
        } catch (Throwable throwable){
            throw new NullPointerException("获取输入框失败");
        }
    }

    public Dialog getDialog(){
        return dialog;
    }

    public void show(){
        if (dialog != null)
            dialog.show();
    }

    public void dismiss(){
        if (dialog != null)
            dialog.dismiss();
    }
}
