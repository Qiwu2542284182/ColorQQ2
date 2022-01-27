package me.qiwu.colorqq.hook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.colorqq.R;
import me.qiwu.colorqq.XHook.XConstructor;
import me.qiwu.colorqq.XHook.XField;
import me.qiwu.colorqq.XHook.XMethod;
import me.qiwu.colorqq.XHook.XUtils;
import me.qiwu.colorqq.XHook.XposedCompact;
import me.qiwu.colorqq.util.DensityUtil;
import me.qiwu.colorqq.util.QQHelper;
import me.qiwu.colorqq.XHook.XSettingUtils;
import me.qiwu.colorqq.widget.MainViewPager;

import static me.qiwu.colorqq.util.ClassUtil.DrawerFrame;
import static me.qiwu.colorqq.util.ClassUtil.QQSettingMe;

/**
 * Created by  on 2018/12/14.
 */

public class DrawerHook implements IHook {
    private static DrawerHook sInstance;
    private int width = 0;//c
    private int right_drawer_width = DensityUtil.dip2px(QQHelper.getContext(),100);//d
    private int max_width = (int) (((double) (QQHelper.getContext().getResources().getDisplayMetrics().density * 100.0f)) + 0.5d);//i
    public static DrawerHook getInstance(){
        synchronized (DrawerHook.class){
            if (sInstance == null){
                sInstance = new DrawerHook();
            }
            return sInstance;
        }
    }


    @Override
    public void startHook() {
        cacheDrawer();

        useSuofangshiDrawer();
        //useDiejiaceng();
        //setPadding();

    }

    private void cacheDrawer(){
        XposedBridge.hookAllConstructors(XposedHelpers.findClass(DrawerFrame, XposedCompact.classLoader), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                MainViewPager.sDrawerCache = new WeakReference<>(param.thisObject);
            }
        });
    }

    @Override
    public boolean init() {
       return true;
    }


    private  void useDiejiaceng(){
        XConstructor.create(QQSettingMe)
                .enable(XSettingUtils.getInt("drawer_mode")==1 && XSettingUtils.getBoolean("drawer_use_diejiaceng"))
                .hookAllConstructor(new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        int radius = DensityUtil.dip2px(QQHelper.getContext(),Integer.parseInt(XSettingUtils.getString("drawer_corner","0")));
                        ViewGroup viewGroup = XField.create(param.thisObject).name("a").type(ViewGroup.class).get();
                        Context context = viewGroup.getContext();
                        //第一个叠加层
                        ImageView imageView = new ImageView(context);
                        GradientDrawable gradientDrawable = new GradientDrawable();
                        gradientDrawable.setColor(Color.parseColor("#B3FFFFFF"));
                        gradientDrawable.setCornerRadii(new float[]{radius,radius,0,0,0,0,radius,radius});
                        imageView.setBackground(gradientDrawable);
                        imageView.setFocusable(false);

                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(context,60),DensityUtil.dip2px(context, Integer.valueOf(XSettingUtils.getString("drawer_height","650"))));

                        layoutParams.setMargins(0,DensityUtil.dip2px(context,100),DensityUtil.dip2px(context,45),DensityUtil.dip2px(context,100));
                        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        viewGroup.addView(imageView,layoutParams);

                        //第二个叠加层
                        ImageView imageView2 = new ImageView(context);
                        GradientDrawable gradientDrawable2 = new GradientDrawable();
                        gradientDrawable2.setColor(Color.parseColor("#80FFFFFF"));
                        gradientDrawable2.setCornerRadii(new float[]{radius,radius,0,0,0,0,radius,radius});
                        imageView2.setBackground(gradientDrawable2);
                        imageView2.setFocusable(false);
                        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(DensityUtil.dip2px(context,70),DensityUtil.dip2px(context,Integer.valueOf(XSettingUtils.getString("drawer_height","650"))-50));
                        layoutParams2.setMargins(0,DensityUtil.dip2px(context,120),DensityUtil.dip2px(context,60),DensityUtil.dip2px(context,110));
                        layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL);
                        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        imageView2.setPadding(DensityUtil.dip2px(context,30),0,DensityUtil.dip2px(context,30),0);
                        viewGroup.addView(imageView2,layoutParams2);
                    }
                });

    }

    public static class ViewHolder{
        View view_a;
        ViewGroup mainFragment;
        ViewGroup qq_setting_me_content;
        ViewGroup qq_setting_me_bg;
    }

    private void useSuofangshiDrawer(){
        int radius = Integer.parseInt(XSettingUtils.getString("drawer_corner","0"));
        XMethod.create(DrawerFrame)
                .enable(XSettingUtils.getInt("drawer_mode")==1 && radius != 0)
                .name("a")
                .parameterTypes(ViewGroup.class, ViewGroup.class, View.class, ViewGroup.class)
                .hook(new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        View drawer = (View) param.thisObject;
                        ViewHolder viewHolder = new ViewHolder();
                        viewHolder.view_a = (View) param.args[2];
                        viewHolder.mainFragment = (ViewGroup) param.args[0];
                        viewHolder.qq_setting_me_content = (ViewGroup) param.args[1];
                        viewHolder.qq_setting_me_bg = (ViewGroup) param.args[3];
                        drawer.setTag(R.id.tag_qq_drawer,viewHolder);
                        ViewGroup viewGroup = (ViewGroup)param.args[0];
                        viewGroup.bringToFront();
                        viewGroup.setOutlineProvider(new ViewOutlineProvider() {
                            @Override
                            public void getOutline(View view, Outline outline) {
                                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), DensityUtil.dip2px(XUtils.getContext(),radius));
                            }
                        });
                        viewGroup.setClipToOutline(false);
                    }
                });

        XMethod.create(DrawerFrame)
                .name("a")
                .enable(XSettingUtils.getInt("drawer_mode")==1)
                .parameterTypes(int.class,int.class)
                .returnType(void.class)
                .hook(new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        View drawerFrame = (View) param.thisObject;
                        ViewHolder viewHolder = (ViewHolder) drawerFrame.getTag(R.id.tag_qq_drawer);

                        int i = (int)param.args[0];//always equal 0
                        int i2 = (int)param.args[1];

                        if (width == 0){
                            width = drawerFrame.getMeasuredWidth();
                        }



                        if (viewHolder.qq_setting_me_content != null){
                            if (i2!=0 && viewHolder.qq_setting_me_content.getVisibility() != View.VISIBLE){
                                viewHolder.qq_setting_me_content.setVisibility(View.VISIBLE);
                            }
                            viewHolder.qq_setting_me_content.setTranslationX(i2  - width);
                        }
                        if (viewHolder.qq_setting_me_bg != null){
                            if (i2!=0 && viewHolder.qq_setting_me_bg.getVisibility() != View.VISIBLE){
                                viewHolder.qq_setting_me_bg.setVisibility(View.VISIBLE);
                            }
                            viewHolder.qq_setting_me_bg.setTranslationX(i2 - width);
                        }
                        if (viewHolder.mainFragment != null){
                            viewHolder.mainFragment.setTranslationX(i2);
                        }
                        XMethod.create(drawerFrame.getClass()).name("c").parameterTypes(int.class,int.class).call(drawerFrame,width,i2);
                        return null;
                    }
                });
    }




    private void setPadding(){
        XConstructor.create(DrawerFrame)
                .enable("start_drawer_padding")
                .hookAllConstructor(new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XField.create(param.thisObject).name("C").type(int.class).set(DensityUtil.dip2px(XUtils.getContext(),Integer.valueOf(XSettingUtils.getString("drawer_padding","74"))));
                    }
                });

        XMethod.create(DrawerFrame)
                .enable("start_drawer_padding")
                .returnType(int.class)
                .name("getTargetWidth")
                .replace(DensityUtil.dip2px(XUtils.getContext(),Integer.valueOf(XSettingUtils.getString("drawer_padding","74"))));
    }


}
