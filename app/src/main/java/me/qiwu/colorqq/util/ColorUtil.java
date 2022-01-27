package me.qiwu.colorqq.util;

import android.graphics.Color;

public class ColorUtil {

    //获取颜色透明度
    public static int getAlpha(int color){
        return (color & 0xff000000) >>> 24;
    }

    public static String getColorWithOutAlpha(int color) {
        int r = (color & 0x00ff0000) >> 16;
        int g = (color & 0x0000ff00) >> 8;
        int b = (color & 0x000000ff);

        String rr = Integer.toHexString(r).length()==1? "0" + Integer.toHexString(r) : Integer.toHexString(r);
        String gg = Integer.toHexString(g).length()==1? "0" + Integer.toHexString(g) : Integer.toHexString(g);
        String bb = Integer.toHexString(b).length()==1? "0" + Integer.toHexString(b) : Integer.toHexString(b);
        return "#"+rr + gg + bb;

    }

    public static String getHexColor(int color){
        int a = (color & 0xff000000) >>> 24;
        int r = (color & 0x00ff0000) >> 16;
        int g = (color & 0x0000ff00) >> 8;
        int b = (color & 0x000000ff);

        String aa = Integer.toHexString(a).length()==1? "0" + Integer.toHexString(a) : Integer.toHexString(a);
        String rr = Integer.toHexString(r).length()==1? "0" + Integer.toHexString(r) : Integer.toHexString(r);
        String gg = Integer.toHexString(g).length()==1? "0" + Integer.toHexString(g) : Integer.toHexString(g);
        String bb = Integer.toHexString(b).length()==1? "0" + Integer.toHexString(b) : Integer.toHexString(b);
        return aa.toUpperCase() + rr.toUpperCase() + gg.toUpperCase() + bb.toUpperCase();
    }

    public static int getPressColr(int color) {
        if (getAlpha(color)<155){
            return Color.TRANSPARENT;
        }
        int r = (color & 0x00ff0000) >> 16;
        int g = (color & 0x0000ff00) >> 8;
        int b = (color & 0x000000ff);
        int a = (color & 0xff000000) >>> 24;

        String rr = Integer.toHexString(r).length()==1? "0" + Integer.toHexString(r) : Integer.toHexString(r);
        String gg = Integer.toHexString(g).length()==1? "0" + Integer.toHexString(g) : Integer.toHexString(g);
        String bb = Integer.toHexString(b).length()==1? "0" + Integer.toHexString(b) : Integer.toHexString(b);
        String aa = Integer.toHexString(a-155).length()==1? "0" + Integer.toHexString(a-155): Integer.toHexString(a-155);
        return Color.parseColor("#" + aa + rr + gg +bb);
    }

    public static boolean isColorLight(int color) {
        return getColorDarkness(color) < 0.5;
    }

    private static double getColorDarkness(int color) {
        if (color == Color.BLACK)
            return 1.0;
        else if (color == Color.WHITE || color == Color.TRANSPARENT)
            return 0.0;
        else
            return (1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255);
    }

    public static int getInverseColor(int color) {
        return (0xFFFFFF - color) | 0xFF000000;
    }

    public static boolean isColorSaturated( int color) {
        double max = Math.max(0.299 * Color.red(color), Math.max(0.587 * Color.green(color), 0.114 * Color.blue(color)));
        double min = Math.min(0.299 * Color.red(color), Math.min(0.587 * Color.green(color), 0.114 * Color.blue(color)));
        double diff = Math.abs(max - min);
        return diff > 20;
    }

    public static int getMixedColor(int color1,int color2) {
        return Color.rgb(
                (Color.red(color1) + Color.red(color2)) / 2,
                (Color.green(color1) + Color.green(color2)) / 2,
                (Color.blue(color1) + Color.blue(color2)) / 2
        );
    }

    public static double getDifference(int color1, int color2) {
        double diff = Math.abs(0.299 * (Color.red(color1) - Color.red(color2)));
        diff += Math.abs(0.587 * (Color.green(color1) - Color.green(color2)));
        diff += Math.abs(0.114 * (Color.blue(color1) - Color.blue(color2)));
        return diff;
    }


    public static int getReadableText(int textColor, int backgroundColor) {
        return getReadableText(textColor, backgroundColor, 100);
    }


    public static int getReadableText(int textColor,int backgroundColor, int difference) {
        boolean isLight = isColorLight(backgroundColor);
        for (int i = 0; getDifference(textColor, backgroundColor) < difference && i < 100; i++) {
            textColor = getMixedColor(textColor, isLight ? Color.BLACK : Color.WHITE);
        }

        return textColor;
    }
}
