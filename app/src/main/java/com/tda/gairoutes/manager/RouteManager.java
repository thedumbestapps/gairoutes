package com.tda.gairoutes.manager;

import android.content.res.AssetManager;

import com.tda.gairoutes.general.AppAdapter;
import com.tda.gairoutes.misc.util.CSVUtil;
import com.tda.gairoutes.misc.util.FileUtil;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Alexey on 9/20/2015.
 */
public class RouteManager {

    public static final File ROUTES_INTERNAL_LOCATION = new File(AppAdapter.context().getFilesDir(), "routes");

    private static final String ROUTES_REMOTE_URL = "https://docs.google.com/uc?authuser=0&id=0B25Olm7mh7MyMHAwUFE0T2puWG8&export=download";
    private static final String ROUTES_ARCHIVE_FILENAME = "routes.zip";

    public interface UpdateListener {
        public void onUpdateComplete();
        public void onUpdateError();
    }

    public static void updateRoutes(final UpdateListener updateListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadManager.downloadFile(ROUTES_REMOTE_URL, AppAdapter.context().getFilesDir().getAbsolutePath(), ROUTES_ARCHIVE_FILENAME,
                        new DownloadManager.DownloadListener() {
                            @Override
                            public void onDownloadComplete(String url, File file) {
                                if (FileUtil.unzip(file, ROUTES_INTERNAL_LOCATION)) {
                                    saveRoutes();
                                    updateListener.onUpdateComplete();
                                } else {
                                    updateListener.onUpdateError();
                                }
                            }

                            @Override
                            public void onDownloadError(String url, Exception ex) {
                                updateListener.onUpdateError();
                            }

                            @Override
                            public void onDownloadProgress(String url, int percentReady) {}
                        });
            }
        }).start();
    }

    public static List<GeoPoint> getGeoPointsForRoute(String routeName) {
        File routeFile = new File(ROUTES_INTERNAL_LOCATION, routeName + ".csv");
        if (routeFile.exists()) {
            return CSVUtil.getPointsFromCsvFile(routeFile);
        } else {
            return null;
        }
    }

    public static void initRoutes() {
        AssetManager assetManager = AppAdapter.context().getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(ROUTES_ARCHIVE_FILENAME);
            if (FileUtil.unzip(is, ROUTES_INTERNAL_LOCATION)) {
                saveRoutes();
                Timber.d("Routes init done");
            } else {
                Timber.d("Routes init failed");
            }
        } catch (IOException e) {
            Timber.e(e, "Routes init failed");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void saveRoutes() {
        Set<String> routes = new HashSet<>();
        for (File routeFile : ROUTES_INTERNAL_LOCATION.listFiles()) {
            String routeFileName = routeFile.getName();
            routes.add(routeFileName.substring(0, routeFile.getName().lastIndexOf(".")));
        }
        new SettingsManager().setRoutes(routes);
    }
}
