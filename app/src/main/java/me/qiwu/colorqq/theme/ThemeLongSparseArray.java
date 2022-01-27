package me.qiwu.colorqq.theme;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.TypedValue;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class ThemeLongSparseArray extends LongSparseArray<Object> {

    private boolean isInit = false;
    private Resources mResources;
    private String mType = null;
    private boolean isColor = false;
    private LongSparseArray<Object> mCache = new LongSparseArray<>();
    private LongSparseArray<WeakReference<Object>> mCache2 = new LongSparseArray<>();
    private ResourcesFactory mResourcesFactory;

    public ThemeLongSparseArray(Resources resources){
        mResources = resources;
    }

    @Override
    public Object get(long key) {
        int id = (int) key;
        WeakReference<Object> weakReference = mCache2.get(id);
        if (weakReference != null){
            Object obj = weakReference.get();
            if (obj != null){
                return weakReference;
            }
            mCache2.delete(id);
        }
        if (!isInit){
            String type = mResources.getResourceTypeName(id);
            if ("color".equals(type)){
                isInit = true;
                isColor = true;
                mType = "color";
            } else if ("drawable".equals(type)){
                isInit = true;
                isColor = false;
                mType = "drawable";
            }
        }
        if (!TextUtils.isEmpty(mType) && mResourcesFactory != null){
            Object o = isColor ? mResourcesFactory.getColor(id) : mResourcesFactory.getDrawable(id);
            if (o != null){
                WeakReference<Object> weakReference1 = new WeakReference<>(o);
                mCache2.put(id,weakReference1);
                return weakReference1;
            }
        }

        return mCache.get(key);
    }

    @Override
    public void put(long key, Object value) {
        mCache.put(key, value);
    }

    @Override
    public Object valueAt(int index) {
        return mCache.valueAt(index);
    }

    @Override
    public int size() {
        return mCache.size();
    }

    @Override
    public void delete(long key) {
        mCache.delete(key);
    }

    @Override
    public void remove(long key) {
        mCache.remove(key);
    }

    @Override
    public void clear() {
        mCache.clear();
    }

    @Override
    public void removeAt(int index) {
        mCache.removeAt(index);
    }

    @Override
    public int indexOfKey(long key) {
        return mCache.indexOfKey(key);
    }

    @Override
    public int indexOfValue(Object value) {
        return mCache.indexOfValue(value);
    }

    @Override
    public long keyAt(int index) {
        return mCache.keyAt(index);
    }

    @Override
    public Object get(long key, Object valueIfKeyNotFound) {
        return get(key);
    }

    public void setResourcesFactory(ResourcesFactory resourcesFactory) {
        this.mResourcesFactory = resourcesFactory;
    }

    public interface ResourcesFactory{
        Object getDrawable(int id);
        Object getColor(int id);
    }

}
