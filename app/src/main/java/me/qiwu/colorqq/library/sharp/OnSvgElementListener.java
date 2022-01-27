package me.qiwu.colorqq.library.sharp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public interface OnSvgElementListener {

    void onSvgStart(@NonNull Canvas canvas,
                    @Nullable RectF bounds);

    void onSvgEnd(@NonNull Canvas canvas,
                  @Nullable RectF bounds);

    <T> T onSvgElement(@Nullable String id,
                       @NonNull T element,
                       @Nullable RectF elementBounds,
                       @NonNull Canvas canvas,
                       @Nullable RectF canvasBounds,
                       @Nullable Paint paint);

    <T> void onSvgElementDrawn(@Nullable String id,
                               @NonNull T element,
                               @NonNull Canvas canvas,
                               @Nullable Paint paint);

}
