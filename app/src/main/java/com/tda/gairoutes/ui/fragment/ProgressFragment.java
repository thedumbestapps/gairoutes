package com.tda.gairoutes.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.tda.gairoutes.R;

/**
 * Created by Alexey on 9/20/2015.
 */
public class ProgressFragment extends DialogFragment {

    public static final String TAG = ProgressFragment.class.getSimpleName();

    private static ProgressFragment instance;

    private static ProgressFragment getInstance() {
        if (instance == null) {
            instance = new ProgressFragment();
            instance.setRetainInstance(true);
        }
        return instance;
    }

    public static void show(FragmentManager manager) {
        if (!getInstance().isAdded() && getInstance().getFragmentManager() == null) {
            getInstance().show(manager, TAG);
        }
    }

    public static void cancel() {
        if (getInstance().isAdded()) {
            getInstance().dismissAllowingStateLoss();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.Base_Theme_AppCompat_Light_Dialog);
        dialog.setCancelable(false);
        dialog.setContentView(new ProgressBar(getActivity()));
        setCancelable(false);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // Work around bug: http://code.google.com/p/android/issues/detail?id=17423
        if ((dialog != null) && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
