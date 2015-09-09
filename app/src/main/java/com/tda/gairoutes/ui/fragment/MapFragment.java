package com.tda.gairoutes.ui.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tda.gairoutes.R;
import com.tda.gairoutes.databinding.FragmentMapBinding;
import com.tda.gairoutes.general.AppAdapter;
import com.tda.gairoutes.manager.SettingsManager;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import timber.log.Timber;

/**
 * Created by Alexey on 8/28/2015.
 */
public class MapFragment extends BaseFragment {

    public static final int MIN_ZOOM = 3;
    public static final int DEFAULT_ZOOM = 16;

    private SettingsManager mSettingsManager;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private Menu mToolbarMenu;

    MyLocationNewOverlay mCurrentLocationOverlay;

    FragmentMapBinding mBinding;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    protected void initTools() {
        mSettingsManager = AppAdapter.settings();
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
        setHasOptionsMenu(true);
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
        mBinding.mvMap.getOverlays().add(mCurrentLocationOverlay);
        mBinding.mvMap.getOverlays().add(new Overlay(getActivity()) {
            @Override
            protected void draw(Canvas c, MapView osmv, boolean shadow) {
            }

            @Override
            public boolean onTouchEvent(MotionEvent event, MapView mapView) {
                if (event.getAction() == MotionEvent.ACTION_UP && mSettingsManager.getFollowMode()) {
                    GeoPoint mCurrentGeoPoint = mSettingsManager.getLastLocation();
                    if (mCurrentGeoPoint == null || !mCurrentGeoPoint.equals(mBinding.mvMap.getMapCenter())) {
                        updateFollowMeItem(mToolbarMenu.findItem(R.id.menu_follow_me), false);
                        mSettingsManager.setFollowMode(false);
                    }
                }
                return super.onTouchEvent(event, mapView);
            }
        });
    }

    @Override
    public void onStart() {
        setCurrentLocation(mSettingsManager.getLastLocation());
        super.onStart();
    }

    @Override
    public void onStop() {
        updateCurrentLocationWithCenterLocation();
        super.onStop();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mToolbarMenu = menu;
        updateFollowMeItem(menu.findItem(R.id.menu_follow_me), mSettingsManager.getFollowMode());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_follow_me:
                boolean newFollowMode = !mSettingsManager.getFollowMode();
                updateFollowMeItem(item, newFollowMode);
                mSettingsManager.setFollowMode(newFollowMode);
                return true;
            case R.id.menu_route:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateFollowMeItem(MenuItem item, boolean newFollowMode) {
        if (newFollowMode) {
            mCurrentLocationOverlay.enableFollowLocation();
            item.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_view));
        } else {
            mCurrentLocationOverlay.disableFollowLocation();
            item.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_call));
        }
    }

    private void updateCurrentLocationWithCenterLocation() {
        setCurrentLocation((GeoPoint) mBinding.mvMap.getMapCenter());
    }

    private void setCurrentLocation(GeoPoint geoPoint) {
        if (geoPoint != null) {
            mSettingsManager.setLastLocation(geoPoint);
            mBinding.mvMap.getController().setCenter(geoPoint);
        }
    }

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

        /*PathOverlay routePath = new PathOverlay(Color.RED, 5f, mResourceProxy);
        routePath.addPoints(CSVUtil.getPointsFromCsvFile(new File(Environment.getExternalStorageDirectory(), "Home-chiki-mother.csv")));
        mBinding.mvMap.getOverlays().add(routePath);

        mBinding.mvMap.getOverlays().add(mCurrentLocationOverlay);
        mBinding.mvMap.invalidate();*/

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
