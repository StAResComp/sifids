package uk.ac.masts.sifids.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.List;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchPresentation;
import uk.ac.masts.sifids.entities.CatchSpecies;
import uk.ac.masts.sifids.entities.CatchState;
import uk.ac.masts.sifids.entities.Fish1FormRow;
import uk.ac.masts.sifids.entities.Gear;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class EditFish1FormRowActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Fish1FormRow fish1FormRow;

    CatchDatabase db;

    int formId;

    EditText latitude;
    EditText longitude;
    EditText icesArea;
    List<Gear> gearList;
    ArrayAdapter<Gear> gearAdapter;
    Spinner gear;
    EditText meshSize;
    List<CatchSpecies> speciesList;
    ArrayAdapter<CatchSpecies> speciesAdapter;
    Spinner species;
    List<CatchState> stateList;
    ArrayAdapter<CatchState> stateAdapter;
    Spinner state;
    List<CatchPresentation> presentationList;
    ArrayAdapter<CatchPresentation> presentationAdapter;
    Spinner presentation;
    EditText weight;
    CheckBox dis;
    CheckBox bms;
    EditText numberOfPotsHauled;
    EditText transporterRegEtc;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_edit_fish_1_form_row);

        db = CatchDatabase.getInstance(getApplicationContext());

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        this.mapElements();

        this.processIntent();

    }

    private void processIntent() {
        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (!extras.isEmpty() && extras.containsKey("form_id")) {
                this.formId = extras.getInt("form_id");
            }
            if (!extras.isEmpty() && extras.containsKey("id")) {
                final int id = extras.getInt("id");

                Runnable r = new Runnable(){
                    @Override
                    public void run() {
                        fish1FormRow = db.catchDao().getFormRow(id);
                    }
                };

                Thread newThread= new Thread(r);
                newThread.start();
                try {
                    newThread.join();
                }
                catch (InterruptedException ie) {

                }
            }
        }
    }

    private void mapElements() {

        this.loadOptions();

        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        icesArea = (EditText) findViewById(R.id.ices_area);
        this.createSpinner(gearAdapter, gearList, gear, R.id.gear);
        meshSize = (EditText) findViewById(R.id.mesh_size);
        this.createSpinner(speciesAdapter, speciesList, species,R.id.species);
        this.createSpinner(stateAdapter, stateList, state,R.id.state);
        this.createSpinner(presentationAdapter, presentationList, presentation,R.id.presentation);
        weight = (EditText) findViewById(R.id.weight);
        dis = (CheckBox) findViewById(R.id.dis);
        bms = (CheckBox) findViewById(R.id.bms);
        numberOfPotsHauled = (EditText) findViewById(R.id.number_of_pots_hauled);
        transporterRegEtc = (EditText) findViewById(R.id.transporter_reg_etc);
    }

    private void loadOptions() {
        Runnable r = new Runnable(){
            @Override
            public void run() {
                gearList = db.catchDao().getGear();
                speciesList = db.catchDao().getSpecies();
                stateList = db.catchDao().getStates();
                presentationList = db.catchDao().getPresentations();
            }
        };

        Thread newThread= new Thread(r);
        newThread.start();
        try {
            newThread.join();
        }
        catch (InterruptedException ie) {

        }
    }

    private void createSpinner(ArrayAdapter adapter, List list, Spinner spinner, int widgetId) {
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(widgetId);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void showFishingActivityDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "fishing_activity_date");
    }

    public void showLandingOrDiscardDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "landing_or_discard_date");
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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String tag = getTag();
            // Do something with the date chosen by the user
        }
    }
}
