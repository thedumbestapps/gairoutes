package com.tda.gairoutes.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tda.gairoutes.R;
import com.tda.gairoutes.ui.dialog.DialogManager;
import com.tda.gairoutes.ui.fragment.MapFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar actionBarToolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(actionBarToolBar);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.main_frame, new MapFragment()).addToBackStack(null).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_find_me:

                return true;
            case R.id.menu_route:

                return true;
            case R.id.menu_map:
                DialogManager.getDialog(this, DialogManager.ID_MAP_SOURCE).show();
                return true;
            case R.id.menu_about:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
