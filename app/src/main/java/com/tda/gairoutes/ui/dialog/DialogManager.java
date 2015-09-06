package com.tda.gairoutes.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tda.gairoutes.R;
import com.tda.gairoutes.manager.SettingsManager;

import java.util.Arrays;

import timber.log.Timber;

/**
 * Created by Alexey on 9/6/2015.
 */
public class DialogManager {

    public static final int ID_MAP_SOURCE = 1;

    public static AlertDialog getDialog(Activity activity, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        switch (id) {
            case ID_MAP_SOURCE:
                buildMapSourceDialog(activity, builder);
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

        /* Custom RadioGroup
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_map_source, null, false);
        RadioGroup rgMapSource = (RadioGroup) view.findViewById(R.id.rgMapSource);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        RadioButton radioButton;
        int i = 0;
        for (String mapSource : mapSources) {
            radioButton = new RadioButton(activity);
            radioButton.setText(mapSource);
            radioButton.setTextSize(activity.getResources().getDimension(R.dimen.radio_group_in_dialog_text_size));
            radioButton.setTag(mapSourceValues[i]);
            radioButton.setId(i);
            radioButton.setChecked(mapSourceValues[i].equals(currentMapSource));
            rgMapSource.addView(radioButton, layoutParams);
            i++;
            if (i != mapSources.length) {
                ImageView divider = new ImageView(activity);
                divider.setImageDrawable(activity.getResources().getDrawable(R.drawable.divider_radio_group));
                rgMapSource.addView(divider);
            }
        }
        rgMapSource.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                settingsManager.setPrefMapSource((String) group.findViewById(checkedId).getTag());
                // TODO dialog dismiss
            }
        });*/
        //builder.setView(view);

        builder.setSingleChoiceItems(mapSources, currentMapSourceIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingsManager.setPrefMapSource(mapSourceValues[which]);
                dialog.dismiss();
            }
        });
    }
}
