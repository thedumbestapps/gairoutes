package com.tda.gairoutes.misc.util;

import android.text.Html;
import android.text.Spanned;

import com.tda.gairoutes.general.AppAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Alexey on 9/13/2015.
 */
public class StringUtil {

    public static String ENCODING_UTF8 = "UTF-8";
    public static String ENCODING_ISO_8859 = "iso-8859-1";

    private StringUtil() {
    }

    public static Spanned makeUnderlined(int stringId) {
        return makeUnderlined(AppAdapter.context().getString(stringId));
    }

    public static Spanned makeUnderlined(String string) {
        return Html.fromHtml("<u>" + string + "</u>");
    }
}
