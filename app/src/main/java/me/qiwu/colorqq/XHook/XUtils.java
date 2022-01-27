package me.qiwu.colorqq.XHook;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XUtils {
    public static Class<?> findClass(String clazz){
        try {
            return XposedHelpers.findClass(clazz,XposedCompact.classLoader);
        } catch (Throwable throwable){
            return null;
        }
    }

    public static Object findFirstFieldByExactType(Object clazz,String type){
        try {

            return XposedHelpers.findFirstFieldByExactType(clazz.getClass(),XUtils.findClass(type)).get(clazz);
        } catch (Throwable throwable){
            return null;
        }
    }

    public static Object invokeOriginalMethod(XC_MethodHook.MethodHookParam param) throws InvocationTargetException, IllegalAccessException {
        return XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
    }


    public static Method findMethodIfExists(Class<?> clazz, Class<?> returnType, String methodName, Class<?>... parameterTypes) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (returnType != null && returnType != method.getReturnType())
                continue;


            if (!TextUtils.isEmpty(methodName) && !method.getName().equals(methodName)){
                continue;
            }

            if (parameterTypes == null){
                method.setAccessible(true);
                return method;
            }

            Class<?>[] methodParameterTypes = method.getParameterTypes();
            if (parameterTypes.length != methodParameterTypes.length)
                continue;

            boolean match = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == null) continue;
                if (parameterTypes[i] != methodParameterTypes[i]) {
                    match = false;
                    break;
                }
            }
            if (match){
                method.setAccessible(true);
                return method;
            }

        }
        return null;
    }

    public static Constructor findConstructorIfExists(Class<?> clazz, Class<?>... parameterTypes) {
        if (parameterTypes == null || parameterTypes.length == 0){
            try {
                return clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }
        for (Constructor constructor : clazz.getConstructors()) {

            Class<?>[] methodParameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length != methodParameterTypes.length)
                continue;

            boolean match = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == null) continue;
                if (parameterTypes[i] != methodParameterTypes[i]) {
                    match = false;
                    break;
                }
            }
            if (match){
                constructor.setAccessible(true);
                return constructor;
            }

        }
        return null;
    }

    public static Context getContext(){
        return AndroidAppHelper.currentApplication().getApplicationContext();
    }

    public static Field findField(Class<?> clazz, Class<?> type, String name) {
        if (clazz != null && !name.isEmpty()) {
            Class<?> clz = clazz;
            do {
                for (Field field : clz.getDeclaredFields()) {
                    if ((type == null || field.getType() == type) && field.getName()
                            .equals(name)) {
                        field.setAccessible(true);
                        return field;
                    }
                }
            } while ((clz = clz.getSuperclass()) != null);
        }
        return null;
    }

    static Field findFieldWithoutName(Class<?> clazz, Class<?> type) {
        if (clazz != null && type != null) {
            Class<?> clz = clazz;
            do {
                for (Field field : clz.getDeclaredFields()) {
                    if (field.getType() == type) {
                        field.setAccessible(true);
                        return field;
                    }
                }
            } while ((clz = clz.getSuperclass()) != null);
        }
        return null;
    }

    static String getParametersString(Class<?>... clazzes) {
        if (clazzes == null) return "(null)";
        StringBuilder sb = new StringBuilder("(");
        boolean first = true;
        for (Class<?> clazz : clazzes) {
            if (first)
                first = false;
            else
                sb.append(",");

            if (clazz != null)
                sb.append(clazz.getCanonicalName());
            else
                sb.append("null");
        }
        sb.append(")");
        return sb.toString();
    }
}
