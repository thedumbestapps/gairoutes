package com.tda.gairoutes.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tda.gairoutes.R;
import com.tda.gairoutes.general.AppAdapter;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alexey on 8/29/2015.
 */
public class SettingsManager {

    public static final String PREF_MAP_SOURCE = "settings_map_source";
    public static final String PREF_FOLLOW_MODE = "settings_follow_mode";
    public static final String PREF_LAST_LOCATION = "settings_last_location";
    public static final String PREF_ROUTES = "settings_routes";
    public static final String PREF_CURRENT_ROUTE = "current_route";

    private static final String PREF_LAST_LOCATION_DEFAULT_VALUE = new GeoPoint(53858238, 27502154).toString();

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public SettingsManager() {
        mContext = AppAdapter.context();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }

    public ITileSource getMapSource() {
        String prefTileSource = getPrefMapSource();
        for (ITileSource tileSource : TileSourceFactory.getTileSources()) {
            if (tileSource.name().equals(prefTileSource)) {
                return tileSource;
            }
        }
        throw new IllegalStateException("No tile source found. Saved value is '" + prefTileSource + "'");
    }

    public String getPrefMapSource() {
        return mSharedPreferences.getString(PREF_MAP_SOURCE, mContext.getResources().getStringArray(R.array.settings_map_source_entry_values)[0]);
    }

    public void setPrefMapSource(String mapSource) {
        mEditor.putString(PREF_MAP_SOURCE, mapSource);
        mEditor.commit();
    }

    public boolean getFollowMode() {
        return mSharedPreferences.getBoolean(PREF_FOLLOW_MODE, false);
    }

    public void setFollowMode(boolean followMode) {
        mEditor.putBoolean(PREF_FOLLOW_MODE, followMode);
        mEditor.commit();
    }

    public GeoPoint getLastLocation() {
        return GeoPoint.fromIntString(mSharedPreferences.getString(PREF_LAST_LOCATION, PREF_LAST_LOCATION_DEFAULT_VALUE));
    }

    public void setLastLocation(GeoPoint geoPoint) {
        mEditor.putString(PREF_LAST_LOCATION, geoPoint.toString());
        mEditor.commit();
    }

    public Set<String> getRoutes() {
        return mSharedPreferences.getStringSet(PREF_ROUTES, new HashSet<String>());
    }

    public void setRoutes(Set<String> routes) {
        mEditor.putStringSet(PREF_ROUTES, routes);
        mEditor.commit();
    }

    public String getCurrentRoute() {
        return mSharedPreferences.getString(PREF_CURRENT_ROUTE, null);
    }

    public void setCurrentRoute(String route) {
        mEditor.putString(PREF_CURRENT_ROUTE, route);
        mEditor.commit();
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }
}
