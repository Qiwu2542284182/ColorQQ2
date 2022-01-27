package me.qiwu.colorqq.XHook;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class XConstructor {
    private static final HashMap<String, Constructor<?>> sConstructorCache = new HashMap<String, Constructor<?>>();
    private Class<?> mClass;
    private Class<?>[] mParameterTypes;
    private boolean isEnable = true;
    private XConstructor(Class<?> clazz){
        this.mClass = clazz;
    }

    public static XConstructor create(Class<?> clazz){
        return new XConstructor(clazz);
    }

    public static XConstructor create(String clazz){
        return new XConstructor(XUtils.findClass(clazz));
    }

    public XConstructor enable(boolean enable){
        this.isEnable = enable;
        return this;
    }

    public XConstructor enable(String key){
        this.isEnable = XSettingUtils.getBoolean(key);
        return this;
    }

    public XConstructor parameterTypes(Object... parameterTypes){
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

    public void hook(XC_MethodHook xcMethodHook){
        if (!isEnable)return;
        String fullConstructorName = mClass.getName() + XUtils.getParametersString(mParameterTypes);
        Constructor constructor = null;
        if (sConstructorCache.containsKey(fullConstructorName)){
            constructor = sConstructorCache.get(fullConstructorName);
        }
        if (constructor == null){
            sConstructorCache.remove(fullConstructorName);
            constructor  = get();
        }
        if (constructor != null){
            sConstructorCache.put(fullConstructorName,constructor);
            XposedBridge.hookMethod(constructor,xcMethodHook);
        }
    }

    public Constructor get(){
        Constructor constructor = null;
        if (mParameterTypes == null || mParameterTypes.length == 0){
            constructor = XUtils.findConstructorIfExists(mClass);
        } else {
            constructor = XUtils.findConstructorIfExists(mClass,mParameterTypes);
        }
        return constructor;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(Object ... initargs){
        Constructor constructor = get();
        if (constructor != null){
            try {
                return (T) constructor.newInstance(initargs);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void hookAllConstructor(XC_MethodHook xcMethodHook){
        if (isEnable)
            XposedBridge.hookAllConstructors(mClass,xcMethodHook);
    }

}
