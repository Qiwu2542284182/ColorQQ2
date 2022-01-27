package me.qiwu.colorqq.library.axml.AxmlPrinter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class StringBlock {
    private final CharsetDecoder UTF16LE_DECODER = Charset.forName("UTF-16LE").newDecoder();
    private final CharsetDecoder UTF8_DECODER = Charset.forName("UTF-8").newDecoder();
    private int[] m_stringOffsets;
    private byte[] m_strings;
    private int[] m_styleOffsets;
    private int[] m_styles;
    private boolean m_isUTF8;
    private static final int CHUNK_TYPE = 1835009;
    private static final int UTF8_FLAG = 256;

    public static StringBlock read(IntReader reader) throws IOException {
        ChunkUtil.readCheckType(reader, 1835009);
        int chunkSize = reader.readInt();
        int stringCount = reader.readInt();
        int styleOffsetCount = reader.readInt();
        int flags = reader.readInt();
        int stringsOffset = reader.readInt();
        int stylesOffset = reader.readInt();
        StringBlock block = new StringBlock();
        block.m_isUTF8 = (flags & 256) != 0;
        block.m_stringOffsets = reader.readIntArray(stringCount);
        if (styleOffsetCount != 0) {
            block.m_styleOffsets = reader.readIntArray(styleOffsetCount);
        }

        int size = (stylesOffset == 0 ? chunkSize : stylesOffset) - stringsOffset;
        block.m_strings = new byte[size];
        reader.readFully(block.m_strings);
        if (stylesOffset != 0) {
            size = chunkSize - stylesOffset;
            if (size % 4 != 0) {
                throw new IOException("Style data size is not multiple of 4 (" + size + ").");
            }

            block.m_styles = reader.readIntArray(size / 4);
        }

        return block;
    }

    public int getCount() {
        return this.m_stringOffsets != null ? this.m_stringOffsets.length : 0;
    }

    public String getString(int index) {
        if (index >= 0 && this.m_stringOffsets != null && index < this.m_stringOffsets.length) {
            int offset = this.m_stringOffsets[index];
            int length;
            int[] val;
            if (this.m_isUTF8) {
                val = getUtf8(this.m_strings, offset);
                offset = val[0];
                length = val[1];
            } else {
                val = getUtf16(this.m_strings, offset);
                offset += val[0];
                length = val[1];
            }

            return this.decodeString(offset, length);
        } else {
            return null;
        }
    }

    public CharSequence get(int index) {
        return this.getString(index);
    }

    public String getHTML(int index) {
        String raw = this.getString(index);
        if (raw == null) {
            return raw;
        } else {
            int[] style = this.getStyle(index);
            if (style == null) {
                return raw;
            } else {
                StringBuilder html = new StringBuilder(raw.length() + 32);
                int offset = 0;

                while(true) {
                    int i = -1;

                    int start;
                    for(start = 0; start != style.length; start += 3) {
                        if (style[start + 1] != -1 && (i == -1 || style[i + 1] > style[start + 1])) {
                            i = start;
                        }
                    }

                    start = i != -1 ? style[i + 1] : raw.length();

                    for(int j = 0; j != style.length; j += 3) {
                        int end = style[j + 2];
                        if (end != -1 && end < start) {
                            if (offset <= end) {
                                html.append(raw, offset, end + 1);
                                offset = end + 1;
                            }

                            style[j + 2] = -1;
                            html.append('<');
                            html.append('/');
                            html.append(this.getString(style[j]));
                            html.append('>');
                        }
                    }

                    if (offset < start) {
                        html.append(raw, offset, start);
                        offset = start;
                    }

                    if (i == -1) {
                        return html.toString();
                    }

                    html.append('<');
                    html.append(this.getString(style[i]));
                    html.append('>');
                    style[i + 1] = -1;
                }
            }
        }
    }

    public int find(String string) {
        if (string == null) {
            return -1;
        } else {
            for(int i = 0; i != this.m_stringOffsets.length; ++i) {
                int offset = this.m_stringOffsets[i];
                int length = getShort(this.m_strings, offset);
                if (length == string.length()) {
                    int j;
                    for(j = 0; j != length; ++j) {
                        offset += 2;
                        if (string.charAt(j) != getShort(this.m_strings, offset)) {
                            break;
                        }
                    }

                    if (j == length) {
                        return i;
                    }
                }
            }

            return -1;
        }
    }

    private StringBlock() {
    }

    private int[] getStyle(int index) {
        if (m_styleOffsets==null || m_styles==null || index>=m_styleOffsets.length) {
            return null;
        }
        int offset=m_styleOffsets[index]/4;
        int style[];
        {
            int count=0;
            for (int i=offset;i<m_styles.length;++i) {
                if (m_styles[i]==-1) {
                    break;
                }
                count+=1;
            }
            if (count==0 || (count%3)!=0) {
                return null;
            }
            style=new int[count];
        }
        for (int i=offset,j=0;i<m_styles.length;) {
            if (m_styles[i]==-1) {
                break;
            }
            style[j++]=m_styles[i++];
        }
        return style;
    }

    private String decodeString(int offset, int length) {
        try {
            return (this.m_isUTF8 ? this.UTF8_DECODER : this.UTF16LE_DECODER).decode(ByteBuffer.wrap(this.m_strings, offset, length)).toString();
        } catch (CharacterCodingException var4) {
            return null;
        }
    }

    private static final int getShort(byte[] array, int offset) {
        return (array[offset + 1] & 255) << 8 | array[offset] & 255;
    }

    private static final int getShort(int[] array, int offset) {
        int value = array[offset / 4];
        return offset % 4 / 2 == 0 ? value & '\uffff' : value >>> 16;
    }

    private static final int[] getUtf8(byte[] array, int offset) {
        int val = array[offset];
        if ((val & 128) != 0) {
            offset += 2;
        } else {
            ++offset;
        }

        val = array[offset];
        if ((val & 128) != 0) {
            offset += 2;
        } else {
            ++offset;
        }

        int length;
        for(length = 0; array[offset + length] != 0; ++length) {
        }

        return new int[]{offset, length};
    }

    private static final int[] getUtf16(byte[] array, int offset) {
        int val = (array[offset + 1] & 255) << 8 | array[offset] & 255;
        if (val == 32768) {
            int high = (array[offset + 3] & 255) << 8;
            int low = array[offset + 2] & 255;
            return new int[]{4, (high + low) * 2};
        } else {
            return new int[]{2, val * 2};
        }
    }
}
