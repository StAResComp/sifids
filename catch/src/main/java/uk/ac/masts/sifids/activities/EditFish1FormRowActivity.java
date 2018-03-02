package uk.ac.masts.sifids.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.Fish1Form;
import uk.ac.masts.sifids.entities.Fish1FormRow;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class EditFish1FormRowActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Fish1FormRow fish1FormRow;

    int formId;

    Button fishingActivityDate;
    EditText latitude;
    EditText longitude;
    EditText icesArea;
    Spinner gear;
    EditText meshSize;
    Spinner species;
    Spinner state;
    Spinner presentation;
    EditText weight;
    CheckBox dis;
    CheckBox bms;
    EditText numberOfPotsHauled;
    Button landingOrDiscardDate;
    EditText transporterRegEtc;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_edit_fish_1_form_row);

        final CatchDatabase db = CatchDatabase.getInstance(getApplicationContext());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//        switch(parent.getId()) {
//            case R.id.port_of_departure:
//                this.portOfDepartureValue = parent.getItemAtPosition(pos).toString();
//                break;
//            case R.id.port_of_landing:
//                this.portOfLandingValue = parent.getItemAtPosition(pos).toString();
//                break;
//        }
//        portOfDepartureAdapter.notifyDataSetChanged();

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.activity_catch:
                intent = new Intent(this, Fish1FormsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up saveButton in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
