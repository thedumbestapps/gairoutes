package com.tda.gairoutes.general;

import android.app.Application;

import com.bettervectordrawable.Convention;
import com.bettervectordrawable.VectorDrawableCompat;
import com.crashlytics.android.Crashlytics;
import com.tda.gairoutes.BuildConfig;
import com.tda.gairoutes.R;
import com.tda.gairoutes.manager.RouteManager;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by Alexey on 8/29/2015.
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        AppAdapter.getInstance().init(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashReportingTree());
        }
        RouteManager.initRoutes();
        initVectorDrawables();
    }

    @Override
    public void onTerminate() {
        AppAdapter.destroy();
        super.onTerminate();
    }

    private void initVectorDrawables() {
        int[] ids = VectorDrawableCompat.findVectorResourceIdsByConvention(getResources(), R.drawable.class,
                Convention.RESOURCE_NAME_HAS_VECTOR_PREFIX);
        VectorDrawableCompat.enableResourceInterceptionFor(getResources(), ids);
    }

    private static class CrashReportingTree extends Timber.HollowTree {

        @Override
        public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);
            Crashlytics.logException(t);
        }
    }
}
