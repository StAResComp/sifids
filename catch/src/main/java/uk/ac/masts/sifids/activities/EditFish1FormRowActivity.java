package uk.ac.masts.sifids.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
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

public class EditFish1FormRowActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    Fish1FormRow fish1FormRow;

    CatchDatabase db;

    int formId;

    TextView fishingActivityDateDisplay;
    Date fishingActivityDate;
    EditText latitude;
    EditText longitude;
    EditText icesArea;
    List<Gear> gearList;
    ArrayAdapter<Gear> gearAdapter;
    Spinner gear;
    String gearValue;
    EditText meshSize;
    List<CatchSpecies> speciesList;
    ArrayAdapter<CatchSpecies> speciesAdapter;
    Spinner species;
    String speciesValue;
    List<CatchState> stateList;
    ArrayAdapter<CatchState> stateAdapter;
    Spinner state;
    String stateValue;
    List<CatchPresentation> presentationList;
    ArrayAdapter<CatchPresentation> presentationAdapter;
    Spinner presentation;
    String presentationValue;
    EditText weight;
    CheckBox dis;
    CheckBox bms;
    EditText numberOfPotsHauled;
    TextView landingOrDiscardDateDisplay;
    Date landingOrDiscardDate;
    EditText transporterRegEtc;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_edit_fish_1_form_row);

        db = CatchDatabase.getInstance(getApplicationContext());

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        this.processIntent();

        this.buildForm();
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

    private void buildForm() {

        this.loadOptions();

        fishingActivityDateDisplay = (TextView) findViewById(R.id.fishing_activity_date);
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
        landingOrDiscardDateDisplay = (TextView) findViewById(R.id.landing_or_discard_date);
        transporterRegEtc = (EditText) findViewById(R.id.transporter_reg_etc);

        saveButton = (Button) findViewById(R.id.save_form_row_button);

        this.applyExistingValues();

        this.setSaveListener();
    }

    private void applyExistingValues() {
        if (fish1FormRow != null && fish1FormRow.getFishingActivityDate() != null) {
            fishingActivityDate = fish1FormRow.getFishingActivityDate();
            this.updateDateDisplay(fishingActivityDate, fishingActivityDateDisplay, "Fishing Activity Date: ");
        }
        if (fish1FormRow != null) {
            latitude.setText(Double.toString(fish1FormRow.getLatitude()));
            longitude.setText(Double.toString(fish1FormRow.getLongitude()));
            for (int i = 0; i < gearAdapter.getCount(); i++) {
                if (gearAdapter.getItem(i).getId() == fish1FormRow.getGearId())
                    gear.setSelection(i);
            }
            meshSize.setText(Integer.toString(fish1FormRow.getMeshSize()));
            for (int i = 0; i < speciesAdapter.getCount(); i++) {
                if (speciesAdapter.getItem(i).getId() == fish1FormRow.getSpeciesId())
                    species.setSelection(i);
            }
            for (int i = 0; i < stateAdapter.getCount(); i++) {
                if (stateAdapter.getItem(i).getId() == fish1FormRow.getStateId())
                    state.setSelection(i);
            }
            for (int i = 0; i < presentationAdapter.getCount(); i++) {
                if (presentationAdapter.getItem(i).getId() == fish1FormRow.getPresentationId())
                    presentation.setSelection(i);
            }
            weight.setText(Double.toString(fish1FormRow.getWeight()));
            dis.setChecked(fish1FormRow.isDis());
            bms.setChecked(fish1FormRow.isBms());
            numberOfPotsHauled.setText(Integer.toString(fish1FormRow.getNumberOfPotsHauled()));
        }
        if (fish1FormRow != null && fish1FormRow.getIcesArea() != null && !fish1FormRow.getIcesArea().equals(""))
            icesArea.setText(fish1FormRow.getIcesArea());
        if (fish1FormRow != null && fish1FormRow.getLandingOrDiscardDate() != null) {
            landingOrDiscardDate = fish1FormRow.getLandingOrDiscardDate();
            this.updateDateDisplay(landingOrDiscardDate, landingOrDiscardDateDisplay, "Landing or Discard Date: ");
        }
        if (fish1FormRow != null && fish1FormRow.getTransporterRegEtc() != null && !fish1FormRow.getTransporterRegEtc().equals(""))
            icesArea.setText(fish1FormRow.getTransporterRegEtc());
    }

    private void setSaveListener() {

        if (fish1FormRow == null) {
            if (fishingActivityDate != null
                    || !latitude.getText().toString().equals("")
                    || !longitude.getText().toString().equals("")
                    || !icesArea.getText().toString().equals("")
                    || !meshSize.getText().toString().equals("")
                    || !weight.getText().toString().equals("")
                    || !numberOfPotsHauled.getText().toString().equals("")
                    || landingOrDiscardDate != null
                    || !transporterRegEtc.getText().toString().equals("")) {

            }
        }
        else {

        }
    }

    private void updateDateDisplay(Date date, TextView display, String prefix) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String dateString = day + "-" + month + "-" + year;
        display.setText(prefix + dateString);
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
        DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "fishing_activity_date");
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

    public void onDateSet(DatePicker view, int year, int month, int day) {
        String tag = view.getTag().toString();
        Log.w("DatePicker", tag);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        if (tag == "fishing_activity_date") {
            this.fishingActivityDate = c.getTime();
            this.updateDateDisplay(fishingActivityDate, fishingActivityDateDisplay, "Fishing Activity Date: ");
        }
        else if (tag == "landing_or_discard_date") {
            this.landingOrDiscardDate = c.getTime();
            this.updateDateDisplay(landingOrDiscardDate, landingOrDiscardDateDisplay, "Landing or Discard Date: ");
        }
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), (EditFish1FormRowActivity) getActivity(), year, month, day);
            dialog.getDatePicker().setTag(this.getTag());
            return dialog;
        }
    }
}
