package com.tda.gairoutes.misc.util;

import android.text.Html;
import android.text.Spanned;

import com.tda.gairoutes.general.AppAdapter;

/**
 * Created by Alexey on 9/13/2015.
 */
public class StringUtil {

    private StringUtil() {}

    public static Spanned makeUnderlined(int stringId) {
        return makeUnderlined(AppAdapter.context().getString(stringId));
    }

    public static Spanned makeUnderlined(String string) {
        return Html.fromHtml("<u>" + string + "</u>");
    }
}
