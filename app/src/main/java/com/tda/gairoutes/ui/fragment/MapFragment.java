package com.tda.gairoutes.ui.fragment;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tda.gairoutes.R;
import com.tda.gairoutes.databinding.FragmentMapBinding;
import com.tda.gairoutes.general.AppAdapter;
import com.tda.gairoutes.manager.RouteManager;
import com.tda.gairoutes.manager.SettingsManager;
import com.tda.gairoutes.misc.util.ImageUtil;
import com.tda.gairoutes.ui.dialog.DialogManager;
import com.tda.gairoutes.ui.gfx.map.PathUtil;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Alexey on 8/28/2015.
 */
public class MapFragment extends BaseFragment {

    public static final int MIN_ZOOM = 3;
    public static final int DEFAULT_ZOOM = 16;

    public static final String KEY_ZOOM_LEVEL = "MapFragment.ZoomLevel";

    private SettingsManager mSettingsManager;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private Menu mToolbarMenu;

    private MyLocationNewOverlay mCurrentLocationOverlay;
    private Polyline mRoutePath = PathUtil.getRoutePath();
    private ResourceProxy mResourceProxy = new DefaultResourceProxyImpl(AppAdapter.context());
    private Overlay mStartPointOverlay;

    private FragmentMapBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getInt(KEY_ZOOM_LEVEL, 0) != 0) {
            mBinding.mvMap.getController().setZoom(savedInstanceState.getInt(KEY_ZOOM_LEVEL));
        }
        return view;
    }

    @Override
    public void onDetach() {
        ViewGroup view = (ViewGroup) getActivity().getWindow().getDecorView();
        view.removeAllViews();
        super.onDetach();
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
                    case SettingsManager.PREF_CURRENT_ROUTE:
                        changeRoute();
                        break;
                }
            }
        };
        setHasOptionsMenu(true);
    }

    @Override
    protected View initBinder(LayoutInflater inflater, ViewGroup container) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        mBinding.setMapFragmentController(this);
        return mBinding.getRoot();
    }

    @Override
    protected void initViews() {
        mBinding.mvMap.setMinZoomLevel(MIN_ZOOM);
        mBinding.mvMap.getController().setZoom(DEFAULT_ZOOM);
        mBinding.mvMap.setTileSource(mSettingsManager.getMapSource());
        mBinding.mvMap.setBuiltInZoomControls(true);
        mBinding.mvMap.setMultiTouchControls(true);
        mBinding.mvMap.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mCurrentLocationOverlay = new MyLocationNewOverlay(getActivity(), mBinding.mvMap);
        mCurrentLocationOverlay.setDrawAccuracyEnabled(true);
        mBinding.mvMap.getOverlays().add(mCurrentLocationOverlay);
        mBinding.mvMap.getOverlays().add(new Overlay(getActivity()) {
            @Override
            protected void draw(Canvas c, MapView osmv, boolean shadow) {
            }

            @Override
            public boolean onScroll(MotionEvent pEvent1, MotionEvent pEvent2, float pDistanceX, float pDistanceY, MapView pMapView) {
                if (mSettingsManager.getFollowMode()) {
                    updateFollowMeItem(mToolbarMenu.findItem(R.id.menu_follow_me), false);
                    mSettingsManager.setFollowMode(false);
                }
                return super.onScroll(pEvent1, pEvent2, pDistanceX, pDistanceY, pMapView);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_ZOOM_LEVEL, mBinding.mvMap.getZoomLevel());
        super.onSaveInstanceState(outState);
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
        if (mSettingsManager.getCurrentRoute() != null) {
            changeRoute();
        }
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
                DialogManager.getDialog(getActivity(), DialogManager.ID_ROUTES).show();
                return true;
            case R.id.menu_clean_map:
                mSettingsManager.setCurrentRoute(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateFollowMeItem(MenuItem item, boolean newFollowMode) {
        if (newFollowMode) {
            mCurrentLocationOverlay.enableFollowLocation();
            item.setIcon(AppAdapter.resources().getDrawable(R.drawable.vector_unfollow));
        } else {
            mCurrentLocationOverlay.disableFollowLocation();
            item.setIcon(AppAdapter.resources().getDrawable(R.drawable.vector_follow));
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

    private void changeMapSource() {
        ITileSource mapSource = mSettingsManager.getMapSource();
        Timber.d("Setting '%s' changed to '%s'", SettingsManager.PREF_MAP_SOURCE, mapSource.name());
        mBinding.mvMap.setTileSource(mapSource);
    }

    private void changeRoute() {
        mBinding.mvMap.getOverlays().remove(mRoutePath);
        mBinding.mvMap.getOverlays().remove(mStartPointOverlay);
        String currentRoute = mSettingsManager.getCurrentRoute();
        Timber.d("Setting '%s' changed to '%s'", SettingsManager.PREF_CURRENT_ROUTE, currentRoute);
        if (currentRoute != null) {
            List<GeoPoint> points = RouteManager.getGeoPointsForRoute(currentRoute);
            setMapOverlays(currentRoute, points);
        }
        mBinding.mvMap.invalidate();
    }

    private void setMapOverlays(String routeName, List<GeoPoint> points) {
        if (points != null && points.size() > 0) {
            mRoutePath.setPoints(points);
            mBinding.mvMap.getOverlays().add(mRoutePath);
            GeoPoint startPoint = points.get(0);
            mStartPointOverlay = getStartPointOverlay(routeName, startPoint);
            mBinding.mvMap.getOverlays().add(mStartPointOverlay);
            if (!mSettingsManager.getFollowMode()) {
                mBinding.mvMap.getController().setCenter(startPoint);
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_route_broken, routeName), Toast.LENGTH_LONG).show();
            Timber.e("Route " + routeName + " is broken");
        }
    }

    private Overlay getStartPointOverlay(String routeName, GeoPoint startPoint) {
        OverlayItem startItem = new OverlayItem(routeName, null, startPoint);
        startItem.setMarker(ImageUtil.resizeVectorDrawable(R.drawable.vector_start, 32, 32));
        List<OverlayItem> items = new ArrayList<>();
        items.add(startItem);
        return new ItemizedIconOverlay<>(items, null, mResourceProxy);
    }

    public void onZoomIn(View view) {
        mBinding.mvMap.getController().zoomIn();
    }

    public void onZoomOut(View view) {
        mBinding.mvMap.getController().zoomOut();
    }
}
