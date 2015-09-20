package com.tda.gairoutes.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;

import com.tda.gairoutes.BuildConfig;
import com.tda.gairoutes.R;
import com.tda.gairoutes.databinding.DialogAboutBinding;
import com.tda.gairoutes.manager.SettingsManager;
import com.tda.gairoutes.misc.util.AppUtil;
import com.tda.gairoutes.misc.util.DateUtil;
import com.tda.gairoutes.misc.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alexey on 9/6/2015.
 */
public class DialogManager {

    public static final int ID_MAP_SOURCE = 1;
    public static final int ID_ABOUT = 2;
    public static final int ID_ROUTES = 3;

    public static AlertDialog getDialog(Activity activity, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        switch (id) {
            case ID_MAP_SOURCE:
                buildMapSourceDialog(activity, builder);
                return builder.create();
            case ID_ABOUT:
                buildAboutDialog(activity, builder);
                return builder.create();
            case ID_ROUTES:
                buildRoutesDialog(activity, builder);
                return builder.create();
            default:
                throw new IllegalArgumentException("No dialog with such id=" + id);
        }
    }

    private static void buildMapSourceDialog(Activity activity, final AlertDialog.Builder builder) {
        final SettingsManager settingsManager = new SettingsManager();

        builder.setTitle(R.string.dialog_title_map_source);
        builder.setCancelable(true);

        final String[] mapSources = activity.getResources().getStringArray(R.array.settings_map_source_entries);
        final String[] mapSourceValues = activity.getResources().getStringArray(R.array.settings_map_source_entry_values);
        String currentMapSource = settingsManager.getPrefMapSource();
        int currentMapSourceIndex = Arrays.asList(mapSourceValues).indexOf(currentMapSource);

        builder.setSingleChoiceItems(mapSources, currentMapSourceIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingsManager.setPrefMapSource(mapSourceValues[which]);
                dialog.dismiss();
            }
        });
    }

    private static void buildAboutDialog(Activity activity, final AlertDialog.Builder builder) {
        builder.setTitle(R.string.dialog_title_about);
        builder.setCancelable(true);
        DialogAboutBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.dialog_about, null, false);
        binding.setDialogAboutController(new DialogManager());
        builder.setView(binding.getRoot());
        binding.tvAppName.setText(activity.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        long lastUpdateMillis = AppUtil.getLastUpdated();
        String lastUpdate = lastUpdateMillis != 0 ?
                new SimpleDateFormat(DateUtil.DD_MMM_YYYY, Locale.getDefault()).format(new Date(lastUpdateMillis)) : "";
        binding.tvLastUpdate.setText(activity.getString(R.string.last_update, lastUpdate));
        binding.tvAuthor.setText(Html.fromHtml(activity.getString(R.string.by_author)));
        binding.tvAuthorEmail.setText(StringUtil.makeUnderlined(R.string.author_email));
    }

    public void onRateAppClick(View view) {
        AppUtil.RateApp();
    }

    public void onAuthorClick(View view) {
        AppUtil.sendEmailToAuthor();
    }

    private static void buildRoutesDialog(Activity activity, final AlertDialog.Builder builder) {
        final SettingsManager settingsManager = new SettingsManager();

        builder.setTitle(R.string.dialog_title_routes);
        builder.setCancelable(true);

        final String[] routes = settingsManager.getRoutes().toArray(new String[settingsManager.getRoutes().size()]);
        String currentRoute = settingsManager.getCurrentRoute();
        int currentRouteIndex = Arrays.asList(routes).indexOf(currentRoute);

        builder.setSingleChoiceItems(routes, currentRouteIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingsManager.setCurrentRoute(routes[which]);
                dialog.dismiss();
            }
        });
    }
}
