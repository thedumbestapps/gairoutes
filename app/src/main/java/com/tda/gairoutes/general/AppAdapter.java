package com.tda.gairoutes.general;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;

import com.tda.gairoutes.manager.SettingsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexey on 8/29/2015.
 */
public class AppAdapter {

    private Context mContext;
    private Resources mResources;
    private SettingsManager mSettingsManager;
    private List<BroadcastReceiver> mLocalReceivers;

    protected static AppAdapter instance;

    private AppAdapter() {}

    static AppAdapter getInstance() {
        if (instance == null) {
            instance = new AppAdapter();
        }
        return instance;
    }

    public static void destroy() {
        instance = null;
    }

    public void init(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mSettingsManager = new SettingsManager();
        mLocalReceivers = new ArrayList<>();
    }

    public static Context context() {
        return getInstance().mContext;
    }

    public static Resources resources() {
        return getInstance().mResources;
    }

    public static SettingsManager settings() {
        return getInstance().mSettingsManager;
    }

    private static List<BroadcastReceiver> receivers() {
        return getInstance().mLocalReceivers;
    }

    public static void registerLocalReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver != null && !receivers().contains(receiver)) {
            LocalBroadcastManager.getInstance(context()).registerReceiver(receiver, filter);
            receivers().add(receiver);
        }
    }

    public static void unregisterLocalReceiver(BroadcastReceiver receiver) {
        if (receiver != null && receivers().contains(receiver)) {
            LocalBroadcastManager.getInstance(context()).unregisterReceiver(receiver);
            receivers().remove(receiver);
        }
    }

}
