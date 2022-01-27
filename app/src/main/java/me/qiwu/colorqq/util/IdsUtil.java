package me.qiwu.colorqq.util;

import android.content.Context;
import android.content.res.Resources;



/**
 * Created by  on 2018/12/11.
 */


public class IdsUtil {
    public static int ivTitleBtnRightImage;
    public static int ivTitleBtnLeft;
    public static int ivTitleBtnRightCall;
    public static int recent_chat_list;
    public static int skin_chat_background;
    public static int skin_header_bar_bg;
    public static int skin_header_bar_bg2;
    public static int rlCommenTitle;
    public static int et_search_keyword;
    public static int root;
    public static int skin_color_title_immersive_bar;
    public static int input;
    public static int inputBar;
    public static int ivTitleName;
    public static int title_bar_height;
    public static int MenuDialogStyle;
    public static int search_box;
    public static int relativeItem;
    public static int icon;
    public static int dialogRightBtn;
    public static int dialogLeftBtn;
    public static int checkBoxConfirm;
    public static int main;
    public static int account_switch;
    public static int title;
    public static int title_sub;
    public static int fun_btn;
    public static int head_layout;
    public static int richstatus_txt;
    public static int listView1;


    static {
        Context context = QQHelper.getContext();
        Resources resources = context.getResources();
        String packageName = context.getPackageName();
        ivTitleBtnRightImage = resources.getIdentifier("ivTitleBtnRightImage","id",packageName);
        ivTitleBtnRightCall = resources.getIdentifier("ivTitleBtnRightCall","id",packageName);
        ivTitleBtnLeft = resources.getIdentifier("ivTitleBtnLeft","id",packageName);
        recent_chat_list = resources.getIdentifier("recent_chat_list","id",packageName);
        search_box = resources.getIdentifier("search_box","id",packageName);
        skin_chat_background = resources.getIdentifier("skin_chat_background","drawable",packageName);
        skin_header_bar_bg = resources.getIdentifier("skin_header_bar_bg","drawable",packageName);
        skin_header_bar_bg2 = skin_header_bar_bg + 1;
        rlCommenTitle = resources.getIdentifier("rlCommenTitle","id",packageName);
        et_search_keyword = resources.getIdentifier("et_search_keyword","id",packageName);
        root = resources.getIdentifier("root","id",packageName);
        skin_color_title_immersive_bar = resources.getIdentifier("skin_color_title_immersive_bar","color",packageName);
        input = resources.getIdentifier("input","id",packageName);
        inputBar = resources.getIdentifier("inputBar","id",packageName);
        ivTitleName = resources.getIdentifier("ivTitleName","id",packageName);
        title_bar_height = resources.getIdentifier("title_bar_height","dimen",packageName);
        MenuDialogStyle = resources.getIdentifier("MenuDialogStyle","style",packageName);
        relativeItem = resources.getIdentifier("relativeItem","id",packageName);
        icon = resources.getIdentifier("icon","id",packageName);
        dialogLeftBtn = resources.getIdentifier("dialogLeftBtn","id",packageName);
        dialogRightBtn = resources.getIdentifier("dialogRightBtn","id",packageName);
        checkBoxConfirm = resources.getIdentifier("checkBoxConfirm","id",packageName);
        main = resources.getIdentifier("main","id",packageName);
        account_switch = resources.getIdentifier("account_switch","id",packageName);
        title = resources.getIdentifier("title","id",packageName);
        title_sub = resources.getIdentifier("title_sub","id",packageName);
        fun_btn = resources.getIdentifier("fun_btn","id",packageName);
        head_layout = resources.getIdentifier("head_layout","id",packageName);
        richstatus_txt = resources.getIdentifier("richstatus_txt","id",packageName);
        listView1 = resources.getIdentifier("listView1","id",packageName);
    }



}
