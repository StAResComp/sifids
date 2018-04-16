package uk.ac.masts.sifids.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    int gearIdValue;
    EditText meshSize;
    int speciesIdValue;
    int stateIdValue;
    int presentationIdValue;
    EditText weight;
    CheckBox dis;
    CheckBox bms;
    EditText numberOfPotsHauled;
    TextView landingOrDiscardDateDisplay;
    Date landingOrDiscardDate;
    EditText transporterRegEtc;
    Button saveButton;

    Map<String,Spinner> spinners;
    Map<String,Adapter> adapters;
    Map<String,List> spinnerLists;

    final String GEAR_KEY = "gear";
    final String SPECIES_KEY = "species";
    final String STATE_KEY = "state";
    final String PRESENTATION_KEY = "presentation";

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

        this.spinnerLists = new HashMap();

        this.spinnerLists.put(GEAR_KEY, new ArrayList<Gear>());
        this.spinnerLists.put(SPECIES_KEY, new ArrayList<CatchSpecies>());
        this.spinnerLists.put(STATE_KEY, new ArrayList<CatchState>());
        this.spinnerLists.put(PRESENTATION_KEY, new ArrayList<CatchPresentation>());

        this.adapters = new HashMap();
        this.spinners = new HashMap();

        this.loadOptions();

        fishingActivityDateDisplay = (TextView) findViewById(R.id.fishing_activity_date);
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        icesArea = (EditText) findViewById(R.id.ices_area);
        this.createSpinner(GEAR_KEY, R.id.gear);
        meshSize = (EditText) findViewById(R.id.mesh_size);
        this.createSpinner(SPECIES_KEY, R.id.species);
        this.createSpinner(STATE_KEY, R.id.state);
        this.createSpinner(PRESENTATION_KEY, R.id.presentation);
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (fish1FormRow != null && fish1FormRow.getFishingActivityDate() != null) {
            fishingActivityDate = fish1FormRow.getFishingActivityDate();
            this.updateDateDisplay(fishingActivityDate, fishingActivityDateDisplay, "Fishing Activity Date: ");
        }
        if (fish1FormRow != null) {
            formId = fish1FormRow.getFormId();
            latitude.setText(Double.toString(fish1FormRow.getLatitude()));
            longitude.setText(Double.toString(fish1FormRow.getLongitude()));
            for (int i = 0; i < adapters.get(GEAR_KEY).getCount(); i++) {
                if (((Gear) adapters.get(GEAR_KEY).getItem(i)).getId() == fish1FormRow.getGearId())
                    spinners.get(GEAR_KEY).setSelection(i);
            }
            for (int i = 0; i < adapters.get(SPECIES_KEY).getCount(); i++) {
                if (((CatchSpecies) adapters.get(SPECIES_KEY).getItem(i)).getId() == fish1FormRow.getSpeciesId())
                    spinners.get(SPECIES_KEY).setSelection(i);
            }
            for (int i = 0; i < adapters.get(STATE_KEY).getCount(); i++) {
                if (((CatchState) adapters.get(STATE_KEY).getItem(i)).getId() == fish1FormRow.getStateId())
                    spinners.get(STATE_KEY).setSelection(i);
            }
            for (int i = 0; i < adapters.get(PRESENTATION_KEY).getCount(); i++) {
                if (((CatchPresentation) adapters.get(PRESENTATION_KEY).getItem(i)).getId() == fish1FormRow.getPresentationId())
                    spinners.get(PRESENTATION_KEY).setSelection(i);
            }
            weight.setText(Double.toString(fish1FormRow.getWeight()));
            dis.setChecked(fish1FormRow.isDis());
            bms.setChecked(fish1FormRow.isBms());
            numberOfPotsHauled.setText(Integer.toString(fish1FormRow.getNumberOfPotsHauled()));
        }
        if (fish1FormRow != null) {
            for (int i = 0; i < adapters.get(GEAR_KEY).getCount(); i++) {
                if (((Gear) adapters.get(GEAR_KEY).getItem(i)).getId() == fish1FormRow.getGearId())
                    spinners.get(GEAR_KEY).setSelection(i);
            }
        }
        else {
            for (int i = 0; i < adapters.get(GEAR_KEY).getCount(); i++) {
                if (((Gear) adapters.get(GEAR_KEY).getItem(i)).getId() == Integer.parseInt(prefs.getString("pref_gear", "")));
                    spinners.get(GEAR_KEY).setSelection(i);
            }
        }
        if (fish1FormRow != null) meshSize.setText(Integer.toString(fish1FormRow.getMeshSize()));
        else meshSize.setText(prefs.getString("pref_mesh_size", ""));
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        if (formId != 0) {
                            fish1FormRow = new Fish1FormRow();
                            fish1FormRow.setFormId(formId);
                            fish1FormRow.setFishingActivityDate(fishingActivityDate);
                            double latitudeDbl;
                            try {
                                latitudeDbl = Double.parseDouble(latitude.getText().toString());
                            } catch (Exception e) {
                                latitudeDbl = 100;
                            }
                            fish1FormRow.setLatitude(latitudeDbl);
                            double longitudeDbl;
                            try {
                                longitudeDbl = Double.parseDouble(longitude.getText().toString());
                            } catch (Exception e) {
                                longitudeDbl = 100;
                            }
                            fish1FormRow.setLongitude(longitudeDbl);
                            fish1FormRow.setIcesArea(icesArea.getText().toString());
                            fish1FormRow.setGearId(gearIdValue);
                            int meshSizeInt;
                            try {
                                meshSizeInt = Integer.parseInt(meshSize.getText().toString());
                            } catch (Exception e) {
                                meshSizeInt = 0;
                            }
                            fish1FormRow.setMeshSize(meshSizeInt);
                            fish1FormRow.setSpeciesId(speciesIdValue);
                            fish1FormRow.setStateId(stateIdValue);
                            fish1FormRow.setPresentationId(presentationIdValue);
                            double weightDbl;
                            try {
                                weightDbl = Double.parseDouble(weight.getText().toString());
                            } catch (Exception e) {
                                weightDbl = 100;
                            }
                            fish1FormRow.setWeight(weightDbl);
                            fish1FormRow.setDis(dis.isChecked());
                            fish1FormRow.setBms(bms.isChecked());
                            int numberOfPotsHauledInt;
                            try {
                                numberOfPotsHauledInt = Integer.parseInt(numberOfPotsHauled.getText().toString());
                            } catch (Exception e) {
                                numberOfPotsHauledInt = 0;
                            }
                            fish1FormRow.setNumberOfPotsHauled(numberOfPotsHauledInt);
                            fish1FormRow.setLandingOrDiscardDate(landingOrDiscardDate);

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    db.catchDao().insertFish1FormRows(fish1FormRow);
                                }
                            });
                        }
                    }
                } else {

                    boolean changes = false;

                    if (fishingActivityDate != null && !fishingActivityDate.equals(fish1FormRow.getFishingActivityDate())) {
                        fish1FormRow.setFishingActivityDate(fishingActivityDate);
                        changes = true;
                    }
                    if (Double.parseDouble(latitude.getText().toString()) != fish1FormRow.getLatitude()) {
                        fish1FormRow.setLatitude(Double.parseDouble(latitude.getText().toString()));
                        changes = true;
                    }
                    if (Double.parseDouble(longitude.getText().toString()) != fish1FormRow.getLongitude()) {
                        fish1FormRow.setLongitude(Double.parseDouble(longitude.getText().toString()));
                        changes = true;
                    }
                    if (!icesArea.getText().toString().equals(fish1FormRow.getIcesArea())) {
                        fish1FormRow.setIcesArea(icesArea.getText().toString());
                        changes = true;
                    }
                    if (gearIdValue != fish1FormRow.getGearId()) {
                        fish1FormRow.setGearId(gearIdValue);
                        changes = true;
                    }
                    if (Integer.parseInt(meshSize.getText().toString()) != fish1FormRow.getMeshSize()) {
                        fish1FormRow.setMeshSize(Integer.parseInt(meshSize.getText().toString()));
                        changes = true;
                    }
                    if (speciesIdValue != fish1FormRow.getSpeciesId()) {
                        fish1FormRow.setSpeciesId(speciesIdValue);
                        changes = true;
                    }
                    if (stateIdValue != fish1FormRow.getStateId()) {
                        fish1FormRow.setSpeciesId(stateIdValue);
                        changes = true;
                    }
                    if (presentationIdValue != fish1FormRow.getPresentationId()) {
                        fish1FormRow.setSpeciesId(presentationIdValue);
                        changes = true;
                    }
                    if (Double.parseDouble(weight.getText().toString()) != fish1FormRow.getWeight()) {
                        fish1FormRow.setWeight(Double.parseDouble(weight.getText().toString()));
                        changes = true;
                    }
                    if (dis.isChecked() != fish1FormRow.isDis()) {
                        fish1FormRow.setDis(dis.isChecked());
                        changes = true;
                    }
                    if (bms.isChecked() != fish1FormRow.isBms()) {
                        fish1FormRow.setBms(bms.isChecked());
                        changes = true;
                    }
                    if (Integer.parseInt(numberOfPotsHauled.getText().toString()) != fish1FormRow.getNumberOfPotsHauled()) {
                        fish1FormRow.setNumberOfPotsHauled(Integer.parseInt(numberOfPotsHauled.getText().toString()));
                        changes = true;
                    }
                    if (!transporterRegEtc.getText().toString().equals(fish1FormRow.getTransporterRegEtc())) {
                        fish1FormRow.setTransporterRegEtc(transporterRegEtc.getText().toString());
                        changes = true;
                    }

                    if (changes) {

                        //save the item before leaving the activity

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                db.catchDao().updateFish1FormRows(fish1FormRow);
                            }
                        });

                    }

                }

                Intent i = new Intent(EditFish1FormRowActivity.this, EditFish1FormActivity.class);
                i.putExtra("id", EditFish1FormRowActivity.this.formId);
                EditFish1FormRowActivity.this.startActivity(i);
            }
        });
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
                EditFish1FormRowActivity.this.spinnerLists.put(EditFish1FormRowActivity.this.GEAR_KEY, EditFish1FormRowActivity.this.db.catchDao().getGear());
                EditFish1FormRowActivity.this.spinnerLists.put(EditFish1FormRowActivity.this.SPECIES_KEY, EditFish1FormRowActivity.this.db.catchDao().getSpecies());
                EditFish1FormRowActivity.this.spinnerLists.put(EditFish1FormRowActivity.this.STATE_KEY, EditFish1FormRowActivity.this.db.catchDao().getStates());
                EditFish1FormRowActivity.this.spinnerLists.put(EditFish1FormRowActivity.this.PRESENTATION_KEY, EditFish1FormRowActivity.this.db.catchDao().getPresentations());
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

    private void createSpinner(String mapKey, int widgetId) {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, this.spinnerLists.get(mapKey));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.adapters.put(mapKey,adapter);
        Spinner spinner = (Spinner) findViewById(widgetId);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        this.spinners.put(mapKey,spinner);
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
        switch(parent.getId()) {
            case R.id.gear:
                this.gearIdValue = ((Gear)parent.getItemAtPosition(pos)).getId();
                break;
            case R.id.species:
                this.speciesIdValue = ((CatchSpecies)parent.getItemAtPosition(pos)).getId();
                break;
            case R.id.state:
                this.stateIdValue = ((CatchState)parent.getItemAtPosition(pos)).getId();
                break;
            case R.id.presentation:
                this.presentationIdValue = ((CatchPresentation)parent.getItemAtPosition(pos)).getId();
                break;
        }
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
