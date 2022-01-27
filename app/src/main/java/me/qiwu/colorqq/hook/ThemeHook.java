package me.qiwu.colorqq.hook;


import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.colorqq.XHook.XField;
import me.qiwu.colorqq.XHook.XMethod;
import me.qiwu.colorqq.XHook.XSettingUtils;
import me.qiwu.colorqq.XHook.XUtils;
import me.qiwu.colorqq.XHook.XposedCompact;
import me.qiwu.colorqq.theme.ColorFactory;
import me.qiwu.colorqq.theme.ThemeLongSparseArray;
import me.qiwu.colorqq.util.BitmapUtil;
import me.qiwu.colorqq.util.ClassUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.IdsUtil;
import me.qiwu.colorqq.util.QQHelper;
import me.qiwu.colorqq.util.StatusBarUtil;

import static me.qiwu.colorqq.util.ClassUtil.IphoneTitleBarActivity;
import static me.qiwu.colorqq.util.ClassUtil.ThemeUtil;

public class ThemeHook implements ThemeLongSparseArray.ResourcesFactory,IHook {

    private TypedValue mTmpValue = new TypedValue();
    private final Object mTmpValueLock = new Object();
    private Resources mResources;
    private String mThemePath;
    private HashMap<String,Integer> mPreLoadDrawable = new HashMap<>();
    private HashMap<String,Integer> mPreLoadColor = new HashMap<>();
    private HashMap<String,Drawable.ConstantState> mPreLoadConstantState = new HashMap<>();
    public static boolean useTransparentPanel = false;

    public static boolean isStopTheme = false;
    private static ThemeHook mInstance;

    private static Class<?> s_SkinEngine;
    private static Class<?> s_SkinData;

    private static Constructor<?> c_SkinData;
    private static Constructor<?> c_SkinSkinnableBitmapDrawable;
    private static Constructor<?> c_SkinnableNinePatchDrawable;
    private static Constructor<?> c_SkinnableColorStateList;

    private static Field f_SkinSkinnableBitmapDrawable_mTargetDensity;
    private static Field f_SkinSkinnableBitmapDrawable_mGravity;
    private static Field f_SkinnableNinePatchDrawable_mTargetDensity;
    private static Field f_SkinnableColorStateList_skinData;
    private static Field f_BaseConstantState_skinData;
    private static Field f_BaseConstantState_hasProblem;
    private static Field f_SkinData_mResourcesID;
    private static Field f_SkinData_mFileName;

    private static Method m_SkinEngine_getInstances;

    private static int mTargetDensity;

    private static final byte[] sBaseChunk = {
            1, 2, 2, 1, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 94, 0, 0, 0,
            0, 0, 0, 0, 94, 0, 0, 0, 1, 0, 0, 0
    };


    public static ThemeHook getInstances() {
        synchronized (ThemeHook.class){
            if (mInstance == null){
                mInstance = new ThemeHook();
            }
            return mInstance;
        }
    }

    public String getThemePath() {
        return mThemePath;
    }

    private void checkPermission(){
        int readPermissionCheck = QQHelper.getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean hasPermission =  readPermissionCheck == PackageManager.PERMISSION_GRANTED;
        isStopTheme = !hasPermission;
    }

    private void hookTheme(){
        if (isStopTheme)return;
        XC_MethodHook xc_methodHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Bundle bundle = (Bundle) param.getResult();
                if (bundle != null){
                    String themeId = bundle.getString("themeId");
                    if (TextUtils.isEmpty(themeId) || "1000".equals(themeId) || "999".equals(themeId)){
                        bundle.putString("themeId","qfav");
                        bundle.putString("version","config");
                    }
                }
            }
        };

        XC_MethodHook xc_methodHook1 = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String id = (String) param.getResult();
                if ("1000".equals(id) || "999".equals(id)){
                    param.setResult("qfav");
                }
            }
        };
        XMethod.create(ThemeUtil)
                .name("getCurrentThemeInfo")
                .hasNotParameterTypes()
                .returnType(Bundle.class)
                .hook(xc_methodHook);
        XMethod.create(ThemeUtil).name("getUserCurrentThemeId").hook(xc_methodHook1);

        //新的ThemeUtil
        Class<?> mThemeUtil = XUtils.findClass(ClassUtil.ThemeUtil2);
        if (mThemeUtil != null){
            for (Method method : mThemeUtil.getDeclaredMethods()){
                if (method.getReturnType() == Bundle.class && method.getParameterTypes().length == 0){
                    XposedBridge.hookMethod(method,xc_methodHook);
                }
                if (method.getReturnType() == String.class && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].getName().equals(ClassUtil.AppRuntime)){
                    XposedBridge.hookMethod(method,xc_methodHook1);
                }
            }
        }

    }

    public void setTransprentDrawable(String name){
        mPreLoadDrawable.put(name, 0);
    }

    public void setPreColorDrawable(String name,int color){
        mPreLoadDrawable.put(name,color);
    }

    public void setPreColor(String name,int color){
        mPreLoadColor.put(name,color);
    }

    public void setPreDrawableConstantState(String name,Drawable.ConstantState constantState){
        mPreLoadConstantState.put(name,constantState);
    }


    public void startHook() {
        if (!XSettingUtils.getBoolean("theme_stop")){
            checkPermission();
            hookTheme();
            hookAssets();
            hookSettingActivity();
            setBlackStatusBar();
        } else {
            isStopTheme = true;
        }

        hideSettingLine();
    }

    //隐藏设置界面项目之间的灰色线条
    private void hideSettingLine(){
        Class<?> clazz = XUtils.findClass(ClassUtil.FormItemConstants);
        if (clazz != null){
            for (Field field : clazz.getDeclaredFields()){
                if (int.class == field.getType() && Modifier.isStatic(field.getModifiers())){
                    field.setAccessible(true);
                    try {
                        if (field.getInt(null) == 0xffebedf5){
                            field.set(null,0);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void hookAssets(){
        if (isStopTheme)return;
        XMethod.create(AssetManager.class)
                .parameterTypes(String.class)
                .name("open")
                .hook(new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String fileName = (String)param.args[0];
                        if (fileName.equals("splash.png") || fileName.equals("splash.jpg") || fileName.equals("splash_logo.png") || fileName.equals("splash_big.jpg")){
                            File file = new File(mThemePath + "assets/" + fileName);
                            if (file.exists()){
                                param.setResult(new FileInputStream(file));
                            }

                        }
                    }
                });
    }

    private void setBlackStatusBar(){
        if (XSettingUtils.getBoolean("theme_black_status_bar")){
            Class<?> clazz = XUtils.findClass(ClassUtil.ImmersiveUtils);
            if (clazz != null){
                String name = "a";
                try {
                  clazz.getMethod("setStatusBarDarkMode",Window.class,boolean.class);
                } catch (Throwable throwable){
                    throwable.printStackTrace();
                }
                XMethod.create(clazz).name(name).parameterTypes(Window.class,boolean.class).hook(new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[1] = true;
                    }
                });

                String name1 = "a";
                try {
                    clazz.getMethod("setStatusTextColor",boolean.class,Window.class);
                } catch (Throwable throwable){
                    throwable.printStackTrace();
                }
                XMethod.create(clazz).name(name1).parameterTypes(boolean.class,Window.class).hook(new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[0] = true;
                    }
                });

                XMethod.create(clazz).name("adjustThemeStatusBar").parameterTypes(Window.class).hook(new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        StatusBarUtil.setStatusBarMode((Window)param.args[0],true);
                        return ((Method)param.method).getReturnType() == boolean.class ? true : null;
                    }
                });
            }


        }
    }

    private void hookSettingActivity(){
        if (isStopTheme)return;

        XMethod.create(IphoneTitleBarActivity)
                .name("setContentView")
                .hookAllMethod(new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        View view = XField.create(param.thisObject).type(View.class).name("mContentView").get();
                        if (view!=null){
                            File file = new File(mThemePath + "drawable-xhdpi/skin_setting_background.png");
                            if (file.exists()){
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                if (bitmap != null){
                                    view.setBackground(new BitmapDrawable(view.getContext().getResources(),bitmap));
                                }
                            }


                        }
                    }
                });
    }


    public boolean init() {
        try {
            Method m_getDrawable = null;

            String themePath = XSettingUtils.getString("current_theme_path",new File(FileUtil.getDefThemePath()).getAbsolutePath());
            mThemePath = themePath.endsWith("/") ? themePath : themePath + "/";
            mResources = QQHelper.getResources();
            mTargetDensity = QQHelper.getResources().getDisplayMetrics().densityDpi;
            s_SkinEngine = XposedHelpers.findClass(ClassUtil.SkinEngine,QQHelper.getContext().getClassLoader());
            if (s_SkinEngine == null) return false;

            for (Method method : s_SkinEngine.getDeclaredMethods()){
                if (s_SkinEngine == method.getReturnType() && method.getParameterTypes().length == 0){
                    m_SkinEngine_getInstances = method;
                    m_SkinEngine_getInstances.setAccessible(true);
                    break;
                }
            }
            if (m_SkinEngine_getInstances == null) return false;

            for (Method method : s_SkinEngine.getDeclaredMethods()){
                if (ColorStateList.class.isAssignableFrom(method.getReturnType())){
                    Class<?>[] classes = method.getParameterTypes();
                    if (classes.length == 3 && classes[0] == int.class && classes[1] == Resources.class){
                        Class<?> mSkinnableColorStateList = method.getReturnType();
                        c_SkinnableColorStateList = mSkinnableColorStateList.getConstructor(int[][].class,int[].class);
                        c_SkinnableColorStateList.setAccessible(true);
                        s_SkinData = classes[2];
                        c_SkinData = s_SkinData.getConstructor();
                        c_SkinData.setAccessible(true);
                        for (Field field : mSkinnableColorStateList.getDeclaredFields()){
                            if (field.getType() == s_SkinData){
                                f_SkinnableColorStateList_skinData = field;
                                f_SkinnableColorStateList_skinData.setAccessible(true);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            if (c_SkinnableColorStateList == null || c_SkinData == null || f_SkinnableColorStateList_skinData == null) return false;

            for (Method method : s_SkinEngine.getDeclaredMethods()){
                if (Drawable.ConstantState.class.isAssignableFrom(method.getReturnType()) && method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == int.class){
                    m_getDrawable = method;
                    m_getDrawable.setAccessible(true);
                    break;
                }
            }

            Object skinEngine = getSkinEngine();
            for (Field field : skinEngine.getClass().getDeclaredFields()){
                if (field.getType() == Resources.class){
                    field.setAccessible(true);
                    if (field.get(skinEngine) == null){
                        field.set(skinEngine,mResources);
                    }
                    break;
                }
            }

            Object skinNinePathObj = m_getDrawable.invoke(skinEngine, IdsUtil.skin_header_bar_bg);
            c_SkinnableNinePatchDrawable = skinNinePathObj.getClass().getDeclaredConstructor(NinePatch.class,Bitmap.class, Rect.class);
            c_SkinnableNinePatchDrawable.setAccessible(true);
            Object o_skinNinePathObj = c_SkinnableNinePatchDrawable.newInstance(null,null,null);
            for (Field field : o_skinNinePathObj.getClass().getDeclaredFields()){
                if (field.getType() == int.class){
                    field.setAccessible(true);
                    int value = (int) field.get(o_skinNinePathObj);
                    if (value == 160){
                        f_SkinnableNinePatchDrawable_mTargetDensity = field;
                        break;
                    }
                }
            }
            if (c_SkinnableNinePatchDrawable == null || f_SkinnableNinePatchDrawable_mTargetDensity == null) return false;

            Object o_skinBitmapObj = m_getDrawable.invoke(skinEngine,IdsUtil.skin_chat_background);
            c_SkinSkinnableBitmapDrawable = o_skinBitmapObj.getClass().getDeclaredConstructor(Bitmap.class);
            c_SkinSkinnableBitmapDrawable.setAccessible(true);
            Class<?> mBaseConstantState = o_skinBitmapObj.getClass().getSuperclass();
            for (Field field : mBaseConstantState.getDeclaredFields()){
                if (field.getType() == s_SkinData){
                    f_BaseConstantState_skinData = field;
                    f_BaseConstantState_skinData.setAccessible(true);
                } else if (field.getType() == boolean.class){
                    f_BaseConstantState_hasProblem = field;
                    f_BaseConstantState_hasProblem.setAccessible(true);
                }
            }
            if (c_SkinSkinnableBitmapDrawable == null || f_BaseConstantState_skinData == null || f_BaseConstantState_hasProblem == null) return false;

            Object o = c_SkinSkinnableBitmapDrawable.newInstance((Object) null);
            for (Field field : o.getClass().getDeclaredFields()){
                if (field.getType() == int.class){
                    field.setAccessible(true);
                    int value = (int)field.get(o);
                    if (value == 160){
                        f_SkinSkinnableBitmapDrawable_mTargetDensity = field;
                    } else if (value == 119){
                        f_SkinSkinnableBitmapDrawable_mGravity = field;
                    }
                }
            }
            if (f_SkinSkinnableBitmapDrawable_mTargetDensity == null || f_SkinSkinnableBitmapDrawable_mGravity == null )return false;

            Object skinData = f_BaseConstantState_skinData.get(o_skinBitmapObj);
            for (Field field : skinData.getClass().getDeclaredFields()){
                field.setAccessible(true);
                if (field.getType() == int.class){
                    int value = (int) field.get(skinData);
                    if (value == IdsUtil.skin_chat_background){
                        f_SkinData_mResourcesID = field;
                        f_SkinData_mResourcesID.setAccessible(true);
                    }
                } else if (field.getType() == String.class){
                    String value = (String) field.get(skinData);
                    if ("skin_chat_background.png".equals(value)){
                        f_SkinData_mFileName = field;
                        f_SkinData_mFileName.setAccessible(true);
                    }
                }
            }
            if (f_SkinData_mResourcesID == null || f_SkinData_mFileName == null) return false;

            for (Field field : s_SkinEngine.getDeclaredFields()){
                if (LongSparseArray.class == field.getType()){
                    field.setAccessible(true);
                    ThemeLongSparseArray themeLongSparseArray = new ThemeLongSparseArray(mResources);
                    themeLongSparseArray.setResourcesFactory(this);
                    field.set(skinEngine,themeLongSparseArray);
                }
            }
        } catch (Throwable throwable){
            XposedBridge.log("ThemeHook:  " + Log.getStackTraceString(throwable));
            return false;
        }
        return true;
    }

    private TypedValue obtainTempTypedValue() {
        TypedValue tmpValue = null;
        synchronized (mTmpValueLock) {
            if (mTmpValue != null) {
                tmpValue = mTmpValue;
                mTmpValue = null;
            }
        }
        if (tmpValue == null) {
            return new TypedValue();
        }
        return tmpValue;
    }

    private void releaseTempTypedValue(TypedValue value) {
        synchronized (mTmpValueLock) {
            if (mTmpValue == null) {
                mTmpValue = value;
            }
        }
    }

    @Override
    public Object getDrawable(int id) {
        final TypedValue value = obtainTempTypedValue();
        try {
            mResources.getValue(id, value, true);
            String path = value.string.toString();
            String fileName = path.substring(path.lastIndexOf("/") + 1);

            Bitmap bitmap = null;

            if (mPreLoadConstantState.containsKey(fileName)){
                return mPreLoadConstantState.get(fileName);
            }

            if (mPreLoadDrawable.containsKey(fileName)){
                try {
                    Bitmap temp = BitmapFactory.decodeStream(XposedCompact.getModuleContext(QQHelper.getContext()).getAssets().open("icons/ic_transparent.png"));
                    bitmap = BitmapUtil.getColorBitmap(temp,mPreLoadDrawable.get(fileName));
                    return fileName.endsWith(".9.png") ? getNinePathDrawable(new NinePatch(bitmap,makeChunk(bitmap)),bitmap,new Rect(),false,id,fileName) : getBitmapDrawable(bitmap,id,fileName);
                } catch (Throwable throwable){
                    throwable.printStackTrace();
                }

            }

            if (useTransparentPanel){
                String picName = "qvip_emoji_tab_mgr_setting_normal.png".equals(fileName) || "qvip_emoji_tab_mgr_setting_pressed.png".equals(fileName) ? "icons/ic_emoji_setting.png" : "qvip_emoji_tab_more_new_normal.png".equals(fileName) || "qvip_emoji_tab_more_new_pressed.png".equals(fileName) ? "icons/ic_fav_add.png" : null;
                if (picName != null){
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inScreenDensity = mTargetDensity;
                        options.inDensity = 320;
                        options.inTargetDensity = mTargetDensity;
                        bitmap = BitmapFactory.decodeStream(XposedCompact.getModuleContext(QQHelper.getContext()).getAssets().open(picName),new Rect(),options);
                        return getBitmapDrawable(bitmap,id,fileName);
                    } catch (OutOfMemoryError outOfMemoryError){
                        return null;
                    }
                }
            }

            if (isStopTheme)
                return null;

            //修复聊天背景压缩
            if ("chat_bg_texture.xml".equals(fileName)){
                return getChatBitmapDrawable(id,fileName);
            }
            if (!path.endsWith(".xml")) {

                File file = new File(mThemePath + "drawable-xhdpi/" + fileName);
                if (file.exists() && file.isFile() && file.canRead()){
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inScreenDensity = mTargetDensity;
                        options.inDensity = 320;
                        options.inTargetDensity = mTargetDensity;
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
                    } catch (OutOfMemoryError outOfMemoryError){
                        return null;
                    }
                    if (bitmap == null)
                        return null;
                    if (fileName.endsWith(".9.png")){
                        boolean hasProblem = false;
                        byte[] bytes = bitmap.getNinePatchChunk();
                        if (bytes == null || !NinePatch.isNinePatchChunk(bytes)){
                            bytes = makeChunk(bitmap);
                            hasProblem = true;
                        }
                        return getNinePathDrawable(new NinePatch(bitmap,bytes),bitmap,new Rect(),hasProblem,id,fileName);
                    } else {
                        return getBitmapDrawable(bitmap,id,fileName);
                    }
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            releaseTempTypedValue(value);
        }
        return null;
    }

    @Override
    public Object getColor(int id) {
        final TypedValue value = obtainTempTypedValue();
        try {
            mResources.getValue(id, value, true);
            String path = value.string.toString();
            if (path.endsWith(".xml")) {
                String fileName = path.substring(path.lastIndexOf("/") + 1);

                if (mPreLoadColor.containsKey(fileName)){
                    return getColorStateList(new int[][] { new int[0] }, new int[] { mPreLoadColor.get(fileName) },id,fileName);
                }
                if (isStopTheme)
                    return null;

                File file = new File(mThemePath + "color/" + fileName);
                if (file.exists() && file.isFile() && file.canRead()){
                    ColorFactory.ColorValue colorValue = ColorFactory.createColorFromFile(mResources,file);
                    return getColorStateList(colorValue.states,colorValue.colors,id,fileName);

                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            releaseTempTypedValue(value);
        }
        return null;
    }

    private Object getSkinEngine() throws InvocationTargetException, IllegalAccessException {
        return m_SkinEngine_getInstances.invoke(null);
    }

    private Object getNinePathDrawable(NinePatch ninePatch,Bitmap bitmap,Rect rect,boolean hasProblem,int id,String fileName) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object o = c_SkinnableNinePatchDrawable.newInstance(ninePatch,bitmap,rect);
        f_SkinnableNinePatchDrawable_mTargetDensity.set(o,mTargetDensity);
        Object skinData = c_SkinData.newInstance();
        f_SkinData_mResourcesID.set(skinData,id);
        f_SkinData_mFileName.set(skinData,fileName);
        f_BaseConstantState_skinData.set(o,skinData);
        if (hasProblem){
            f_BaseConstantState_hasProblem.set(o,true);
        }
        return o;
    }

    private Object getChatBitmapDrawable(int id,String fileName) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        File file = new File(mThemePath + "drawable-xhdpi/skin_chat_background.png");
        if (file.exists() && file.isFile() && file.canRead()){
            Bitmap bitmap = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScreenDensity = mTargetDensity;
                options.inDensity = 320;
                options.inTargetDensity = mTargetDensity;
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
            } catch (OutOfMemoryError outOfMemoryError){
                return null;
            }
            if (bitmap == null)
                return null;
            Object o = c_SkinSkinnableBitmapDrawable.newInstance(bitmap);
            f_SkinSkinnableBitmapDrawable_mTargetDensity.set(o,mTargetDensity);
            f_SkinSkinnableBitmapDrawable_mGravity.set(o, Gravity.TOP);
            Object skinData = c_SkinData.newInstance();
            f_SkinData_mResourcesID.set(skinData,id);
            f_SkinData_mFileName.set(skinData,fileName);
            f_BaseConstantState_skinData.set(o,skinData);
            return o;
        }
        return null;
    }


    private Object getBitmapDrawable(Bitmap bitmap,int id,String fileName) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object o = c_SkinSkinnableBitmapDrawable.newInstance(bitmap);
        f_SkinSkinnableBitmapDrawable_mTargetDensity.set(o,mTargetDensity);
        Object skinData = c_SkinData.newInstance();
        f_SkinData_mResourcesID.set(skinData,id);
        f_SkinData_mFileName.set(skinData,fileName);
        f_BaseConstantState_skinData.set(o,skinData);
        return o;
    }

    private Object getColorStateList(int[][] states,int[] colors,int id,String fileName) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object o =  c_SkinnableColorStateList.newInstance(states,colors);
        Object skinData = c_SkinData.newInstance();
        f_SkinData_mResourcesID.set(skinData,id);
        f_SkinData_mFileName.set(skinData,fileName);
        f_SkinnableColorStateList_skinData.set(o,skinData);
        return o;
    }

    private static byte[] makeChunk(Bitmap bitmap) {
        byte[] bArr = new byte[sBaseChunk.length];
        System.arraycopy(sBaseChunk, 0, bArr, 0, sBaseChunk.length);
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        wrap.position(36);
        wrap.putInt(bitmap.getWidth());
        wrap.position(44);
        wrap.putInt(bitmap.getHeight());
        return bArr;
    }
}
