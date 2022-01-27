package me.qiwu.colorqq.hook;

import android.widget.EditText;

import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import me.qiwu.colorqq.XHook.XSettingUtils;
import me.qiwu.colorqq.XHook.XUtils;
import me.qiwu.colorqq.util.ClassUtil;

public class QQEditViewHook implements IHook {
    private static QQEditViewHook sInstance;
    private static Class<?> mXEditTextEx;
    public static QQEditViewHook getInstance(){
        synchronized (QQEditViewHook.class){
            if (sInstance == null){
                sInstance = new QQEditViewHook();
            }
            return sInstance;
        }
    }



    @Override
    public void startHook() {
        if (XSettingUtils.getBoolean("chat_edit_start")){
            XposedBridge.hookAllConstructors(mXEditTextEx, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    EditText editText = (EditText) param.thisObject;
                    editText.setHint(XSettingUtils.getString("chat_hint","这是一个提示语"));
                    editText.setHintTextColor(XSettingUtils.getInt("chat_hint_color",0x66666666));
                }
            });
        }

    }

    @Override
    public boolean init() {
        mXEditTextEx = XUtils.findClass(ClassUtil.XEditTextEx);
        return mXEditTextEx != null;
    }
}
