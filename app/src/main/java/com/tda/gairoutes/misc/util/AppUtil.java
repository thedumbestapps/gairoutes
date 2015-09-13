package com.tda.gairoutes.misc.util;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.MailTo;
import android.net.Uri;

import com.tda.gairoutes.BuildConfig;
import com.tda.gairoutes.R;
import com.tda.gairoutes.general.AppAdapter;

import java.io.File;

import timber.log.Timber;

/**
 * Created by Alexey on 9/13/2015.
 */
public class AppUtil {

    public static final String APP_MARKET_PATTERN = "market://details?id=%s";

    private AppUtil() {}

    public static long getLastUpdated() {
        PackageManager pm = AppAdapter.context().getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfo(BuildConfig.APPLICATION_ID, 0);
            String appFile = appInfo.sourceDir;
            return new File(appFile).lastModified();
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, "Can't find last update date");
            return 0;
        }
    }

    public static void RateApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(APP_MARKET_PATTERN, BuildConfig.APPLICATION_ID)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppAdapter.context().startActivity(intent);
    }

    public static void sendEmailToAuthor() {
        String uriString = MailTo.MAILTO_SCHEME + AppAdapter.context().getString(R.string.author_email);
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uriString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppAdapter.context().startActivity(intent);
    }
}
