package me.qiwu.colorqq.annotation;

import android.app.Activity;
import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class BindView {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ViewResId {
        int value();
    }

    public static void load(Object obj, View contentParentView) {
        Class<?> cls = obj.getClass(); //获取obj的Class
        Field[] fields = cls.getDeclaredFields(); //获取Class中所有的成员
        for (Field field : fields) { //遍历所有成员
            ViewResId viewResId = field.getAnnotation(ViewResId.class);//获取成员的注解
            //判断成员是否含有注解
            if (viewResId != null) {
                int viewId = viewResId.value(); //获取成员注解的参数，这就是我们传进去控件Id
                if (viewId != -1) {
                    try {
                        field.setAccessible(true);//取消成员的封装
                        field.set(obj, contentParentView.findViewById(viewId));//即 field = contentParentView.findViewById(viewId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void load(Activity activity){
        load(activity,activity.getWindow().getDecorView());
    }
}