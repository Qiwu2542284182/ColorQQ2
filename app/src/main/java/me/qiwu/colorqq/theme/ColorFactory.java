package me.qiwu.colorqq.theme;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import me.qiwu.colorqq.library.axml.AxmlPrinter.AXmlResourceParser;


public class ColorFactory {
    private static final int[][] EMPTY_COLOR = {new int[0]};

    public static ColorFactory.ColorValue createColorFromFile(Resources resources, File file){
        try {
            AXmlResourceParser aXmlResourceParser = new AXmlResourceParser();
            InputStream inputStream = new FileInputStream(file);
            aXmlResourceParser.open(inputStream);
            ColorFactory.ColorValue colorStateList = createColorFromXml(resources,aXmlResourceParser);
            aXmlResourceParser.close();
            inputStream.close();
            return colorStateList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ColorFactory.ColorValue(EMPTY_COLOR, new int[]{Color.RED});
    }

    public static ColorFactory.ColorValue createColorFromXml(Resources resources,XmlPullParser parser) {
        try {
            AttributeSet attrs = Xml.asAttributeSet(parser);

            int type;
            while ((type=parser.next()) != XmlPullParser.START_TAG
                    && type != XmlPullParser.END_DOCUMENT) {
            }

            if (type != XmlPullParser.START_TAG) {
                throw new XmlPullParserException("No start tag found");
            }

            return createColorFromXmlInner(resources,parser, attrs);
        } catch (Exception e) {
            return new ColorFactory.ColorValue(EMPTY_COLOR, new int[]{Color.RED});
        }


    }

    private static ColorFactory.ColorValue createColorFromXmlInner(Resources resources, XmlPullParser parser,AttributeSet attrs) throws XmlPullParserException, IOException {
        int type;

        final int innerDepth = parser.getDepth()+1;
        int depth;

        int listAllocated = 20;
        int listSize = 0;
        int[] colorList = new int[listAllocated];
        int[][] stateSpecList = new int[listAllocated][];

        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT && ((depth = parser.getDepth()) >= innerDepth || type != XmlPullParser.END_TAG)) {
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
                    stateSpec[j++] = attrs.getAttributeBooleanValue(i, false)
                            ? stateResId
                            : -stateResId;
                }
            }
            stateSpec = StateSet.trimStateSet(stateSpec, j);

            if (colorRes != 0) {
                color = resources.getColor(colorRes);
            } else if (!haveColor) {
                throw new XmlPullParserException(
                        parser.getPositionDescription()
                                + ": <item> tag requires a 'android:color' attribute.");
            }

            /*
            if (listSize == 0 || stateSpec.length == 0) {
                mDefaultColor = color;
            }*/

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

        int[] mColors = new int[listSize];
        int[][] mStateSpecs = new int[listSize][];
        System.arraycopy(colorList, 0, mColors, 0, listSize);
        System.arraycopy(stateSpecList, 0, mStateSpecs, 0, listSize);
        return new ColorFactory.ColorValue(mStateSpecs,mColors);
    }

    private static int idealIntArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    private static int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++) {
            if (need <= (1 << i) - 12) {
                return (1 << i) - 12;
            }
        }
        return need;
    }

    public static class ColorValue{
        public int[][] states;
        public int[] colors;

        public ColorValue(int[][] states,int[]colors){
            this.states = states;
            this.colors = colors;
        }
    }
}
