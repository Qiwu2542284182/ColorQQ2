package me.qiwu.colorqq.theme;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public abstract class BaseConstantState extends Drawable.ConstantState {
    static final int INDEX_DENSITY = 2;
    static final int INDEX_HEIGHT = 1;
    static final int INDEX_WIDTH = 0;
    static final Paint sColorPaint = new Paint();
    static final Paint sPaint = new Paint();
    boolean hasProblem;
    int[] mImageSizeWhenOOM;
    public static String THEME_PATH = null;

    static {
        sPaint.setColor(0xffff0000);
        sPaint.setStrokeWidth(4.0f);
        sColorPaint.setColor(0x50ff0000);
    }

    public static int scaleFromDensity(int size, int sDensity, int tDensity) {
        return (sDensity == 0 || tDensity == 0 || sDensity == tDensity) ? size : ((size * tDensity) + (sDensity >> 1)) / sDensity;
    }
}
