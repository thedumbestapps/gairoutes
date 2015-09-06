package com.tda.gairoutes.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Alexey on 8/30/2015.
 */
public abstract class BaseFragment extends Fragment {

    protected abstract void initTools();
    protected abstract View initBinder(LayoutInflater inflater, ViewGroup container);
    protected abstract void initViews();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initTools();
        View rootView = initBinder(inflater, container);
        initViews();
        return rootView;
    }
}
