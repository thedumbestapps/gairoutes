package com.tda.gairoutes.misc.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tda.gairoutes.general.AppAdapter;

import timber.log.Timber;

/**
 * Created by Alexey on 9/28/2015.
 */
public class NetworkUtil {

    public static boolean isConnectedToInternet() {
        return isWifiOn() || isMobileInternetOn();
    }

    public static boolean isWifiOn() {
        ConnectivityManager connManager = (ConnectivityManager) AppAdapter.context().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi == null) {
            Timber.d("wifi = null");
            return false;
        } else {
            Timber.d("wifi.isConnectedOrConnecting()=" + wifi.isConnectedOrConnecting());
            return wifi.isConnectedOrConnecting();
        }
    }

    public static boolean isMobileInternetOn() {
        ConnectivityManager connManager = (ConnectivityManager) AppAdapter.context().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInternet = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileInternet == null) {
            Timber.d("mobileInternet = null");
            return false;
        } else {
            Timber.d("mobileInternet.isConnectedOrConnecting()=" + mobileInternet.isConnectedOrConnecting());
            return mobileInternet.isConnectedOrConnecting();
        }
    }
}