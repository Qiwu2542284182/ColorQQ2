package me.qiwu.colorqq.theme;

import android.os.Environment;

import java.io.File;

public interface ISkinWidget {
    int TYPE_COLOR = 0;
    int TYPE_IMAGE = 1;
    int getType();
    String getSkinName();
    void loadSkin();
}
