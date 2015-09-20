package com.tda.gairoutes.general;

import android.app.Application;

import com.tda.gairoutes.BuildConfig;
import com.tda.gairoutes.manager.RouteManager;

import timber.log.Timber;

/**
 * Created by Alexey on 8/29/2015.
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppAdapter.getInstance().init(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            //Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashReportingTree());
        }
        RouteManager.initRoutes();
    }

    @Override
    public void onTerminate() {
        AppAdapter.destroy();
        super.onTerminate();
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override
        public void i(String message, Object... args) {
            //Crashlytics.log(String.format(message, args));
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override
        public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);
            //Crashlytics.logException(t);
        }
    }
}
