package me.qiwu.colorqq.XHook;

import android.text.TextUtils;

import java.lang.reflect.Field;

public class XField {
    private Class<?> mClazz;
    private Object mBase;
    private Class<?> mType;
    private String  mName;
    private Field mField;


    private XField(Object o){
        if (o instanceof Class){
            mClazz = (Class) o;
        } else {
            mBase = o;
            mClazz = o.getClass();
        }

    }

    public static XField create(Object o){
        return new XField(o);
    }


    public static XField create(String clazz){
        return new XField(XUtils.findClass(clazz));
    }

    public XField type(Class<?> type){
        this.mType = type;
        return this;
    }

    public XField type(String type){
        this.mType = XUtils.findClass(type);
        return this;
    }

    public XField name(String name){
        this.mName = name;
        return this;
    }

    @SuppressWarnings ("unchecked")
    public <T> T get(){
        Field field = getField();
        if (field != null){
            try {
                return (T)field.get(mBase);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Field getField(){
        if (mField == null){
            mField = TextUtils.isEmpty(mName) ? XUtils.findFieldWithoutName(mClazz,mType) : XUtils.findField(mClazz,mType,mName);
        }
        return mField;

    }

    public void set(Object value){
        if (mBase == null) return;
        if (mField == null)
            mField = XUtils.findField(mClazz,mType,mName);
        if (mField != null){
            try {
                mField.set(mBase,value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
