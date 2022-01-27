package me.qiwu.colorqq.theme;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

public class SkinContext extends ContextWrapper {
    private Resources resources;
    public SkinContext(Context base) {
        super(base);
        Resources resources = base.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        DisplayMetrics systemdisplayMetrics = new DisplayMetrics();
        systemdisplayMetrics.setTo(displayMetrics);
        systemdisplayMetrics.density = displayMetrics.density * 0.7f;
        systemdisplayMetrics.scaledDensity = displayMetrics.scaledDensity * 0.7f;
        systemdisplayMetrics.densityDpi = (int) (0.7f * ((float)displayMetrics.densityDpi));

        Configuration configuration = new Configuration();
        configuration.setTo(resources.getConfiguration());
        configuration.densityDpi = (int) (0.7f * ((float)displayMetrics.densityDpi));
        this.resources = new Resources(resources.getAssets(),systemdisplayMetrics,configuration);
    }

    @Override
    public Resources getResources() {
        return resources;
    }
}
