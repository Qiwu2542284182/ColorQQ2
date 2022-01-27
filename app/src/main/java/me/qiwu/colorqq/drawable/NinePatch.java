//
// Decompiled by Jadx - 1002ms
//
package me.qiwu.colorqq.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;

public class NinePatch {
    private static final int NO_COLOR = 1;
    public static final int SAVE_COMPILED = 2;
    public static final int SAVE_RAW = 0;
    public static final int SAVE_UNCOMPILED = 1;
    private static final String TAG = "NinePatch";
    public Bitmap mImage;
    public Rect mMargins = null;
    public LinkedList<Region> mXRegions = new LinkedList();
    public LinkedList<Region> mYRegions = new LinkedList();

    public NinePatch(Bitmap bitmap, boolean z) {
        byte[] ninePatchChunk = bitmap.getNinePatchChunk();
        if (ninePatchChunk != null && android.graphics.NinePatch.isNinePatchChunk(ninePatchChunk)) {
            loadCompiled(bitmap, ninePatchChunk);
        } else if (z) {
            loadUncompiled(bitmap);
            Log.v(TAG, "Loaded nine patch with " + this.mXRegions.size() + " horizontal and " + this.mYRegions.size() + " vertical regions");
        } else {
            this.mImage = bitmap;
        }
    }


    private void addRegionsFromMask(int[] iArr, boolean z, LinkedList<Region> linkedList) {
        int i = 0;
        if (iArr[0] != 0) {
            i = 1;
        }
        int i2 = 0;
        for (int i3 = 1; i3 < iArr.length; i3++) {
            if (i != 0) {
                if (iArr[i3] == 0) {
                    Log.v(TAG, "Found new region with bounds (" + i2 + "," + (i3 - 1) + ")");
                    linkedList.add(new Region(this, z, i2, i3 - 1));
                    i = 0;
                }
            } else if (iArr[i3] != 0) {
                i2 = i3;
                i = 1;
            }
        }
        if (i != 0) {
            Log.v(TAG, "Found new region with bounds (" + i2 + "," + iArr.length + ")");
            linkedList.add(new Region(this, z, i2, iArr.length));
        }
    }

    private static void checkDivCount(int i) {
        if (i == 0 || (i & 1) != 0) {
            throw new RuntimeException("invalid nine-patch: " + i);
        }
    }

    private void checkMargins() {
        if (this.mMargins != null) {
            this.mMargins.left = Math.min(Math.max(this.mMargins.left, 0), getWidth());
            this.mMargins.right = Math.min(Math.max(this.mMargins.right, 0), getWidth());
            this.mMargins.top = Math.min(Math.max(this.mMargins.top, 0), getHeight());
            this.mMargins.bottom = Math.min(Math.max(this.mMargins.bottom, 0), getHeight());
        }
    }

    public static int[] createColorsArray(List<Region> list, List<Region> list2, int i, int i2) {
        int[] iArr = new int[(getRegions(list, i) * getRegions(list2, i2))];
        Arrays.fill(iArr, 1);
        return iArr;
    }

    private static int getRegions(List<Region> list, int i) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < list.size(); i3++) {
            Region region = (Region) list.get(i3);
            if (i3 == 0 && region.mLowerBound != 0) {
                i2++;
            }
            if (i3 > 0) {
                i2++;
            }
            i2++;
            if (i3 == list.size() - 1 && region.mUpperBound < i) {
                i2++;
            }
        }
        return i2;
    }

    public void loadCompiled(Bitmap bitmap, byte[] bArr) {
        Log.d(TAG, "Compiled");
        ByteBuffer order = ByteBuffer.wrap(bArr).order(ByteOrder.nativeOrder());
        byte b = order.get();
        byte b2 = order.get();
        byte b3 = order.get();
        int[] iArr = new int[order.get()];
        checkDivCount(b2);
        checkDivCount(b3);
        order.getInt();
        order.getInt();
        this.mMargins = new Rect(order.getInt(), order.getInt(), order.getInt(), order.getInt());
        order.getInt();
        readRegions(true, b2, order);
        readRegions(false, b3, order);
        readIntArray(iArr, order);
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int[] iArr2 = new int[(height * width)];
        bitmap.getPixels(iArr2, 0, width, 0, 0, width, height);
        this.mImage = Bitmap.createBitmap(iArr2, width, height, Config.ARGB_8888);
    }

    private void loadUncompiled(Bitmap bitmap) {
        Log.d(TAG, "Uncompiled");
        int width = bitmap.getWidth() - 2;
        int height = bitmap.getHeight() - 2;
        if (width <= 0 || height <= 0) {
            Log.e(TAG, "Image is too small to be a nine patch image.");
            this.mImage = bitmap;
            return;
        }
        int[] iArr = new int[(height * width)];
        bitmap.getPixels(iArr, 0, width, 1, 1, width, height);
        this.mImage = Bitmap.createBitmap(iArr, width, height, Config.ARGB_8888);
        int[] iArr2 = new int[width];
        int[] iArr3 = new int[height];
        bitmap.getPixels(iArr2, 0, width, 1, 0, width, 1);
        bitmap.getPixels(iArr3, 0, 1, 0, 1, 1, height);
        addRegionsFromMask(iArr2, true, this.mXRegions);
        addRegionsFromMask(iArr3, false, this.mYRegions);
        bitmap.getPixels(iArr2, 0, width, 1, height + 1, width, 1);
        bitmap.getPixels(iArr3, 0, 1, width + 1, 1, 1, height);
        setMargins(iArr2, iArr3);
    }

    public void sortRegions() {
        Region region = new Region(this,true,0,0);
        region.mLowerBound = region.getMax()/2;
        region.mUpperBound = region.mLowerBound + 2;
        mXRegions.add(region);
        Region region1 = new Region(this,false,0,0);
        region1.mLowerBound = region.getMax()/2;
        region1.mUpperBound = region.mLowerBound + 2;
        mYRegions.add(region1);
        sortRegions(mXRegions);
        sortRegions(mYRegions);
    }


    private void sortRegions(LinkedList<Region> linkedList) {
        Collections.sort(linkedList, new Comparator<Region>() {
            @Override
            public int compare(Region region, Region region2) {
                return region.mLowerBound - region2.mLowerBound;
            }
        });
        Region region = null;
        Iterator it = ((LinkedList) linkedList.clone()).iterator();
        while (it.hasNext()) {
            Region region2 = (Region) it.next();
            if (region2.mLowerBound > region2.mUpperBound) {
                linkedList.remove(region2);
            } else if (region == null || region2.mLowerBound > region.mUpperBound) {
                region = region2;
            } else {
                region.mUpperBound = Math.max(region.mUpperBound, region2.mUpperBound);
                linkedList.remove(region2);
            }
        }
    }


    private Bitmap makeBitmap() {
        int width = getWidth() + 2;
        int height = getHeight() + 2;
        int[] iArr = new int[(height * width)];
        this.mImage.getPixels(iArr, width + 1, width, 0, 0, width - 2, height - 2);
        Iterator it = this.mXRegions.iterator();
        Region region = null;
        int max;
        while (it.hasNext()) {
            region = (Region) it.next();
            max = Math.max(region.mLowerBound, 0);
            while (max <= region.mUpperBound && max < width - 2) {
                iArr[max + 1] = -16777216;
                max++;
            }
        }
        it = this.mYRegions.iterator();
        while (it.hasNext()) {
            region = (Region) it.next();
            max = Math.max(region.mLowerBound, 0);
            while (max <= region.mUpperBound && max < height - 2) {
                iArr[(max + 1) * width] = -16777216;
                max++;
            }
        }
        if (this.mMargins != null) {
            checkMargins();
            max = this.mMargins.left;
            while (max < getWidth() - this.mMargins.right && max < width - 2) {
                iArr[(((height - 1) * width) + 1) + max] = -16777216;
                max++;
            }
            max = this.mMargins.top;
            while (max < getHeight() - this.mMargins.bottom && max < height - 2) {
                iArr[((max * width) + (width * 2)) - 1] = -16777216;
                max++;
            }
        }
        return Bitmap.createBitmap(iArr, width, height, Config.ARGB_8888);
    }

    private static void readIntArray(int[] iArr, ByteBuffer byteBuffer) {
        int length = iArr.length;
        for (int i = 0; i < length; i++) {
            iArr[i] = byteBuffer.getInt();
        }
    }

    private void readRegions(boolean z, int i, ByteBuffer byteBuffer) {
        int i2 = i / 2;
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = byteBuffer.getInt();
            int i5 = byteBuffer.getInt() - 1;
            (z ? this.mXRegions : this.mYRegions).add(new Region(this, z, i4, i5));
            Log.v(TAG, "Added region: (" + i4 + "," + i5 + ")");
        }
    }

    private void setMargins(int[] iArr, int[] iArr2) {
        int i;
        int width = getWidth() - 1;
        int height = getHeight() - 1;
        int i2 = -1;
        int i3 = -1;
        for (i = 0; i <= height; i++) {
            if (iArr2[i] != 0) {
                i2 = i;
                break;
            }
        }
        for (i = 0; i <= width; i++) {
            if (iArr[i] != 0) {
                i3 = i;
                break;
            }
        }
        if (i3 == -1 && i2 == -1) {
            this.mMargins = null;
            return;
        }
        int i4;
        int i5;
        if (i2 == -1) {
            i2 = 0;
            i4 = 0;
        } else {
            i4 = i2;
            i = i2 + 1;
            while (i <= height && iArr2[i] != 0) {
                i4 = i;
                i++;
            }
        }
        if (i3 == -1) {
            i3 = 0;
            i5 = 0;
        } else {
            i5 = i3;
            i = i3 + 1;
            while (i <= width && iArr[i] != 0) {
                i5 = i;
                i++;
            }
        }
        this.mMargins = new Rect(i3, i2, width - i5, height - i4);
    }

    private void writeFile(File file, Bitmap bitmap, int i) throws IOException {
        OutputStream byteArrayOutputStream = null;
        if (i == 2) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byte[] toByteArray = ((ByteArrayOutputStream)byteArrayOutputStream).toByteArray();
            byte[] chunk = getChunk(ByteOrder.BIG_ENDIAN);
            byte[] array = ByteBuffer.allocate(4).putInt(chunk.length).array();
            byte[] array2 = ByteBuffer.allocate(4).put(new byte[]{(byte) 110, (byte) 112, (byte) 84, (byte) 99}).array();
            CRC32 crc32 = new CRC32();
            crc32.update(array2);
            crc32.update(chunk);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(toByteArray, 0, toByteArray.length - 12);
            fileOutputStream.write(array);
            fileOutputStream.write(array2);
            fileOutputStream.write(chunk);
            fileOutputStream.write(ByteBuffer.allocate(4).putInt((int) crc32.getValue()).array());
            fileOutputStream.write(toByteArray, toByteArray.length - 12, 12);
            fileOutputStream.flush();
            fileOutputStream.close();
            return;
        }
        byteArrayOutputStream = new FileOutputStream(file);
        bitmap.compress(CompressFormat.PNG, 100, byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
    }

    private static void writeRegions(LinkedList<Region> linkedList, ByteBuffer byteBuffer) {
        Iterator it = linkedList.iterator();
        while (it.hasNext()) {
            Region region = (Region) it.next();
            byteBuffer.putInt(region.mLowerBound);
            byteBuffer.putInt(region.mUpperBound + 1);
        }
    }

    public boolean areMarginsOverlapped() {
        return this.mMargins.left + this.mMargins.right > getWidth() || this.mMargins.top + this.mMargins.bottom > getHeight();
    }

    public boolean areMarginsUnset() {
        return this.mMargins == null;
    }

    public byte[] getChunk() {
        return getChunk(ByteOrder.nativeOrder());
    }

    public byte[] getChunk(ByteOrder byteOrder) {
        int i = 0;
        int[] createColorsArray = createColorsArray(this.mXRegions, this.mYRegions, getWidth(), getHeight());
        int size = this.mXRegions.size() * 2;
        int size2 = this.mYRegions.size() * 2;
        ByteBuffer order = ByteBuffer.allocate((((size + size2) + createColorsArray.length) * 4) + 32).order(byteOrder);
        order.put((byte) 1);
        order.put((byte) size);
        order.put((byte) size2);
        order.put((byte) createColorsArray.length);
        order.putInt(0);
        order.putInt(0);
        if (this.mMargins == null) {
            order.putInt(0);
            order.putInt(0);
            order.putInt(0);
            order.putInt(0);
        } else {
            order.putInt(this.mMargins.left);
            order.putInt(this.mMargins.right);
            order.putInt(this.mMargins.top);
            order.putInt(this.mMargins.bottom);
        }
        order.putInt(0);
        writeRegions(this.mXRegions, order);
        writeRegions(this.mYRegions, order);
        int length = createColorsArray.length;
        while (i < length) {
            order.putInt(createColorsArray[i]);
            i++;
        }
        return order.array();
    }

    public int getHeight() {
        return this.mImage.getHeight();
    }

    public float[] getMarginsAsPoints() {
        if (this.mMargins == null) {
            return new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        }
        return new float[]{(float) this.mMargins.left, (float) this.mMargins.top, (float) (getWidth() - this.mMargins.right), (float) (getHeight() - this.mMargins.bottom)};
    }

    public NinePatchDrawable getNinePatchDrawable(Resources resources) {
        Rect rect = this.mMargins;
        if (rect == null) {
            Region region;
            rect = new Rect();
            if (!this.mXRegions.isEmpty()) {
                region = (Region) this.mXRegions.getFirst();
                rect.left = region.mLowerBound;
                rect.right = (getWidth() - 1) - region.mUpperBound;
            }
            if (!this.mYRegions.isEmpty()) {
                region = (Region) this.mYRegions.getFirst();
                rect.top = region.mLowerBound;
                rect.bottom = (getHeight() - 1) - region.mUpperBound;
            }
        }
        return new NinePatchDrawable(resources, this.mImage, getChunk(), rect, null);
    }

    public int getWidth() {
        return this.mImage.getWidth();
    }

    public void resetMargins() {
        this.mMargins = new Rect();
    }

    public void saveToFile(File file, int i) throws IOException {
        Bitmap makeBitmap;
        switch (i) {
            case 1:
                makeBitmap = makeBitmap();
                break;
            default:
                makeBitmap = this.mImage;
                break;
        }
        writeFile(file, makeBitmap, i);
    }

    public void unsetMargins() {
        this.mMargins = null;
    }
    public class Region {
        public boolean mIsHorizontal;
        public int mLowerBound = 0;
        public int mUpperBound = 0;
        final /* synthetic */ NinePatch this$0;

        public Region(NinePatch ninePatch, boolean z, int i, int i2) {
            this.this$0 = ninePatch;
            this.mIsHorizontal = z;
            this.mUpperBound = i2;
            this.mLowerBound = i;
        }

        public int getMax() {
            return this.mIsHorizontal ? this.this$0.getWidth() : this.this$0.getHeight();
        }
    }

}

