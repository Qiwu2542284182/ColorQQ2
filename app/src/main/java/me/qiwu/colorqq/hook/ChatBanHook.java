package me.qiwu.colorqq.hook;

import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import me.qiwu.colorqq.XHook.XMethod;
import me.qiwu.colorqq.XHook.XUtils;
import me.qiwu.colorqq.util.ClassUtil;
import me.qiwu.colorqq.XHook.XSettingUtils;

import static me.qiwu.colorqq.XHook.XposedCompact.classLoader;


public class ChatBanHook implements IHook {
    private static ChatBanHook sInstance;
    public static ChatBanHook getInstance(){
        synchronized (ChatBanHook.class){
            if (sInstance == null){
                sInstance = new ChatBanHook();
            }
            return sInstance;
        }
    }

    public void hook() {
        XMethod.create(ClassUtil.MessageRecord)
                .enable(XSettingUtils.getBoolean("theme_default_bubble")||XSettingUtils.getBoolean("start_diy_bubble"))
                .name("needVipBubble")
                .replace(false);

        XMethod.create(ClassUtil.MessageRecord)
                .name("getExtInfoFromExtStr")
                .parameterTypes(String.class)
                .enable("theme_default_font")
                .hook(new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String s = (String) param.args[0];
                        if (TextUtils.isEmpty(s) || "vip_font_id".equals(s) || "vip_font_effect_id".equals(s)){
                            param.setResult("");
                        }
                    }
                });

        XMethod.create(ClassUtil.ETTextView)
                .enable("theme_default_font")
                .name("setFont")
                .replaceAllNull();

        XMethod.create(ClassUtil.PendantInfo)
                .enable("theme_default_pendant")
                .parameterTypes(View.class,int.class,long.class,String.class,int.class)
                .name("a")
                .replaceNull();

    }


    @Override
    public void startHook() {
        hook();
    }

    @Override
    public boolean init() {
        return true;
    }
}
