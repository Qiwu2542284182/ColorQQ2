package me.qiwu.colorqq.XHook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XMethod {

    private static final HashMap<String, Method> sMethodCache = new HashMap<String, Method>();
    private Object mBase;
    private Class<?> mClazz;
    private Class<?> mReturnType;
    private String mMethodName;
    private Class<?>[] mParameterTypes;
    private boolean isEnable = true;
    private boolean hasParameterTypes = true;
    private XMethod(Object base){
        if (base != null){
            if (base instanceof Class){
                mClazz = (Class) base;
            } else {
                mClazz = base.getClass();
                mBase = base;
            }
        }

    }


    public static XMethod create(Object base){
        return new XMethod(base);

    }

    public static XMethod create(String clazz){
        return new XMethod(XUtils.findClass(clazz));
    }

    public XMethod enable(boolean isEnable){
        this.isEnable = isEnable;
        return this;
    }

    public XMethod enable(String key){
        this.isEnable = XSettingUtils.getBoolean(key);
        return this;
    }

    public XMethod returnType(Class<?> returnType){
        this.mReturnType = returnType;
        return this;
    }

    public XMethod returnType(String returnType){
        this.mReturnType = XUtils.findClass(returnType);
        return this;
    }

    public XMethod name(String name){
        this.mMethodName = name;
        return this;
    }

    public XMethod hasNotParameterTypes(){
        this.hasParameterTypes = false;
        return this;
    }

    public XMethod parameterTypes(Object... parameterTypes){
        hasParameterTypes = true;
        mParameterTypes = new Class[parameterTypes.length];
        for (int i=0;i<parameterTypes.length;i++){
            Object o = parameterTypes[i];
            if (o == null){
                mParameterTypes[i] = null;
            } else if (o instanceof String){
                mParameterTypes[i] = XUtils.findClass((String)o);
            } else if (o instanceof Class){
                mParameterTypes[i] = (Class) o;
            }
        }
        return this;
    }

    public Method get(){
        if (mClazz == null)
            return null;
        String fullMethodName = mClazz.getName() + '#' + mMethodName + XUtils.getParametersString(mParameterTypes) + "#return(" + (mReturnType == null ? "null" : mReturnType.getName()) + ")" ;
        Method method = null;
        if (sMethodCache.containsKey(fullMethodName)) {
            method = sMethodCache.get(fullMethodName);
        }
        if (method == null){
            method =  hasParameterTypes ? XUtils.findMethodIfExists(mClazz,mReturnType,mMethodName,mParameterTypes) : XUtils.findMethodIfExists(mClazz,mReturnType,mMethodName);
        }
        if (method != null){
            sMethodCache.put(fullMethodName,method);
            //XposedBridge.log("qiwu getMethod success,name :" + fullMethodName);
        } else {
            //XposedBridge.log("qiwu getMethod fail ,name :" + fullMethodName);
        }
        return method;
    }

    public void hook(XC_MethodHook xcMethodHook){
        if (isEnable){
             Method method = get();
             if (method!=null){
                 XposedBridge.hookMethod(method,xcMethodHook);
             }
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T call(Object object,Object...args){
        Method method = get();
        if (method != null){
            try {
                return (T) method.invoke(object, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T call(){
        if (mBase != null){
            Method method = get();
            if (method != null){
                try {
                    return (T) method.invoke(mBase);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T callStatic(Object...args){
        Method method = get();
        if (method != null){
            try {
                return (T) method.invoke(null,args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    public void replace(Object o){
        hook(XC_MethodReplacement.returnConstant(o));
    }

    public void replaceNull(){
        hook(XC_MethodReplacement.DO_NOTHING);
    }

    public void replaceAll(Object o){
        hookAllMethod(XC_MethodReplacement.returnConstant(o));
    }

    public void replaceAllNull(){
        hookAllMethod(XC_MethodReplacement.DO_NOTHING);
    }

    public void hookAllMethod(XC_MethodHook xcMethodHook){
        if (isEnable)
            XposedBridge.hookAllMethods(mClazz,mMethodName,xcMethodHook);
    }

}
