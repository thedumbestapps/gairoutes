package com.tda.gairoutes.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tda.gairoutes.R;
import com.tda.gairoutes.general.AppAdapter;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

/**
 * Created by Alexey on 8/29/2015.
 */
public class SettingsManager {

    public static final String PREF_MAP_SOURCE = "settings_map_source";

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

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }
}
