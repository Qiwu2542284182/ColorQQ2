package me.qiwu.colorqq.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import de.robv.android.xposed.XposedHelpers;
import me.qiwu.colorqq.XHook.XSettingUtils;
import me.qiwu.colorqq.XHook.XposedCompact;

import static me.qiwu.colorqq.util.ClassUtil.LebaSearchTransparentJumpActivity;
import static me.qiwu.colorqq.util.ClassUtil.QQBrowserActivity;
import static me.qiwu.colorqq.util.ClassUtil.UniteSearchActivity;


/**
 * Created by  on 2018/11/10.
 */


public class FunUtils {
    public static void qzone(Context context){
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqapi://qzone/activefeed?src_type=app&version=1.0&hydtgzh=11&puin="));
        context.startActivity(intent);
    }

    public static void exit(){
        Activity activity = QQHelper.getActivity();
        if (activity!=null){
            try {
                Object mainFragment = QQHelper.getFragmentByName(activity,"MainFragment");
                if (mainFragment!=null){
                    Dialog dialog = (Dialog)XposedHelpers.callMethod(mainFragment, "a", "你确定要退出QQ吗？", null, (DialogInterface.OnDismissListener) dialog1 -> { });
                    TextView dialogLeftBtn = dialog.findViewById(IdsUtil.dialogLeftBtn);
                    dialogLeftBtn.setOnClickListener(v -> dialog.dismiss());
                    TextView dialogRightBtn = dialog.findViewById(IdsUtil.dialogRightBtn);
                    dialogRightBtn.setOnClickListener(v -> System.exit(0));
                    CheckBox checkBox = dialog.findViewById(IdsUtil.checkBoxConfirm);
                    checkBox.setVisibility(View.GONE);
                    dialog.show();
                } else {
                    System.exit(0);
                }
            } catch (Throwable throwable){
                System.exit(0);
            }
        } else {
            System.exit(0);
        }

        //XposedHelpers.callStaticMethod(XposedHelpers.findClass(ClassUtil.AccountManageActivity,XposedCompact.classLoader),"a",QQHelper.getActivity(),QQHelper.getQQAppInterface());
    }

    public static void showDialog(String title,String msg,View.OnClickListener onClickListener){
        Activity activity = QQHelper.getActivity();
        if (activity!=null){
            try {
                Object fragmentManager = XposedHelpers.callMethod(activity,"getSupportFragmentManager");
                Object mainFragment = XposedHelpers.callMethod(fragmentManager,"findFragmentByTag",ClassUtil.MainFragment);
                if (mainFragment!=null){
                    Dialog dialog = (Dialog)XposedHelpers.callMethod(mainFragment, "a", title,msg, (DialogInterface.OnDismissListener) dialog1 -> { });
                    dialog.setCancelable(false);
                    TextView dialogLeftBtn = dialog.findViewById(IdsUtil.dialogLeftBtn);
                    dialogLeftBtn.setVisibility(View.GONE);
                    TextView dialogRightBtn = dialog.findViewById(IdsUtil.dialogRightBtn);
                    dialogRightBtn.setMaxLines(5);
                    dialogRightBtn.setOnClickListener(onClickListener);
                    CheckBox checkBox = dialog.findViewById(IdsUtil.checkBoxConfirm);
                    checkBox.setVisibility(View.GONE);
                    dialog.show();
                }
            } catch (Throwable throwable){
                throwable.printStackTrace();
            }
        }
    }

    public static void exitAccount(){
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(ClassUtil.AccountManageActivity, XposedCompact.classLoader),"a",QQHelper.getActivity(),QQHelper.getQQAppInterface());
    }


    public static void qlink(Context context){
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqapi://qlink/openqlink"));
        context.startActivity(intent);
    }

    public static void money(Context context){
            Intent intent = new Intent();
            intent.setData(Uri.parse("mqqapi://wallet/open?src_type=web&viewtype=0&version=1&view=10&entry=2&seq=0"));
            ((Activity)context).startActivityForResult(intent,-1);
    }


    public static void wallet(Context context){
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqapi://wallet/open?src_type=web&viewtype=0&version=1"));
        ((Activity)context).startActivityForResult(intent,-1);
    }

    public static void fav(Context context,ClassLoader classLoader){
        Intent intent = new Intent(context, XposedHelpers.findClass(LebaSearchTransparentJumpActivity,classLoader));
        intent.putExtra("key_business",4);
        context.startActivity(intent);
    }

    public static void photo(Context context,ClassLoader classLoader){
        Intent intent = new Intent(context, XposedHelpers.findClass(LebaSearchTransparentJumpActivity,classLoader));
        intent.putExtra("key_business",5);
        context.startActivity(intent);
    }



    public static void message(Context context){
        Intent intent = new Intent();
        intent.putExtra("tab_index",0);
        intent.setAction("com.tencent.mobileqq.action.MAINACTIVITY");
        context.startActivity(intent);
    }

    public static void contact(Context context){
        Intent intent = new Intent();
        intent.putExtra("tab_index",1);
        intent.setAction("com.tencent.mobileqq.action.MAINACTIVITY");
        context.startActivity(intent);
    }

    public static void readInjoy(Context context){
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqapi://readinjoy/open?src_type=internal&version=1&target=1"));
        context.startActivity(intent);
    }

    public static void leba(Context context){
        int position = XSettingUtils.getBoolean("tab_use") || !context.getSharedPreferences("readinjoy_sp_" + QQHelper.getCurrentAccountUin(),0).getBoolean("local_kd_tab_switch",true) ? 2 :3;
        Intent intent = new Intent();
        intent.putExtra("tab_index",position);
        intent.setAction("com.tencent.mobileqq.action.MAINACTIVITY");
        context.startActivity(intent);
    }

    public static void search(Context context,ClassLoader classLoader){
        Class<?> clazz = XposedHelpers.findClass(UniteSearchActivity,classLoader);
        XposedHelpers.callStaticMethod(clazz,"a",(Activity)context,null,2,2);

    }

    public static void jump(Context context,String activity,ClassLoader classLoader){
        Intent intent = new Intent(context, XposedHelpers.findClass(activity,classLoader));
        intent.putExtra("leftViewText", "返回");
        intent.putExtra("selfSet_leftViewText", "返回");
        intent.putExtra("EntranceId", 4);
        intent.putExtra("entrence_data_report",2);
        intent.putExtra("param_exit_animation", 0);
        intent.putExtra("create_source", 0);
        context.startActivity(intent);
    }

    public static void url(Context context,String url,ClassLoader classLoader){
        Intent intent = new Intent(context, XposedHelpers.findClass(QQBrowserActivity,classLoader));
        intent.putExtra("url",url);
        context.startActivity(intent);
    }


    public static void friend(Context context,String uin){
        Intent intent = new Intent();
        intent.putExtra("uin",uin);
        intent.putExtra("uintype",0);
        intent.setAction("com.tencent.mobileqq.action.CHAT");
        context.startActivity(intent);
    }

    public static void openApp(String appName){
        Context context = QQHelper.getBaseContext();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(appName, 0);
            Intent intent = new Intent("android.intent.action.MAIN", null);
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(packageInfo.packageName);
            ResolveInfo next = context.getPackageManager().queryIntentActivities(intent, 0).iterator().next();
            if (next != null) {
                String packageName = next.activityInfo.packageName;
                String name = next.activityInfo.name;
                Intent intent2 = new Intent("android.intent.action.MAIN");
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.addCategory("android.intent.category.LAUNCHER");
                intent2.setComponent(new ComponentName(packageName, name));
                context.startActivity(intent2);
            }
        }catch (Throwable throwable) {
            try {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(appName);
                context.startActivity(intent);
            } catch (Throwable throwable1){
                Toast.makeText(context, "启动" + appName + "失败", Toast.LENGTH_SHORT).show();
            }

        }
    }

}

