package com.tda.gairoutes.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.tda.gairoutes.R;
import com.tda.gairoutes.databinding.FragmentMapBinding;
import com.tda.gairoutes.general.AppAdapter;
import com.tda.gairoutes.manager.SettingsManager;
import com.tda.gairoutes.misc.util.CSVUtil;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;

import timber.log.Timber;

/**
 * Created by Alexey on 8/28/2015.
 */
public class MapFragment extends BaseFragment implements
        GoogleApiClient.ConnectionCallbacks {

    public static final int MIN_ZOOM = 3;
    public static final int DEFAULT_ZOOM = 15;

    private Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private SettingsManager mSettingsManager;
    private ResourceProxy mResourceProxy;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;

    MyLocationNewOverlay mCurrentLocationOverlay;

    FragmentMapBinding mBinding;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    protected void initTools() {
        mSettingsManager = AppAdapter.settings();
        mResourceProxy = new DefaultResourceProxyImpl(AppAdapter.context());
        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case SettingsManager.PREF_MAP_SOURCE:
                        changeMapSource();
                        break;
                }
            }
        };
    }

    @Override
    protected View initBinder(LayoutInflater inflater, ViewGroup container) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        mBinding.setZoomButtonHandler(this);
        return mBinding.getRoot();
    }

    @Override
    protected void initViews() {
        mBinding.mvMap.setMinZoomLevel(MIN_ZOOM);
        mBinding.mvMap.getController().setZoom(DEFAULT_ZOOM);
        mBinding.mvMap.setTileSource(mSettingsManager.getMapSource());
        mBinding.mvMap.setBuiltInZoomControls(true);
        mBinding.mvMap.setMultiTouchControls(true);
        mCurrentLocationOverlay = new MyLocationNewOverlay(getActivity(), mBinding.mvMap);
        mCurrentLocationOverlay.setDrawAccuracyEnabled(true);
    }

    @Override
    public void onStart() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentLocationOverlay.enableMyLocation();
        mSettingsManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCurrentLocationOverlay.disableMyLocation();
        mSettingsManager.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    //
    @Override
    public void onConnected(Bundle bundle) {
        updateCurrentLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
    }
    @Override
    public void onConnectionSuspended(int i) {}
    //

    private void updateCurrentLocation(Location location) {
        mCurrentLocation = location;
        final GeoPoint currentPosition = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mBinding.mvMap.getController().setCenter(currentPosition);

        /*
        ItemizedOverlay<OverlayItem> currentLocationOverlay = new ItemizedIconOverlay<OverlayItem>(
                new ArrayList<OverlayItem>() {{add(new OverlayItem("You are here", "Yep, right here", currentPosition));}},
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, mResourceProxy);
        mBinding.mvMap.getOverlays().add(currentLocationOverlay);*/

        PathOverlay routePath = new PathOverlay(Color.RED, 5f, mResourceProxy);
        routePath.addPoints(CSVUtil.getPointsFromCsvFile(new File(Environment.getExternalStorageDirectory(), "Home-chiki-mother.csv")));
        mBinding.mvMap.getOverlays().add(routePath);

        mBinding.mvMap.getOverlays().add(mCurrentLocationOverlay);
        mBinding.mvMap.invalidate();
    }

    private void changeMapSource() {
        ITileSource mapSource = mSettingsManager.getMapSource();
        Timber.d("Setting '%s' changed to '%s'", SettingsManager.PREF_MAP_SOURCE, mapSource.name());
        mBinding.mvMap.setTileSource(mapSource);
    }

    public void onZoomIn(View view) {
        mBinding.mvMap.getController().zoomIn();
    }

    public void onZoomOut(View view) {
        mBinding.mvMap.getController().zoomOut();
    }
}
