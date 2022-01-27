package me.qiwu.colorqq.hook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.qiwu.colorqq.BuildConfig;
import me.qiwu.colorqq.R;
import me.qiwu.colorqq.XHook.XField;
import me.qiwu.colorqq.XHook.XMethod;
import me.qiwu.colorqq.XHook.XUtils;
import me.qiwu.colorqq.activity.FabButtonSettingActivity;
import me.qiwu.colorqq.bean.FabInfo;
import me.qiwu.colorqq.hook.IHook;
import me.qiwu.colorqq.hook.ThemeHook;
import me.qiwu.colorqq.library.FloatingActionButton.FloatingActionButton;
import me.qiwu.colorqq.library.FloatingActionButton.FloatingActionMenu;
import me.qiwu.colorqq.theme.ThemeUtil;
import me.qiwu.colorqq.util.ClassUtil;
import me.qiwu.colorqq.util.DensityUtil;
import me.qiwu.colorqq.util.FileUtil;
import me.qiwu.colorqq.util.FunUtils;
import me.qiwu.colorqq.util.IdsUtil;
import me.qiwu.colorqq.util.QQCustomDialog;
import me.qiwu.colorqq.util.QQHelper;
import me.qiwu.colorqq.XHook.XSettingUtils;
import me.qiwu.colorqq.XHook.XposedCompact;
import me.qiwu.colorqq.widget.MainTabLayout;
import me.qiwu.colorqq.widget.MainViewPager;

import static android.view.View.GONE;
import static android.view.View.OVER_SCROLL_NEVER;
import static android.view.View.VISIBLE;
import static me.qiwu.colorqq.util.ClassUtil.BusinessInfoCheckUpdate$RedTypeInfo;
import static me.qiwu.colorqq.util.ClassUtil.DrawerFrame;
import static me.qiwu.colorqq.util.ClassUtil.FrameFragment;
import static me.qiwu.colorqq.util.ClassUtil.Leba;
import static me.qiwu.colorqq.XHook.XposedCompact.classLoader;


public class MainUIHook implements IHook, TabHost.OnTabChangeListener {
    private Context qqContext;
    private Context moduleContext;
    private ArrayList<FloatingActionButton>fabs = new ArrayList<>();
    private TabHost mTabHost;
    private FrameLayout mainFrameLayout;
    private MainTabLayout tabLayout;
    private HashMap<Integer, ImageView> tempMainView = new HashMap<>();
    private HashMap<Integer, Drawable.ConstantState> mainRightTopIcon = new HashMap<>();
    private HashMap<Integer, FrameLayout> mainFrameLayouts = new HashMap<>();
    private TextView mainTextView;
    private ImageView mainImageView;
    public static String themeError = "";

    public static MainUIHook getInstance(){
        return new MainUIHook();
    }



    private void checkTheme(Context context){

        if (!TextUtils.isEmpty(themeError)){
            Toast.makeText(context,themeError,Toast.LENGTH_LONG).show();
        }
        boolean showPermission = context.getSharedPreferences(BuildConfig.APPLICATION_ID,0).getBoolean("showPermissionDialog",true);
        if (showPermission && ThemeHook.isStopTheme) {
            new AlertDialog.Builder(context,5)
                    .setMessage("读取本地文件失败，ColorQQ将无法正常运行。请检查ColorQQ本地资源文件是否存在，或是否拥有读取权限。\n如果是双开QQ，请将ColorQQ本地资源文件复制到双开目录。")
                    .setPositiveButton("确定", (dialog, which) -> { })
                    .setNegativeButton("不再提示", (dialog, which) -> {
                        QQHelper.getContext().getSharedPreferences(BuildConfig.APPLICATION_ID,0).edit().putBoolean("showPermissionDialog",false).apply();
                        //dialog.dismiss();
                    }).show();
        }
    }

    private void addMainWidget(){
        XposedHelpers.findAndHookMethod(TabHost.class,"setup", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                TabHost tabHost = (TabHost) param.thisObject;
                if (tabHost.getContext().getClass().getName().equals(ClassUtil.SplashActivity)){
                    mTabHost = tabHost;

                    if (isUseViewPager()){
                        if (mainFrameLayouts.size() == 0){
                            for (int i = 0;i < 3;i++){
                                FrameLayout frameLayout = new FrameLayout(mTabHost.getContext());
                                mainFrameLayouts.put(i,frameLayout);
                            }
                        }
                    }

                    try {
                        qqContext = mTabHost.getContext();
                        moduleContext = XposedCompact.getModuleContext(qqContext);

                        mainFrameLayout = (FrameLayout) mTabHost.getChildAt(0);

                        if (XSettingUtils.getBoolean("fab_use")&&mainFrameLayout.findViewWithTag("fab")==null){
                            addFab(mainFrameLayout);
                        }
                        if (XSettingUtils.getBoolean("tab_use")&&mainFrameLayout.findViewWithTag("tab")==null){
                            addTab(mainFrameLayout);
                        }
                        addTop(mainFrameLayout);
                        if (XSettingUtils.getBoolean("tab_hide_origin")){
                            hideTabHostOrBg(mainFrameLayout,false);
                        }
                        checkTheme(qqContext);
                        //禁用简洁模式QQ空间
                        QQHelper.getContext().getSharedPreferences(QQHelper.getCurrentAccountUin() + "_QZ_QQ_shared_preference",0).edit().putInt("qzone_feed_gray_mask",0).apply();
                    } catch (Throwable throwable){
                        XposedBridge.log("错误"+ Log.getStackTraceString(throwable));
                    }

                }
            }

        });

        if(XSettingUtils.getBoolean("tab_use")){
            XposedHelpers.findAndHookMethod(TabHost.class, "addTab", TabHost.TabSpec.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    TabHost.TabSpec tabSpec = (TabHost.TabSpec) param.args[0];
                    if (tabSpec != null){
                        String name = tabSpec.getTag();
                        if (name.equals(ClassUtil.ReadinjoyTabFrame) || name.equals(ClassUtil.ReadinjoyTabFrame2) || name.equals(ClassUtil.RIJXTabFrame) || name.equals(ClassUtil.RIJXTabFrame2)){
                            param.setResult(null);
                        }
                    }
                }
            });
        }


        XMethod.create(FrameFragment).name("createTabContent").parameterTypes(String.class).returnType(View.class).hook(new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                ViewGroup viewGroup = (ViewGroup) param.getResult();
                initMainView(viewGroup,param.thisObject);

                int currentTab = mTabHost.getCurrentTab();
                if (currentTab == 2 || currentTab == 3){
                    hideLebaSearch(viewGroup);
                }

                if (XSettingUtils.getBoolean("tab_use")&&XSettingUtils.getBoolean("tab_use_viewpager")){
                    FrameLayout frameLayout = mainFrameLayouts.get(currentTab);
                    frameLayout.removeAllViews();
                    frameLayout.addView(viewGroup);
                    param.setResult(new View(viewGroup.getContext()));
                }
            }
        });


        if (XSettingUtils.getBoolean("tab_use")){
            setTabLayoutMsgTips();
        }
    }

    private void findHorizontalScrollView(List<View> list,ViewGroup viewGroup){
        for (int i = 0;i < viewGroup.getChildCount() && list.isEmpty();i ++){
            View view = viewGroup.getChildAt(i);
            if (view instanceof HorizontalScrollView){
                list.add(view);
                return;
            } else if (view instanceof ViewGroup){
                findHorizontalScrollView(list,(ViewGroup)view);
            }
        }
    }


    private boolean isUseViewPager(){
        return XSettingUtils.getBoolean("tab_use")&&XSettingUtils.getBoolean("tab_use_viewpager");
    }

    private void hideTabHostOrBg(FrameLayout frameLayout,boolean isHideBg){
        for (int i=0;i<frameLayout.getChildCount();i++){
            View view = frameLayout.getChildAt(i);
            if (view.getId()==android.R.id.tabs){
                if (isHideBg){
                    view.setBackground(null);
                    view.setTag("ban");
                } else {
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    layoutParams.width = 0;
                    layoutParams.height = 0;
                    view.setVisibility(GONE);
                }
            } else if (view.getClass().getName().contains("QQBlur")){
                view.setAlpha(0);
            }
        }
    }

    private void addTop(FrameLayout frameLayout){
        for (int i=0;i<frameLayout.getChildCount();i++){
            View view = frameLayout.getChildAt(i);
            if (view instanceof RelativeLayout){
                RelativeLayout relativeLayout = (RelativeLayout)view;
                if (relativeLayout.getTag()==null){
                    if (XSettingUtils.getBoolean("tab_use")){
                        relativeLayout.setElevation(Float.parseFloat(XSettingUtils.getString("tab_elevation","5f"))+1f);
                        if (XSettingUtils.getBoolean("top_useMenu")) {
                            relativeLayout.getChildAt(0).setAlpha(0);
                        }
                    } else {
                        if (XSettingUtils.getBoolean("top_useMenu")){
                            relativeLayout.getChildAt(0).setAlpha(0);
                            ImageView view1 = new ImageView(relativeLayout.getContext());
                            view1.setImageDrawable(new BitmapDrawable(qqContext.getResources(),FileUtil.getTopIcon("ic_menu.png")));
                            if (XSettingUtils.getBoolean("top_tint")){
                                view1.setColorFilter(XSettingUtils.getInt("top_tint_color",Color.WHITE));
                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(qqContext,27),DensityUtil.dip2px(qqContext,27));
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                            layoutParams.leftMargin = DensityUtil.dip2px(qqContext,12);
                            relativeLayout.addView(view1,layoutParams);
                        }
                        if (XSettingUtils.getBoolean("top_useSearch")){
                            relativeLayout.getLayoutParams().width = -1;
                            ImageView imageView = new ImageView(relativeLayout.getContext());
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(qqContext, 27), DensityUtil.dip2px(qqContext, 27));
                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                            layoutParams.rightMargin = DensityUtil.dip2px(qqContext, 12);
                            imageView.setImageDrawable(new BitmapDrawable(qqContext.getResources(),FileUtil.getTopIcon("ic_search.png")));
                            if (XSettingUtils.getBoolean("top_tint")){
                                imageView.setColorFilter(XSettingUtils.getInt("top_tint_color",Color.WHITE));
                            }
                            imageView.setOnClickListener(v -> FunUtils.search(qqContext,classLoader));
                            relativeLayout.addView(imageView, layoutParams);

                        }
                    }

                }

            }
        }
    }

    private int getImmerBarHeight(Context context){
        String s = Build.MANUFACTURER.toUpperCase();
        String s2 = s + "-" + Build.MODEL;
        if (s.endsWith("BBK") || s2.equals("OPPO-3007")){
            return 0;
        }
        return context.getSharedPreferences("mobileQQ",0).getInt("status_bar_height",0);
    }

    private int getTitleBarHeight(Context context){
        int headerId = context.getResources().getIdentifier("title_bar_height","dimen",context.getPackageName());
        return context.getResources().getDimensionPixelSize(headerId);
    }

    private void hookTabChange(){
        XMethod.create(FrameFragment)
                .name("onTabChanged")
                .hook(new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        int position = mTabHost.getCurrentTab();
                        if (tabLayout!=null && mainImageView!=null && mainTextView!=null){
                            int tabPosition = tabLayout.getSelectedTabPosition();

                            if (position != tabPosition){
                                if (tabLayout.getTabAt(position)!=null){
                                    tabLayout.getTabAt(position).select();
                                }
                            }

                            if (!XSettingUtils.getBoolean("top_useSearch")){
                                if (mainRightTopIcon.get(position)!=null){
                                    mainImageView.setImageDrawable(mainRightTopIcon.get(position).newDrawable());
                                } else {
                                    mainImageView.setImageDrawable(null);
                                }
                            }

                            mainTextView.setText(position==0?"消息":position==1?"联系人":position==mTabHost.getTabWidget().getTabCount()-1?"动态":"看点");
                        }
                    }
                });
    }


    private void setTabLayoutMsgTips(){
        XMethod.create(ClassUtil.SplashActivity)
                //.name("a")
                .parameterTypes(int.class, int.class, Object.class)
                .hook(new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        int num1 = (int)param.args[0];
                        int num2 = (int)param.args[1];
                        Object num3 = param.args[2];
                        if (num2==16 && num3 instanceof Integer){
                            if (num1==32){
                                if ((int)num3!=0){
                                    tabLayout.showMsg(0,(int)num3);
                                }else {
                                    tabLayout.hideMsg(0);
                                }
                            }
                        }
                    }
                });

        for (Method method : XUtils.findClass(ClassUtil.SplashActivity).getDeclaredMethods()){
            if (method.getParameterTypes().length == 2 && method.getParameterTypes()[0] == int.class && method.getParameterTypes()[1].getName().endsWith("BusinessInfoCheckUpdate$RedTypeInfo")){
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (tabLayout!=null){
                            int i = (int) param.args[0];
                            Object redTypeInfo = param.args[1];
                            if (redTypeInfo!=null){
                                Object msg = XField.create(redTypeInfo).name("red_content").get();
                                String num = XMethod.create(msg).hasNotParameterTypes().name("get").call();
                                if (i == 33 ){
                                    if (num.equals("")){
                                        tabLayout.hideMsg(1);
                                    }else {
                                        tabLayout.showMsg(1,Integer.parseInt(num));
                                    }
                                } else if (i == 34 ){
                                    if (num.equals("")){
                                        tabLayout.hideMsg(mTabHost.getTabWidget().getTabCount()-1);
                                    }else {
                                        tabLayout.showMsg(mTabHost.getTabWidget().getTabCount()-1,Integer.parseInt(num));
                                    }
                                }
                            } else {
                                if (i == 33){
                                    tabLayout.hideMsg(1);
                                } else if (i == 34){
                                    tabLayout.hideMsg(mTabHost.getTabWidget().getTabCount()-1);
                                }
                            }
                        }
                    }
                });
                break;
            }
        }
    }

    private void initMainView(ViewGroup viewGroup, Object thisObject) throws Throwable{
        for (int i = 0;i<viewGroup.getChildCount();i++){
            View view = viewGroup.getChildAt(i);
            if (view.getClass().getName().contains("ImmersiveTitleBar2")){
                if (XSettingUtils.getBoolean("tab_use")){
                    for (Field field : view.getClass().getDeclaredFields()){
                        if (field.getType() == int.class && !Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())){
                            field.set(view,field.getInt(view) + DensityUtil.dip2px(XUtils.getContext(),Integer.parseInt(XSettingUtils.getString("tab_height","54"))));
                            break;
                        }
                    }
                }

            } else if (view instanceof ImageView){
                if (view.getContentDescription()!=null&&"快捷入口".equals(view.getContentDescription().toString())){
                    if (XSettingUtils.getBoolean("top_useSearch")){
                        view.setVisibility(View.INVISIBLE);
                    }
                    tempMainView.put(0, (ImageView) view);
                    mainRightTopIcon.put(0,((ImageView)view).getDrawable().getConstantState());
                } else {
                    if (view.getId()== IdsUtil.ivTitleBtnRightImage){
                        if (XSettingUtils.getBoolean("top_useSearch")){
                            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                            layoutParams.width = 0;
                            layoutParams.height = 0;
                            view.setLayoutParams(layoutParams);
                        }
                        if (view.getContentDescription()!=null && "添加".equals(view.getContentDescription().toString())){
                            mainRightTopIcon.put(1,((ImageView)view).getDrawable().getConstantState());
                            tempMainView.put(1, (ImageView) view);
                        } else {
                            mainRightTopIcon.put(mTabHost.getTabWidget().getTabCount()-1,((ImageView)view).getDrawable().getConstantState());
                            tempMainView.put(mTabHost.getTabWidget().getTabCount()-1, (ImageView) view);
                        }
                    }
                }
            } else if (view.getId() == IdsUtil.recent_chat_list){
                if (XSettingUtils.getBoolean("top_hideSearch")){
                    XMethod.create(ClassUtil.ListView).name("removeHeaderView").returnType(boolean.class).parameterTypes(View.class).call(view,getSearchBarAssistant(thisObject));
                }
                if (XSettingUtils.getBoolean("tab_hide_origin")){
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    view.setPadding(0,0,0,0);
                }

            } else if (view.getId() == IdsUtil.et_search_keyword){
                if (XSettingUtils.getBoolean("top_hideSearch")){
                    View search = (View)view.getParent();
                    ViewGroup.LayoutParams layoutParams = search.getLayoutParams();
                    layoutParams.width = 0;
                    layoutParams.height = 0;
                    search.setLayoutParams(layoutParams);
                }
            } else if (view.getClass().getName().equals(ClassUtil.CommonRefreshLayout)){
                initMainView((ViewGroup) view,thisObject);
            } else if (view instanceof ViewGroup){
                initMainView((ViewGroup) view,thisObject);
            }
        }
    }

    private View getSearchBarAssistant(Object mainFragment) throws Throwable{
        Field temp = XField.create(FrameFragment).type(Map.class).getField();
        Object frame = ((Map)temp.get(mainFragment)).get(mTabHost.getCurrentTabTag());
        if (frame == null){
            return null;
        }
        Field[] fields = frame.getClass().getDeclaredFields();
        for (Field field : fields){
            field.setAccessible(true);
            if (field.getType() == View.class){
                View view = (View)field.get(frame);
                if (view!=null){
                    if (view.findViewById(IdsUtil.et_search_keyword)!=null){
                        return view;
                    }
                }

            }
        }
        return null;
    }

    private ViewPager addViewPager(FrameLayout v){
        ViewPager viewPager = new MainViewPager(qqContext);

        viewPager.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        viewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                FrameLayout frameLayout = mainFrameLayouts.get(position);
                container.addView(frameLayout);
                return frameLayout;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View)object);
            }
        });

        v.addView(viewPager,1);
        return viewPager;
    }

    private void addTab(FrameLayout frameLayout){
        int color = XSettingUtils.getInt("tab_mode") == 0? qqContext.getResources().getColor(IdsUtil.skin_color_title_immersive_bar) : XSettingUtils.getInt("tab_mainColor", Color.parseColor("#ff009688"));
        RelativeLayout relativeLayout = new RelativeLayout(qqContext);
        relativeLayout.setTag("top");
        relativeLayout.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        relativeLayout.setElevation(Float.valueOf(XSettingUtils.getString("tab_elevation","5f")));

        ImageView bgImage = new ImageView(qqContext);
        bgImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        int bgMode = XSettingUtils.getInt("tab_bg_mode");
        if (bgMode == 0){
            bgImage.setImageDrawable(new ColorDrawable(color));
        } else {
            File file = new File(FileUtil.getTabIconPath("tab_bg.png"));
            if (file.exists()){
                bgImage.setImageDrawable(Drawable.createFromPath(file.getAbsolutePath()));
            } else {
                bgImage.setImageDrawable(new ColorDrawable(color));
            }
        }

        RelativeLayout.LayoutParams bgLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bgLayoutParams.addRule(RelativeLayout.ALIGN_TOP,android.R.id.text1);
        bgLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM,android.R.id.stopSelectingText);
        relativeLayout.addView(bgImage,bgLayoutParams);



        View immerBar = new View(qqContext);
        immerBar.setId(android.R.id.text1);
        relativeLayout.addView(immerBar,new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getImmerBarHeight(qqContext)));
        RelativeLayout titleBar = new RelativeLayout(qqContext);
        mainTextView = new TextView(qqContext);
        mainTextView.setText("消息");
        mainTextView.setTextSize(17f);
        mainTextView.setGravity(Gravity.CENTER);
        mainTextView.setTextColor(XSettingUtils.getInt("tab_title_Color",Color.WHITE));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleBar.addView(mainTextView,layoutParams);
        mainImageView = new ImageView(qqContext);
        if (!XSettingUtils.getBoolean("top_useSearch")){
            Drawable.ConstantState constantState = mainRightTopIcon.get(mTabHost.getCurrentTab());
            if (constantState != null){
                mainImageView.setImageDrawable(constantState.newDrawable());
            }

            mainImageView.setOnClickListener(v -> {
                if (tempMainView.get(mTabHost.getCurrentTab())!=null){
                    View tempView = tempMainView.get(mTabHost.getCurrentTab());
                    if (tempView != null){
                        tempView.performClick();
                    }
                }

            });
        } else {
            mainImageView.setImageDrawable(new BitmapDrawable(qqContext.getResources(),FileUtil.getTopIcon("ic_search.png")));
            if (XSettingUtils.getBoolean("top_tint")){
                mainImageView.setColorFilter(XSettingUtils.getInt("top_tint_color",Color.WHITE));
            }
            mainImageView.setOnClickListener(v -> FunUtils.search(qqContext,classLoader));
        }

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(DensityUtil.dip2px(qqContext,27),DensityUtil.dip2px(qqContext,27));
        layoutParams1.addRule(RelativeLayout.CENTER_VERTICAL);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams1.setMargins(0,0,DensityUtil.dip2px(qqContext,10),0);
        titleBar.addView(mainImageView,layoutParams1);
        if (XSettingUtils.getBoolean("top_useMenu")){
            ImageView menuImage = new ImageView(qqContext);
            menuImage.setImageDrawable(new BitmapDrawable(qqContext.getResources(),FileUtil.getTopIcon("ic_menu.png")));
            if (XSettingUtils.getBoolean("top_tint")){
                menuImage.setColorFilter(XSettingUtils.getInt("top_tint_color",Color.WHITE));
            }
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(DensityUtil.dip2px(qqContext,27),DensityUtil.dip2px(qqContext,27));
            layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams2.setMargins(DensityUtil.dip2px(qqContext,10),0,0,0);
            titleBar.addView(menuImage,layoutParams2);
        }

        titleBar.setId(android.R.id.text2);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getTitleBarHeight(qqContext));
        layoutParams2.addRule(RelativeLayout.BELOW,android.R.id.text1);
        relativeLayout.addView(titleBar,layoutParams2);

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(moduleContext,R.style.AppTheme);
        tabLayout = new MainTabLayout(contextThemeWrapper);
        tabLayout.setId(android.R.id.stopSelectingText);
        tabLayout.setTag("tag");
        tabLayout.setUnreadTextColor(XSettingUtils.getInt("tab_unread_color_mode") == 0 ? color : XSettingUtils.getInt("tab_unread_Color",Color.BLACK));
        tabLayout.setUnreadBgColor(XSettingUtils.getInt("tab_unread_bg_Color",Color.WHITE));
        for (int i = 0;i<3;i++){
            tabLayout.addTab(tabLayout.newTab());
        }
        tabLayout.setTabRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(XSettingUtils.getInt("tab_IndicatorColor", Color.WHITE));
        tabLayout.setSelectedTabIndicatorHeight(DensityUtil.dip2px(qqContext, Integer.valueOf(XSettingUtils.getString("tab_IndicatorHeight", "3"))));
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtil.dip2px(qqContext,Integer.valueOf(XSettingUtils.getString("tab_height","54"))));
        layoutParams3.addRule(RelativeLayout.BELOW,android.R.id.text2);
        relativeLayout.addView(tabLayout,layoutParams3);
        frameLayout.addView(relativeLayout,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getImmerBarHeight(qqContext)+getTitleBarHeight(qqContext)+DensityUtil.dip2px(qqContext,Integer.valueOf(XSettingUtils.getString("tab_height","54")))));

        if (XSettingUtils.getBoolean("tab_use_viewpager")){
            tabLayout.setupWithViewPager(addViewPager(frameLayout));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mTabHost.getTabWidget()!=null){
                    mTabHost.setCurrentTab(tabLayout.getSelectedTabPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (mTabHost.getTabWidget()!=null){
                    mTabHost.setCurrentTab(tabLayout.getSelectedTabPosition());
                }
            }
        });

        for (int i = 0;i < 3;i++){
            if (XSettingUtils.getInt("tab_content_mode")==0){
                tabLayout.setIcon(i,new BitmapDrawable(qqContext.getResources(), FileUtil.getTabIcon(i==0 ? "ic_message.png":i==1?"ic_contact.png":"ic_leba.png")));
            } else if (XSettingUtils.getInt("tab_content_mode")==1){
                tabLayout.setText(i,i==0?XSettingUtils.getString("tab_message_name","消息"):i==1?XSettingUtils.getString("tab_contact_name","联系人"):XSettingUtils.getString("tab_leba_name","动态"));
            }
            if (i!=0){
                tabLayout.onTabUnselected(tabLayout.getTabAt(i));
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addFab(FrameLayout frameLayout){
        final View view = new View(qqContext);
        view.setVisibility(GONE);
        int fabColor = XSettingUtils.getInt("fab_mode")==0? qqContext.getResources().getColor(IdsUtil.skin_color_title_immersive_bar) : XSettingUtils.getInt("fab_mainColor", Color.parseColor("#ff009688"));
        final FloatingActionMenu fabMenu = new FloatingActionMenu(moduleContext);
        fabMenu.setTag("fab");
        fabMenu.setMenuButtonColorNormal(fabColor);
        fabMenu.setMenuButtonColorPressed(fabColor);
        String fileName = XSettingUtils.getString("fab_menu_icName");
        fabMenu.setMenuIcon(new BitmapDrawable(qqContext.getResources(), FileUtil.getFabIcon(fileName.equals("") ? "ic_add.png" : fileName)));
        fabMenu.initMenuButton();

        List<FabInfo> data = getFabData();
        for (int i=0;i<data.size();i++){
            FabInfo fabInfo = data.get(i);
            FloatingActionButton fab=new FloatingActionButton(moduleContext);//新建对象
            fab.setImageDrawable(new BitmapDrawable(qqContext.getResources(),FileUtil.getFabIcon(fabInfo.icon)));//fab图标
            fab.setButtonSize(fabInfo.bottonSize);//fab大小
            fab.setColorNormal(fabColor);//正常颜色
            fab.setColorPressed(fabColor);//按压颜色
            fab.setLabelText(fabInfo.title);//label文本
            fab.setLabelColors(fabColor, fabColor, fabColor);//label颜色
            fab.setLabelTextColor(XSettingUtils.getInt("fab_textColor",Color.WHITE));
            fab.setTag(R.id.tag_fab_icName,fabInfo.icon);
            fab.setTag(R.id.tag_fab_onClick,fabInfo.onClick);
            fabMenu.addMenuButton(fab);
            fabs.add(fab);
        }


        fabMenu.setFloatButtonClickListener((fab, index) -> startActivity(fab.getTag(R.id.tag_fab_onClick).toString()));

        fabMenu.setOnMenuToggleListener(opened -> {
            if (opened){
                checkFabIcon();
            }
            view.setVisibility(opened ? VISIBLE : GONE);
        });

        if (XSettingUtils.getBoolean("fab_touch_moveBotton")) {
            fabMenu.getMenuButton().setOnTouchListener(new View.OnTouchListener() {
                float moveX = 0;
                float moveY = 0;
                long downTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            moveX = event.getRawX();
                            moveY = event.getRawY();
                            downTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float nowX = event.getRawX();
                            float nowY = event.getRawY();
                            float dx = (float) ((int) nowX - moveX);
                            float dy = (float) ((int) nowY - moveY);
                            moveX = nowX;
                            moveY = nowY;
                            fabMenu.setX(fabMenu.getX() + dx);
                            fabMenu.setY(fabMenu.getY() + dy);
                            break;
                        case MotionEvent.ACTION_UP:
                            long nowTime = System.currentTimeMillis();
                            if (nowTime - downTime > 300) {
                                fabMenu.getMenuButton().setPressed(false);
                                return true;
                            }
                    }
                    return false;
                }
            });
        }

        view.setOnClickListener(v -> fabMenu.close(true));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.END | Gravity.BOTTOM;
        layoutParams.bottomMargin = Integer.parseInt(XSettingUtils.getString("fab_menu_marginBottom","80"));
        layoutParams.rightMargin = Integer.parseInt(XSettingUtils.getString("fab_menu_marginEnd","50"));
        frameLayout.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(fabMenu, layoutParams);
    }



    private void startActivity(String onClick){
        switch (onClick){
            case "qzone" :
                FunUtils.qzone(qqContext);
                break;
            case "wallet":
                FunUtils.wallet(qqContext);
                break;
            case "fav":
                FunUtils.fav(qqContext,classLoader);
                break;
            case "photo":
                FunUtils.photo(qqContext,classLoader);
                break;
            case "exit" :
                FunUtils.exit();
                break;
            case "search":
                FunUtils.search(qqContext,classLoader);
                break;
            case "qlink" :
                FunUtils.qlink(qqContext);
                break;
            case "money" :
                FunUtils.money(qqContext);
                break;
            case "message" :
                if (mTabHost!=null){
                    mTabHost.setCurrentTab(0);
                }
                break;
            case "contact" :
                if (mTabHost!=null){
                    mTabHost.setCurrentTab(1);
                }
                break;
            case "readInjoy" :
                FunUtils.readInjoy(qqContext);
                break;
            case "leba" :
                if (mTabHost!=null){
                    mTabHost.setCurrentTab(mTabHost.getTabWidget().getChildCount()-1);
                }
                break;
            default:
                break;
        }
        if (onClick.startsWith("jump_")){
            FunUtils.jump(qqContext,onClick.substring(5),classLoader);
        } else if (onClick.startsWith("url_")){
            FunUtils.url(qqContext,onClick.substring(4),classLoader);
        } else  if (onClick.startsWith("friend_")){
            FunUtils.friend(qqContext,onClick.substring(7));
        } else if (onClick.startsWith("app_")){
            FunUtils.openApp(onClick.substring(4));
        }
    }

    private boolean checkFabIcon(){
        if (!fabs.isEmpty()){
            for (FloatingActionButton fab : fabs){
                if (fab.getBackground()==null){
                    String icon = fab.getTag(R.id.tag_fab_icName).toString();
                    fab.setImageDrawable(new BitmapDrawable(qqContext.getResources(),FileUtil.getFabIcon(icon)));
                } else {
                    return true;
                }

            }
        }
        return true;
    }

    private List<FabInfo> getFabData(){
        String json = "";
        List<FabInfo> data = new ArrayList<>();
        File file = new File(FileUtil.getFabPath()+"config.json");
        if (file.exists()){
            try{
                String s = "";
                InputStream instream = new FileInputStream(file);
                if (instream != null){
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //逐行读取
                    while (( line = buffreader.readLine()) != null){
                        s = s + line;
                    }
                    instream.close();
                }
                json = s;
            }catch(Exception e){
                e.printStackTrace();
                json = FabButtonSettingActivity.json;
            }
        } else {
            json = FabButtonSettingActivity.json;


        }

        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FabInfo fabInfo = new FabInfo();
                fabInfo.title = jsonObject.getString("title");
                fabInfo.icon = jsonObject.getString("icon");
                fabInfo.onClick = jsonObject.getString("onClick");
                fabInfo.bottonSize = jsonObject.getInt("buttonSize");
                data.add(fabInfo);
            }
        }catch (Exception e){
            new AlertDialog.Builder(qqContext).setTitle("错误").setMessage(e.toString()).setPositiveButton("确定", null).create().show();
            XposedBridge.log(e.toString());
        }
        return data;
    }


    private void fragmentsHook(){

        XMethod.create(FrameFragment)
                .enable("tab_use")
                .parameterTypes(View.class, Class.class, View.class)
                .hook(new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        Class<?> clazz = (Class<?>) param.args[1];
                        if (ClassUtil.ReadinjoyTabFrame.equals(clazz.getName())) return null;
                        return XposedBridge.invokeOriginalMethod(param.method,param.thisObject,param.args);
                    }
                });

        XMethod.create(FrameFragment)
                .name("createTabContent")
                .hook(new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        ViewGroup viewGroup = (ViewGroup)param.getResult();
                        if (viewGroup!=null){
                            //initMainView(viewGroup,param);
                            int index = mTabHost.getCurrentTab();
                            if (XSettingUtils.getBoolean("tab_use")&&XSettingUtils.getBoolean("tab_use_viewpager")){
                                if (mainFrameLayouts.size() == 0){
                                    for (int i = 0;i < 3;i++){
                                        FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
                                        frameLayout.setBackgroundColor(Color.WHITE);
                                        mainFrameLayouts.put(i,frameLayout);
                                    }
                                }


                                FrameLayout frameLayout = mainFrameLayouts.get(index);
                                frameLayout.removeAllViews();
                                frameLayout.addView(viewGroup);
                                param.setResult(new View(viewGroup.getContext()));
                            }

                            if (index == 2 || index == 3){
                                //hideLebaSearch(viewGroup);
                            }
                        }
                    }
                });

        //禁用简洁模式QQ空间

    }


    private void hideLebaSearch(ViewGroup viewGroup){
        if (XSettingUtils.getBoolean("top_hideSearch")){
            viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    View view = viewGroup.findViewById(IdsUtil.et_search_keyword);
                    if (view != null){
                        View search = (View)view.getParent();
                        ViewGroup.LayoutParams layoutParams = search.getLayoutParams();
                        layoutParams.width = 0;
                        layoutParams.height = 0;
                        search.setLayoutParams(layoutParams);
                        viewGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    private void initDrawer(){
        if (XSettingUtils.getBoolean("tab_use_viewpager")){
            XposedBridge.hookAllConstructors(XposedHelpers.findClass(DrawerFrame, XposedCompact.classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    MainViewPager.sDrawerCache = new WeakReference<>(param.thisObject);
                }
            });
        }
    }

    @Override
    public void startHook() {
        initDrawer();
        addMainWidget();
        //fragmentsHook();
        hookTabChange();
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void onTabChanged(String tabId) {
        //禁用简洁模式QQ空间
        QQHelper.getContext().getSharedPreferences(QQHelper.getCurrentAccountUin() + "_QZ_QQ_shared_preference",0).edit().putInt("qzone_feed_gray_mask",0).apply();
        int position = mTabHost.getCurrentTab();
        if (tabLayout!=null && mainImageView!=null && mainTextView!=null){
            int tabPosition = tabLayout.getSelectedTabPosition();

            if (position != tabPosition){
                if (tabLayout.getTabAt(position)!=null){
                    tabLayout.getTabAt(position).select();
                }
            }

            if (!XSettingUtils.getBoolean("top_useSearch")){
                if (mainRightTopIcon.get(position)!=null){
                    mainImageView.setImageDrawable(mainRightTopIcon.get(position).newDrawable());
                } else {
                    mainImageView.setImageDrawable(null);
                }
            }

            mainTextView.setText(position==0?"消息":position==1?"联系人":"动态");
        }
    }

}

