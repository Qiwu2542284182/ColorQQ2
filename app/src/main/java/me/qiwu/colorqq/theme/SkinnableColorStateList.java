package me.qiwu.colorqq.theme;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Parcel;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import me.qiwu.colorqq.library.axml.AxmlPrinter.AXmlResourceParser;

public class SkinnableColorStateList extends ColorStateList {
    public static final Creator<ColorStateList> CREATOR = new Creator<ColorStateList>() {
        @Override
        public ColorStateList createFromParcel(Parcel parcel) {
            int readInt = parcel.readInt();
            int[][] iArr = new int[readInt][];
            for (int i = 0; i < readInt; i++) {
                iArr[i] = parcel.createIntArray();
            }
            return new ColorStateList(iArr, parcel.createIntArray());

        }

        @Override
        public ColorStateList[] newArray(int size) {
            return new ColorStateList[size];
        }
    };
    private static final int[][] TMP = new int[0][];
    private int[] mColors;
    private int mDefaultColor = -65536;
    private int[][] mStateSpecs;

    private static int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++)
            if (need <= (1 << i) - 12){
                return (1 << i) - 12;
            }
        return need;
    }

    public int describeContents() {
        return 0;
    }

    public boolean isStateful() {
        return true;
    }


    public SkinnableColorStateList(int[][] iArr, int[] iArr2) {
        super(TMP, null);
        this.mStateSpecs = iArr;
        this.mColors = iArr2;
        if (iArr != null && iArr.length > 0) {
            int i = 0;
            this.mDefaultColor = iArr2[0];
            while (i < iArr.length) {
                if (iArr[i].length == 0) {
                    this.mDefaultColor = iArr2[i];
                }
                i++;
            }
        }
    }

    public static SkinnableColorStateList createFromFile(Resources resources, InputStream inputStream) throws IOException, XmlPullParserException {
        AXmlResourceParser aXmlResourceParser = new AXmlResourceParser();
        aXmlResourceParser.open(inputStream);
        SkinnableColorStateList skinnableColorStateList = createFromXml(resources,aXmlResourceParser,true);
        inputStream.close();
        //aXmlResourceParser.close();
        return skinnableColorStateList;
    }

    public static SkinnableColorStateList createFromXml( Resources resources, XmlPullParser parser,boolean z) throws XmlPullParserException, IOException {
        AttributeSet attrs = Xml.asAttributeSet(parser);
        int type;
        while ((type=parser.next()) != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) {

            //do nothing
        }
        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }
        return createFromXmlInner(resources,parser,attrs);
    }


    private static SkinnableColorStateList createFromXmlInner( Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        String name = xmlPullParser.getName();
        if (name.equals("selector")) {
            SkinnableColorStateList skinnableColorStateList = new SkinnableColorStateList((int[][]) null, null);
            skinnableColorStateList.inflate(resources, xmlPullParser, attributeSet);
            return skinnableColorStateList;
        }
        throw new XmlPullParserException(resources.toString());
    }


    public ColorStateList withAlpha(int i) {
        int[] iArr = new int[this.mColors.length];
        int length = iArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            iArr[i2] = (this.mColors[i2] & 16777215) | (i << 24);
        }
        return new ColorStateList(this.mStateSpecs, iArr);
    }

    private void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        int type;
        final int innerDepth = parser.getDepth()+1;
        int depth;
        int listAllocated = 20;
        int listSize = 0;
        int[] colorList = new int[listAllocated];
        int[][] stateSpecList = new int[listAllocated][];
        while ((type=parser.next()) != XmlPullParser.END_DOCUMENT && ((depth=parser.getDepth()) >= innerDepth || type != XmlPullParser.END_TAG)) {
            if (type != XmlPullParser.START_TAG) {
                continue;
            }
            if (depth > innerDepth || !parser.getName().equals("item")) {
                continue;
            }
            int colorRes = 0;
            int color = 0xffff0000;
            boolean haveColor = false;
            int i;
            int j = 0;
            final int numAttrs = attrs.getAttributeCount();
            int[] stateSpec = new int[numAttrs];
            for (i = 0; i < numAttrs; i++) {
                final int stateResId = attrs.getAttributeNameResource(i);
                if (stateResId == 0) break;
                if (stateResId == android.R.attr.color) {
                    colorRes = attrs.getAttributeResourceValue(i, 0);
                    if (colorRes == 0) {
                        color = attrs.getAttributeIntValue(i, color);
                        haveColor = true;
                    }
                } else {
                    stateSpec[j++] = attrs.getAttributeBooleanValue(i, false) ? stateResId : -stateResId;
                }
            }
            stateSpec = StateSet.trimStateSet(stateSpec, j);
            if (colorRes != 0) {
                color = resources.getColor(colorRes);
            } else if (!haveColor) {
                throw new XmlPullParserException(parser.getPositionDescription() + ": <item> tag requires a 'android:color' attribute.");
            }
            if (listSize == 0 || stateSpec.length == 0) {
                mDefaultColor = color;
            }
            if (listSize + 1 >= listAllocated) {
                listAllocated = idealIntArraySize(listSize + 1);
                int[] ncolor = new int[listAllocated];
                System.arraycopy(colorList, 0, ncolor, 0, listSize);
                int[][] nstate = new int[listAllocated][];
                System.arraycopy(stateSpecList, 0, nstate, 0, listSize);
                colorList = ncolor;
                stateSpecList = nstate;
            }
            colorList[listSize] = color;
            stateSpecList[listSize] = stateSpec;
            listSize++;
        }
        mColors = new int[listSize];
        mStateSpecs = new int[listSize][];
        System.arraycopy(colorList, 0, mColors, 0, listSize);
        System.arraycopy(stateSpecList, 0, mStateSpecs, 0, listSize);
    }

    private int idealIntArraySize(int i) {
        return idealByteArraySize(i * 4) / 4;
    }

    public int getColorForState(int[] iArr, int i) {
        int length = this.mStateSpecs.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (StateSet.stateSetMatches(this.mStateSpecs[i2], iArr)) {
                return this.mColors[i2];
            }
        }
        return i;
    }

    public int getDefaultColor() {
        return this.mDefaultColor;
    }

    public String toString() {
        return "ColorStateList{" + "mStateSpecs=" + Arrays.deepToString(mStateSpecs) + "mColors=" + Arrays.toString(mColors) + "mDefaultColor=" + mDefaultColor + '}';
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(i);
        for (int[] writeIntArray : this.mStateSpecs) {
            parcel.writeIntArray(writeIntArray);
        }
        parcel.writeIntArray(this.mColors);
    }

}