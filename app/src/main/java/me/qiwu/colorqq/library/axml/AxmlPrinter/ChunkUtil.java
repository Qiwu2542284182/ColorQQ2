package me.qiwu.colorqq.library.axml.AxmlPrinter;

import java.io.IOException;

class ChunkUtil {
    ChunkUtil() {
    }

    public static final void readCheckType(IntReader reader, int expectedType) throws IOException {
        int type = reader.readInt();
        if (type != expectedType) {
            throw new IOException("Expected chunk of type 0x" + Integer.toHexString(expectedType) + ", read 0x" + Integer.toHexString(type) + ".");
        }
    }
}
