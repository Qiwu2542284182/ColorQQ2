package me.qiwu.colorqq.theme;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SkinnableNinePatchDrawable extends Drawable {
    private static final boolean DEFAULT_DITHER = true;
    private int mBitmapHeight;
    private int mBitmapWidth;
    private boolean mMutated;
    private NinePatch mNinePatch;
    private NinePatchState mNinePatchState;
    private Rect mPadding;
    private Paint mPaint;
    private int mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;

    public static final byte[] sBaseChunk = {1, 2, 2, 1, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 94, 0, 0, 0, 0, 0, 0,
            0, 94, 0, 0, 0, 1, 0, 0, 0
    };

    static byte[] makeChunk(Bitmap bitmap) {
        byte[] bytes = new byte[sBaseChunk.length];
        System.arraycopy(sBaseChunk, 0, bytes, 0, sBaseChunk.length);
        ByteBuffer wrap = ByteBuffer.wrap(bytes);
        wrap.order(ByteOrder.LITTLE_ENDIAN);
        wrap.position(36);
        wrap.putInt(bitmap.getWidth());
        wrap.position(44);
        wrap.putInt(bitmap.getHeight());
        return bytes;
    }


    public SkinnableNinePatchDrawable(Resources resources,Bitmap bitmap,byte[] chunk,String srcName) {
        this(new NinePatchState(chunk == null || !NinePatch.isNinePatchChunk(chunk) ? null : new NinePatch(bitmap,chunk,srcName),bitmap, new Rect()), resources);
        if (mNinePatchState.mNinePatch == null){
            mNinePatchState.hasProblem = true;
            mNinePatchState.mNinePatch = new NinePatch(bitmap,makeChunk(bitmap),srcName);
        }
    }

    @Deprecated
    public SkinnableNinePatchDrawable(Bitmap bitmap, byte[] chunk, Rect padding, String srcName) {
        this(new NinePatchState(new NinePatch(bitmap, chunk, srcName), bitmap, padding), null);
    }

    public SkinnableNinePatchDrawable(Resources res, Bitmap bitmap, byte[] chunk, Rect padding, String srcName) {
        this(new NinePatchState(new NinePatch(bitmap, chunk, srcName), bitmap, padding), res);
        mNinePatchState.mTargetDensity = mTargetDensity;
    }

    private void setNinePatchState(NinePatchState ninePatchState, Resources resources) {
        mNinePatchState = ninePatchState;
        mNinePatch = ninePatchState.mNinePatch;
        mPadding = ninePatchState.mPadding;
        mTargetDensity = resources != null ? resources.getDisplayMetrics().densityDpi : ninePatchState.mTargetDensity;
        if (!ninePatchState.mDither) {
            setDither(ninePatchState.mDither);
        }
        if (mNinePatch != null) {
            computeBitmapSize();
        }
    }

    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    public void setTargetDensity(DisplayMetrics displayMetrics) {
        updateImage();
        mTargetDensity = displayMetrics.densityDpi;
        if (mNinePatch != null) {
            computeBitmapSize();
        }
    }

    public void setTargetDensity(int density) {
        updateImage();
        if (density == 0) {
            density = DisplayMetrics.DENSITY_DEFAULT;
        }
        mTargetDensity = density;
        if (mNinePatch != null) {
            computeBitmapSize();
        }
    }

    private void computeBitmapSize() {
        updateImage();
        if (mNinePatchState.mImageSizeWhenOOM != null) {
            int[] imageSizeWhenOOM = mNinePatchState.mImageSizeWhenOOM;
            mBitmapWidth = BaseConstantState.scaleFromDensity(imageSizeWhenOOM[0], imageSizeWhenOOM[2], mTargetDensity);
            mBitmapWidth = BaseConstantState.scaleFromDensity(imageSizeWhenOOM[1], imageSizeWhenOOM[2], mTargetDensity);
            mPadding.set(0, 0, 0, 0);
            return;
        }
        int density = mNinePatch.getDensity();
        if (mTargetDensity == density) {
            mBitmapWidth = mNinePatch.getWidth();
            mBitmapHeight = mNinePatch.getHeight();
        } else {
            if (mPadding == mNinePatchState.mPadding){
                mBitmapWidth = BaseConstantState.scaleFromDensity(mNinePatch.getWidth(), density, mTargetDensity);
                mBitmapHeight = BaseConstantState.scaleFromDensity(mNinePatch.getHeight(), density, mTargetDensity);
                mPadding = new Rect(mNinePatchState.mPadding);
            }
            mPadding.left = BaseConstantState.scaleFromDensity(mNinePatchState.mPadding.left, density, mTargetDensity);
            mPadding.top = BaseConstantState.scaleFromDensity(mNinePatchState.mPadding.top, density, mTargetDensity);
            mPadding.right = BaseConstantState.scaleFromDensity(mNinePatchState.mPadding.right, density, mTargetDensity);
            mPadding.bottom = BaseConstantState.scaleFromDensity(mNinePatchState.mPadding.bottom, density, mTargetDensity);
        }
    }

    private void updateImage() {
        if (mNinePatch != mNinePatchState.mNinePatch) {
            mNinePatch = mNinePatchState.mNinePatch;
            mPadding = mNinePatchState.mPadding;
            if (mNinePatchState.mImageSizeWhenOOM != null) {
                int[] imageSizeWhenOOM = mNinePatchState.mImageSizeWhenOOM;
                mBitmapWidth = BaseConstantState.scaleFromDensity(imageSizeWhenOOM[0], imageSizeWhenOOM[2], mTargetDensity);
                mBitmapWidth = BaseConstantState.scaleFromDensity(imageSizeWhenOOM[1], imageSizeWhenOOM[2], mTargetDensity);
                mPadding.set(0, 0, 0, 0);
                return;
            }
            int density = mNinePatch.getDensity();
            if (mTargetDensity == density) {
                mBitmapWidth = mNinePatch.getWidth();
                mBitmapHeight = mNinePatch.getHeight();
            } else {
                mBitmapWidth = BaseConstantState.scaleFromDensity(mNinePatch.getWidth(), density, mTargetDensity);
                mBitmapHeight = BaseConstantState.scaleFromDensity(mNinePatch.getHeight(), density, mTargetDensity);
                if (mPadding == mNinePatchState.mPadding){
                    mPadding = new Rect(mNinePatchState.mPadding);
                }
                mPadding.left = BaseConstantState.scaleFromDensity(mNinePatchState.mPadding.left, density, mTargetDensity);
                mPadding.top = BaseConstantState.scaleFromDensity(mNinePatchState.mPadding.top, density, mTargetDensity);
                mPadding.right = BaseConstantState.scaleFromDensity(mNinePatchState.mPadding.right, density, mTargetDensity);
                mPadding.bottom = BaseConstantState.scaleFromDensity(mNinePatchState.mPadding.bottom, density, mTargetDensity);

            }

        }
    }

    public void draw(Canvas canvas) {
        updateImage();
        if (mNinePatchState.mImageSizeWhenOOM == null) {
            Rect bounds = getBounds();
            try {
                mNinePatch.draw(canvas, bounds, mPaint);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mNinePatchState.hasProblem) {
                canvas.drawRect(bounds, BaseConstantState.sColorPaint);
                canvas.drawLine((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom, BaseConstantState.sPaint);
                canvas.drawLine((float) bounds.right, (float) bounds.top, (float) bounds.left, (float) bounds.bottom, BaseConstantState.sPaint);
            }
        }
    }

    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mNinePatchState.mChangingConfigurations;
    }

    public boolean getPadding(Rect rect) {
        updateImage();
        rect.set(mPadding);
        return DEFAULT_DITHER;
    }

    public boolean getOldPadding(Rect rect) {
        if (mNinePatchState.mOldPadding == null) {
            return false;
        }
        rect.set(mNinePatchState.mOldPadding);
        return DEFAULT_DITHER;
    }

    public void setAlpha(int alpha) {
        getPaint().setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        getPaint().setColorFilter(colorFilter);
    }

    public void setDither(boolean dither) {
        getPaint().setDither(dither);
    }

    public Paint getPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setDither(DEFAULT_DITHER);
        }
        return mPaint;
    }

    public int getIntrinsicWidth() {
        updateImage();
        return mBitmapWidth;
    }

    public int getIntrinsicHeight() {
        updateImage();
        return mBitmapHeight;
    }

    public int getMinimumWidth() {
        updateImage();
        return mBitmapWidth;
    }

    public int getMinimumHeight() {
        updateImage();
        return mBitmapHeight;
    }

    public int getOpacity() {
        updateImage();
        return (mNinePatch == null || mNinePatch.hasAlpha() || (mPaint != null && mPaint.getAlpha() < 255)) ? PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    public Region getTransparentRegion() {
        updateImage();
        if (mNinePatch == null) {
            return null;
        }
        return mNinePatch.getTransparentRegion(getBounds());
    }

    public ConstantState getConstantState() {
        mNinePatchState.mChangingConfigurations = super.getChangingConfigurations();
        return mNinePatchState;
    }

    public Drawable mutate() {
        return this;
    }

    public Bitmap getBitmap() {
        return mNinePatchState.mBitmap;
    }

    static final class NinePatchState extends BaseConstantState {
        Bitmap mBitmap;
        int mChangingConfigurations;
        boolean mDither;
        NinePatch mNinePatch;
        Rect mOldPadding;
        Rect mPadding;
        int mTargetDensity;

        NinePatchState(){

        }

        NinePatchState(NinePatch ninePatch, Bitmap bitmap, Rect padding) {
            this(ninePatch, bitmap, padding, SkinnableNinePatchDrawable.DEFAULT_DITHER);
        }

        NinePatchState(NinePatch ninePatch, Bitmap bitmap, Rect padding, boolean dither) {
            mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
            mBitmap = bitmap;
            mNinePatch = ninePatch;
            mPadding = padding;
            mDither = dither;
        }

        NinePatchState(NinePatchState ninePatchState) {
            mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
            mNinePatch = ninePatchState.mNinePatch;
            mPadding = ninePatchState.mPadding;
            mDither = ninePatchState.mDither;
            mChangingConfigurations = ninePatchState.mChangingConfigurations;
            mTargetDensity = ninePatchState.mTargetDensity;
        }

        public Drawable newDrawable() {
            return new SkinnableNinePatchDrawable(this,null);
        }

        public Drawable newDrawable(Resources resources) {
            return new SkinnableNinePatchDrawable(this, resources);
        }

        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }
    }

    SkinnableNinePatchDrawable(NinePatchState ninePatchState, Resources resources) {
        mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        setNinePatchState(ninePatchState, resources);
    }
}
