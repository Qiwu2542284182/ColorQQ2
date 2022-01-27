package me.qiwu.colorqq.theme;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;

public class SkinnableBitmapDrawable extends Drawable {
    private static final int[] BitmapDrawable = {16843033, 16843034, 16843035, 16843036, 16842927, 16843265};
    private static final int DEFAULT_PAINT_FLAGS = 6;
    private boolean mApplyGravity;
    private Bitmap mBitmap;
    private int mBitmapHeight;
    private BitmapState mBitmapState;
    private int mBitmapWidth;
    private final Rect mDstRect;
    private boolean mMutated;
    private boolean mRebuildShader;
    private int mTargetDensity;

    SkinnableBitmapDrawable() {
        mDstRect = new Rect();
        mBitmapState = new BitmapState((Bitmap) null);
    }

    public SkinnableBitmapDrawable(Resources resources) {
        mDstRect = new Rect();
        mBitmapState = new BitmapState((Bitmap) null);
        mBitmapState.mTargetDensity = mTargetDensity;
    }

    @Deprecated
    public SkinnableBitmapDrawable(Bitmap bitmap) {
        this(new BitmapState(bitmap), null);
    }

    public SkinnableBitmapDrawable(Resources resources, Bitmap bitmap) {
        this(new BitmapState(bitmap), resources);
        mBitmapState.mTargetDensity = mTargetDensity;
    }

    @Deprecated
    public SkinnableBitmapDrawable(String filePath) {
        this(new BitmapState(BitmapFactory.decodeFile(filePath)), null);
        if (mBitmap == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + filePath);
        }
    }

    public SkinnableBitmapDrawable(Resources resources, String filePath) {
        this(new BitmapState(BitmapFactory.decodeFile(filePath)), null);
        mBitmapState.mTargetDensity = mTargetDensity;
        if (mBitmap == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + filePath);
        }
    }

    @Deprecated
    public SkinnableBitmapDrawable(InputStream inputStream) {
        this(new BitmapState(BitmapFactory.decodeStream(inputStream)), null);
        if (mBitmap == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + inputStream);
        }
    }

    public SkinnableBitmapDrawable(Resources resources, InputStream inputStream) {
        this(new BitmapState(BitmapFactory.decodeStream(inputStream)), null);
        mBitmapState.mTargetDensity = mTargetDensity;
        if (mBitmap == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + inputStream);
        }
    }

    public final Paint getPaint() {
        return mBitmapState.mPaint;
    }

    public final Bitmap getBitmap() {
        updateBitmap();
        return mBitmap;
    }

    private void computeBitmapSize() {
        updateBitmap();
        if (mBitmapState.mImageSizeWhenOOM != null) {
            int[] iArr = mBitmapState.mImageSizeWhenOOM;
            mBitmapWidth = BaseConstantState.scaleFromDensity(iArr[0], iArr[2], mTargetDensity);
            mBitmapWidth = BaseConstantState.scaleFromDensity(iArr[1], iArr[2], mTargetDensity);
            return;
        }
        mBitmapWidth = mBitmap.getScaledWidth(mTargetDensity);
        mBitmapHeight = mBitmap.getScaledHeight(mTargetDensity);
    }

    private void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        if (bitmap != null) {
            computeBitmapSize();
            return;
        }
        mBitmapHeight = -1;
        mBitmapWidth = -1;
    }

    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    public void setTargetDensity(DisplayMetrics displayMetrics) {
        mTargetDensity = displayMetrics.densityDpi;
        updateBitmap();
        if (mBitmap != null) {
            computeBitmapSize();
        }
    }

    public void setTargetDensity(int density) {
        if (density == 0) {
            density = DisplayMetrics.DENSITY_DEFAULT;
        }
        mTargetDensity = density;
        updateBitmap();
        if (mBitmap != null) {
            computeBitmapSize();
        }
    }

    public int getGravity() {
        return mBitmapState.mGravity;
    }

    public void setGravity(int gravity) {
        mBitmapState.mGravity = gravity;
        mApplyGravity = true;
    }

    public void setAntiAlias(boolean antiAlias) {
        mBitmapState.mPaint.setAntiAlias(antiAlias);
    }

    public void setFilterBitmap(boolean filterBitmap) {
        mBitmapState.mPaint.setFilterBitmap(filterBitmap);
    }

    public void setDither(boolean dither) {
        mBitmapState.mPaint.setDither(dither);
    }

    public Shader.TileMode getTileModeX() {
        return mBitmapState.mTileModeX;
    }

    public Shader.TileMode getTileModeY() {
        return mBitmapState.mTileModeY;
    }

    public void setTileModeX(Shader.TileMode tileModeX) {
        setTileModeXY(tileModeX, mBitmapState.mTileModeY);
    }

    public final void setTileModeY(Shader.TileMode tileModeY) {
        setTileModeXY(mBitmapState.mTileModeX, tileModeY);
    }

    public void setTileModeXY(Shader.TileMode tileModeX, Shader.TileMode tileModeY) {
        BitmapState bitmapState = mBitmapState;
        if (bitmapState.mPaint.getShader() == null || bitmapState.mTileModeX != tileModeX || bitmapState.mTileModeY != tileModeY) {
            bitmapState.mTileModeX = tileModeX;
            bitmapState.mTileModeY = tileModeY;
            mRebuildShader = true;
        }
    }

    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mBitmapState.mChangingConfigurations;
    }


    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        mApplyGravity = true;
    }

    private void updateBitmap() {
        if (mBitmap != mBitmapState.mBitmap) {
            mBitmap = mBitmapState.mBitmap;
            if (mBitmapState.mBuildFromXml) {
                mRebuildShader = true;
                mApplyGravity = true;
            }
            if (mBitmapState.mImageSizeWhenOOM != null) {
                int[] sizeWhenOOM = mBitmapState.mImageSizeWhenOOM;
                mBitmapWidth = BaseConstantState.scaleFromDensity(sizeWhenOOM[0], sizeWhenOOM[2], mTargetDensity);
                mBitmapWidth = BaseConstantState.scaleFromDensity(sizeWhenOOM[1], sizeWhenOOM[2], mTargetDensity);
                return;
            }
            mBitmapWidth = mBitmap.getScaledWidth(mTargetDensity);
            mBitmapHeight = mBitmap.getScaledHeight(mTargetDensity);
        }
    }

    public void draw(Canvas canvas) {
        Bitmap bitmap;
        updateBitmap();
        if (mBitmapState.mImageSizeWhenOOM != null || (bitmap = mBitmap) == null) {
            return;
        }
        BitmapState bitmapState = mBitmapState;
        if (mRebuildShader) {
            Shader.TileMode tmx = bitmapState.mTileModeX;
            Shader.TileMode tmy = bitmapState.mTileModeY;
            if (tmx == null && tmy == null) {
                bitmapState.mPaint.setShader(null);
            } else {
                if (tmx == null) {
                    tmx = Shader.TileMode.CLAMP;
                }
                if (tmy == null) {
                    tmy = Shader.TileMode.CLAMP;
                }
                bitmapState.mPaint.setShader(new BitmapShader(bitmap, tmx, tmy));
            }
            mRebuildShader = false;
            copyBounds(mDstRect);
        }
        if (bitmapState.mPaint.getShader() == null) {
            if (mApplyGravity) {
                Gravity.apply(bitmapState.mGravity, mBitmapWidth, mBitmapHeight, getBounds(), mDstRect);
                mApplyGravity = false;
            }
            canvas.drawBitmap(bitmap, null, mDstRect, bitmapState.mPaint);
            return;
        }
        if (mApplyGravity) {
            mDstRect.set(getBounds());
            mApplyGravity = false;
        }
        canvas.drawRect(mDstRect, bitmapState.mPaint);
    }

    public void setAlpha(int alpha) {
        mBitmapState.mPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        mBitmapState.mPaint.setColorFilter(colorFilter);
    }

    public Drawable mutate() {
        return this;
    }


    public int getIntrinsicWidth() {
        updateBitmap();
        return mBitmapWidth;
    }

    public int getIntrinsicHeight() {
        updateBitmap();
        return mBitmapHeight;
    }

    public int getOpacity() {
        if (mBitmapState.mGravity != Gravity.FILL) {
            return PixelFormat.TRANSLUCENT;
        }
        updateBitmap();
        Bitmap bitmap = mBitmap;
        if (bitmap == null || bitmap.hasAlpha() || mBitmapState.mPaint.getAlpha() < 255) {
            return PixelFormat.TRANSLUCENT;
        }
        return PixelFormat.OPAQUE;
    }

    public final ConstantState getConstantState() {
        return mBitmapState;
    }

    static final class BitmapState extends BaseConstantState {
        Bitmap mBitmap;
        boolean mBuildFromXml;
        int mChangingConfigurations;
        int mGravity;
        Paint mPaint;
        int mTargetDensity;
        Shader.TileMode mTileModeX;
        Shader.TileMode mTileModeY;

        BitmapState(Bitmap bitmap) {
            mGravity = Gravity.FILL;
            mPaint = new Paint(6);
            mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
            mBuildFromXml = false;
            mBitmap = bitmap;
        }

        BitmapState(BitmapState bitmapState) {
            this(bitmapState.mBitmap);
            mChangingConfigurations = bitmapState.mChangingConfigurations;
            mGravity = bitmapState.mGravity;
            mTileModeX = bitmapState.mTileModeX;
            mTileModeY = bitmapState.mTileModeY;
            mTargetDensity = bitmapState.mTargetDensity;
            mPaint = new Paint(bitmapState.mPaint);
        }

        public Drawable newDrawable() {
            return new SkinnableBitmapDrawable(this, null);
        }

        public Drawable newDrawable(Resources resources) {
            return new SkinnableBitmapDrawable(this, resources);
        }

        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }
    }

    SkinnableBitmapDrawable(BitmapState bitmapState, Resources resources) {
        mDstRect = new Rect();
        mBitmapState = bitmapState;
        if (resources != null) {
            mTargetDensity = resources.getDisplayMetrics().densityDpi;
        } else if (bitmapState != null) {
            mTargetDensity = bitmapState.mTargetDensity;
        } else {
            mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        }
        setBitmap(mBitmap);
        if (bitmapState.mBuildFromXml) {
            mRebuildShader = true;
            mApplyGravity = true;
        }
    }
}
