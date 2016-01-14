package com.tda.gairoutes.misc.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.bettervectordrawable.utils.BitmapUtil;
import com.tda.gairoutes.general.AppAdapter;

/**
 * Created by Alexey on 10/11/2015.
 */
public class ImageUtil {

    private ImageUtil() {}

    public static Drawable resizeVectorDrawable(int drawableId, int widthDp, int heightDp) {
        DisplayMetrics metrics = AppAdapter.resources().getDisplayMetrics();
        return new BitmapDrawable(AppAdapter.resources(),
                BitmapUtil.toBitmap(AppAdapter.resources().getDrawable(drawableId), metrics, widthDp, heightDp));
    }
}
