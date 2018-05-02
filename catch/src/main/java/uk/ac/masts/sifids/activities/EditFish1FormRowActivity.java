package uk.ac.masts.sifids.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;
import uk.ac.masts.sifids.entities.CatchPresentation;
import uk.ac.masts.sifids.entities.CatchSpecies;
import uk.ac.masts.sifids.entities.CatchState;
import uk.ac.masts.sifids.entities.EntityWithId;
import uk.ac.masts.sifids.entities.Fish1FormRow;
import uk.ac.masts.sifids.entities.Gear;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class EditFish1FormRowActivity extends AppCompatActivityWithMenuBar implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    Fish1FormRow fish1FormRow;

    CatchDatabase db;

    int formId;

    TextView fishingActivityDateDisplay;
    Date fishingActivityDate;
    EditText latitudeDegrees;
    EditText latitudeMinutes;
    String latitudeDirectionValue;
    EditText longitudeDegrees;
    EditText longitudeMinutes;
    String longitudeDirectionValue;
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
    Button deleteButton;

    Map<String,Spinner> spinners;
    Map<String,Adapter> adapters;
    Map<String,List> spinnerLists;

    final String GEAR_KEY = "gear";
    final String SPECIES_KEY = "species";
    final String STATE_KEY = "state";
    final String PRESENTATION_KEY = "presentation";
    final String LATITUDE_DIRECTION_KEY = "latitude_direction";
    final String LONGITUDE_DIRECTION_KEY = "longitude_direction";

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_edit_fish_1_form_row);

        db = CatchDatabase.getInstance(getApplicationContext());

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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
        this.spinnerLists.put(LATITUDE_DIRECTION_KEY, new ArrayList<String>(Arrays.asList("N", "S")));
        this.spinnerLists.put(LONGITUDE_DIRECTION_KEY, new ArrayList<String>(Arrays.asList("E", "W")));

        this.adapters = new HashMap();
        this.spinners = new HashMap();

        this.loadOptions();

        fishingActivityDateDisplay = (TextView) findViewById(R.id.fishing_activity_date);
        latitudeDegrees = (EditText) findViewById(R.id.latitude_degrees);
        latitudeMinutes = (EditText) findViewById(R.id.latitude_minutes);
        this.createSpinner(LATITUDE_DIRECTION_KEY, R.id.latitude_direction);
        longitudeDegrees = (EditText) findViewById(R.id.longitude_degrees);
        longitudeMinutes = (EditText) findViewById(R.id.longitude_minutes);
        this.createSpinner(LONGITUDE_DIRECTION_KEY, R.id.longitude_direction);
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
        deleteButton = (Button) findViewById(R.id.delete_form_row_button);

        this.applyExistingValues();

        this.setListeners();
    }

    private void applyExistingValues() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (fish1FormRow != null && fish1FormRow.getFishingActivityDate() != null) {
            fishingActivityDate = fish1FormRow.getFishingActivityDate();
            this.updateDateDisplay(fishingActivityDate, fishingActivityDateDisplay, "Fishing Activity Date: ");
        }
        if (fish1FormRow != null) {
            formId = fish1FormRow.getFormId();
            latitudeDegrees.setText(Integer.toString(CatchLocation.getLatitudeDegrees(fish1FormRow.getLatitude())));
            latitudeMinutes.setText(Integer.toString(CatchLocation.getLatitudeMinutes(fish1FormRow.getLatitude())));
            for (int i = 0; i < adapters.get(LATITUDE_DIRECTION_KEY).getCount(); i++) {
                if (((String) adapters.get(LATITUDE_DIRECTION_KEY).getItem(i)).charAt(0) == CatchLocation.getLatitudeDirection(fish1FormRow.getLatitude())) {
                    spinners.get(LATITUDE_DIRECTION_KEY).setSelection(i);
                }
            }
            longitudeDegrees.setText(Integer.toString(CatchLocation.getLongitudeDegrees(fish1FormRow.getLongitude())));
            longitudeMinutes.setText(Integer.toString(CatchLocation.getLongitudeMinutes(fish1FormRow.getLongitude())));
            for (int i = 0; i < adapters.get(LONGITUDE_DIRECTION_KEY).getCount(); i++) {
                if (((String) adapters.get(LONGITUDE_DIRECTION_KEY).getItem(i)).charAt(0) == CatchLocation.getLongitudeDirection(fish1FormRow.getLongitude())) {
                    spinners.get(LONGITUDE_DIRECTION_KEY).setSelection(i);
                }
            }
            for (int i = 0; i < adapters.get(SPECIES_KEY).getCount(); i++) {
                if (fish1FormRow.getSpeciesId() != null && ((CatchSpecies) adapters.get(SPECIES_KEY).getItem(i)).getId() == fish1FormRow.getSpeciesId())
                    spinners.get(SPECIES_KEY).setSelection(i);
            }
            for (int i = 0; i < adapters.get(STATE_KEY).getCount(); i++) {
                if (fish1FormRow.getStateId() != null && ((CatchState) adapters.get(STATE_KEY).getItem(i)).getId() == fish1FormRow.getStateId())
                    spinners.get(STATE_KEY).setSelection(i);
            }
            for (int i = 0; i < adapters.get(PRESENTATION_KEY).getCount(); i++) {
                if (fish1FormRow.getPresentationId() != null && ((CatchPresentation) adapters.get(PRESENTATION_KEY).getItem(i)).getId() == fish1FormRow.getPresentationId())
                    spinners.get(PRESENTATION_KEY).setSelection(i);
            }
            weight.setText(Double.toString(fish1FormRow.getWeight()));
            dis.setChecked(fish1FormRow.isDis());
            bms.setChecked(fish1FormRow.isBms());
            numberOfPotsHauled.setText(Integer.toString(fish1FormRow.getNumberOfPotsHauled()));
            for (int i = 0; i < adapters.get(GEAR_KEY).getCount(); i++) {
                if (fish1FormRow.getGearId() != null && ((Gear) adapters.get(GEAR_KEY).getItem(i)).getId() == fish1FormRow.getGearId())
                    spinners.get(GEAR_KEY).setSelection(i);
            }
            meshSize.setText(Integer.toString(fish1FormRow.getMeshSize()));
        }
        else {
            meshSize.setText(prefs.getString("pref_mesh_size", ""));
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

    private void setListeners() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean create = false;

                if (fish1FormRow == null  && formId != 0) {
                    create = true;
                    fish1FormRow = new Fish1FormRow();
                    fish1FormRow.setFormId(formId);
                }
                if (
                        fish1FormRow.setFishingActivityDate(fishingActivityDate)
                        || fish1FormRow.setLatitude(
                                CatchLocation.getDecimalCoordinate(
                                        Integer.parseInt(latitudeDegrees.getText().toString()),
                                        Integer.parseInt(latitudeDegrees.getText().toString()),
                                        latitudeDirectionValue
                                )
                        )
                                || fish1FormRow.setLongitude(
                                CatchLocation.getDecimalCoordinate(
                                        Integer.parseInt(longitudeDegrees.getText().toString()),
                                        Integer.parseInt(longitudeDegrees.getText().toString()),
                                        longitudeDirectionValue
                                )
                        )
                        || fish1FormRow.setIcesArea(icesArea.getText().toString())
                        || fish1FormRow.setGearId(gearIdValue)
                        || fish1FormRow.setMeshSize(Integer.parseInt(meshSize.getText().toString()))
                        || fish1FormRow.setSpeciesId(speciesIdValue)
                        || fish1FormRow.setStateId(stateIdValue)
                        || fish1FormRow.setPresentationId(presentationIdValue)
                        || fish1FormRow.setWeight(Double.parseDouble(weight.getText().toString()))
                        || fish1FormRow.setDis(dis.isChecked())
                        || fish1FormRow.setBms(bms.isChecked())
                        || fish1FormRow.setNumberOfPotsHauled(Integer.parseInt(numberOfPotsHauled.getText().toString()))
                        || fish1FormRow.setLandingOrDiscardDate(landingOrDiscardDate)
                        ) {
                    if (create) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                db.catchDao().insertFish1FormRows(fish1FormRow);
                            }
                        });
                    }
                    else {
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFormRow();
            }
        });
    }

    private void deleteFormRow() {

        if (fish1FormRow != null) {
            this.confirmDialog();
        }
        else {
            Intent i = new Intent(this, EditFish1FormActivity.class);
            i.putExtra("id", EditFish1FormRowActivity.this.formId);
            this.finish();
            this.startActivity(i);
        }
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Are you sure you want to delete this row?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                db.catchDao().deleteFish1FormRow(fish1FormRow.getId());
                            }
                        };
                        Thread newThread= new Thread(r);
                        newThread.start();
                        try {
                            newThread.join();
                        }
                        catch (InterruptedException ie) {

                        }
                        Intent i = new Intent(EditFish1FormRowActivity.this, EditFish1FormActivity.class);
                        i.putExtra("id", EditFish1FormRowActivity.this.formId);
                        EditFish1FormRowActivity.this.finish();
                        EditFish1FormRowActivity.this.startActivity(i);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
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
                EditFish1FormRowActivity.this.spinnerLists.put(EditFish1FormRowActivity.this.GEAR_KEY, EditFish1FormRowActivity.this.db.catchDao().getGear(prefs.getStringSet("pref_gear", new HashSet<String>())));
                List speciesList = EditFish1FormRowActivity.this.db.catchDao().getSpecies();
                for (String idString : prefs.getStringSet("pref_species", new HashSet<String>())) {
                    speciesList = EditFish1FormRowActivity.rearrangeList(speciesList, Integer.parseInt(idString));
                }
                EditFish1FormRowActivity.this.spinnerLists.put(EditFish1FormRowActivity.this.SPECIES_KEY, speciesList);
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

    private static List<EntityWithId> rearrangeList(List<EntityWithId> list, int id) {
        Iterator<EntityWithId> it = list.iterator();
        while (it.hasNext()) {
            EntityWithId item = it.next();
            if (item.getId() == id) {
                it.remove();
                list.add(0, item);
                return list;
            }
        }
        return list;
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
            case R.id.latitude_direction:
                this.latitudeDirectionValue = ((String)parent.getItemAtPosition(pos));
                break;
            case R.id.longitude_direction:
                this.longitudeDirectionValue = ((String)parent.getItemAtPosition(pos));
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
