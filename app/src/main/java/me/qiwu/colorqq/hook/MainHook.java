package me.qiwu.colorqq.hook;


import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.qiwu.colorqq.XHook.XMethod;
import me.qiwu.colorqq.XHook.XSettingUtils;
import me.qiwu.colorqq.XHook.XposedCompact;
import me.qiwu.colorqq.util.ClassUtil;
import me.qiwu.colorqq.util.QQHelper;


public class MainHook implements IXposedHookLoadPackage {
    private static boolean isHookQQ = false;



    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if ("com.tencent.mobileqq".equals(loadPackageParam.packageName)) {
            XMethod.create(XposedHelpers.findClass(ClassUtil.LoadDex, loadPackageParam.classLoader))
                    .name("doStep")
                    .hasNotParameterTypes()
                    .returnType(boolean.class)
                    .hook(new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            if (!XSettingUtils.hasSettingFile()){
                                Toast.makeText(QQHelper.getContext(),"ColorQQ配置文件不存在",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (XSettingUtils.getBoolean("module_stophook"))
                                return;
                            if (!isHookQQ) {
                                isHookQQ = true;
                                XposedCompact.classLoader = QQHelper.getContext().getClassLoader();
                                XposedCompact.processName = loadPackageParam.processName;
                                QQHelper.init();

                                ThemeHook themeHook = ThemeHook.getInstances();
                                if (themeHook.init()){
                                    themeHook.startHook();
                                }
                                if (loadPackageParam.processName.contains(":")){
                                    return;
                                }
                                IHook[] iHooks = {
                                        ChatBanHook.getInstance(),
                                        MainUIHook.getInstance(),
                                        QQEditViewHook.getInstance()
                                };
                                for (IHook fixHook : iHooks){
                                    if (fixHook.init()){
                                        fixHook.startHook();
                                    }
                                }


                            }

                        }
                    });

        }
    }

}
