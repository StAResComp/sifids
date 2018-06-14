package uk.ac.masts.sifids.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.GridLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;
import uk.ac.masts.sifids.entities.Observation;
import uk.ac.masts.sifids.entities.ObservationClass;
import uk.ac.masts.sifids.entities.ObservationSpecies;
import uk.ac.masts.sifids.tasks.PostDataTask;

import static uk.ac.masts.sifids.activities.Fish1FormsActivity.PERMISSION_REQUEST_FINE_LOCATION;

public class RecordObservationActivity extends AppCompatActivityWithMenuBar implements View.OnClickListener, AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    CatchDatabase db;
    SharedPreferences prefs;
    ArrayList<LinearLayout> formSections;
    int currentSectionIndex = 0;
    ObservationClass animalSeen = null;
    ObservationSpecies speciesSeen = null;
    Date timeSeen = null;
    CatchLocation locationSeen = null;
    CatchLocation suggestedLocation = null;
    String latitudeDirectionValue = null;
    String longitudeDirectionValue = null;
    int numberSeen = 0;
    String observationNotes = null;
    Observation observation = null;

    /**
     * Runs when activity is created
     *
     * @param savedInstanceState Activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_record_observation);

        super.onCreate(savedInstanceState);

        //Set up the action bar/menu
        setupActionBar();

        //Initialise database
        this.db = CatchDatabase.getInstance(getApplicationContext());

        this.prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        this.setFormSections();

        Callable<List<ObservationClass>> c = new Callable<List<ObservationClass>>() {
            @Override
            public List<ObservationClass> call() {
                return db.catchDao().getObservationClasses();
            }
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<List<ObservationClass>> future = service.submit(c);
        try {
            List<ObservationClass> animals = future.get();
            for (ObservationClass animal : animals) {
                addAnimalToGrid(animal);
            }
        } catch (Exception e) {
        }

        this.doDirectionSpinners();
    }

    private void setFormSections() {
        formSections = new ArrayList<>();
        formSections.add(0, (LinearLayout) findViewById(R.id.obs_what_seen_section));
        formSections.add(1, (LinearLayout) findViewById(R.id.obs_species_section));
        formSections.add(2, (LinearLayout) findViewById(R.id.obs_time_section));
        formSections.add(3, (LinearLayout) findViewById(R.id.obs_location_section));
        formSections.add(4, (LinearLayout) findViewById(R.id.obs_count_section));
        formSections.add(5, (LinearLayout) findViewById(R.id.obs_notes_submit_section));
        formSections.add(6, (LinearLayout) findViewById(R.id.obs_post_submission_section));
    }

    private void nextSection() {
        nextSection(null);
    }

    public void nextSection(View v) {
        updateSectionsAfter(this.currentSectionIndex);
        formSections.get(currentSectionIndex).setVisibility(View.GONE);
        currentSectionIndex++;
        if (currentSectionIndex >= formSections.size()) {
            this.finish();
            this.startActivity(this.getIntent());
        }
        formSections.get(currentSectionIndex).setVisibility(View.VISIBLE);
        if (currentSectionIndex == 1 && skipSpeciesSection()) {
            nextSection();
        }
    }

    private void previousSection() {
        previousSection(null);
    }

    public void previousSection(View v) {
        if (currentSectionIndex > 0) {
            formSections.get(currentSectionIndex).setVisibility(View.GONE);
            currentSectionIndex--;
            formSections.get(currentSectionIndex).setVisibility(View.VISIBLE);
        }
        if (currentSectionIndex == 1 && skipSpeciesSection()) {
            previousSection();
        }
    }

    public boolean skipSpeciesSection() {
        if (animalSeen != null) {
            Callable<Integer> c = new Callable<Integer>() {
                @Override
                public Integer call() {
                    return db.catchDao().countObservationSpecies(animalSeen.getId());
                }
            };
            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<Integer> future = service.submit(c);
            try {
                Integer numSpecies = future.get();
                return numSpecies.equals(0);
            } catch (Exception e) {
            } finally {
                return false;
            }
        }
        return false;
    }

    private void addAnimalToGrid(ObservationClass animal) {

        ImageButton button = new ImageButton(this);
        button.setImageResource(
                this.getResources().getIdentifier(
                        animal.getName().toLowerCase().replace(" ", "_"),
                        "drawable", this.getPackageName()));
        button.setBackground(null);
        button.setId(animal.getId());
        button.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        button.setTag(animal);
        button.setPadding(dpToPx(1), 0, dpToPx(1), 0);
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        button.setLayoutParams(lllp);
        button.setContentDescription(animal.getName());
        button.setOnClickListener(this);

        TextView caption = new TextView(this);
        lllp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        caption.setLayoutParams(lllp);
        caption.setGravity(Gravity.CENTER);
        caption.setText(animal.getName());
        caption.setTag(animal);

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        GridLayout.LayoutParams gllp = new GridLayout.LayoutParams();
        gllp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        gllp.width = 0;
        gllp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 0.5f);
        wrapper.setLayoutParams(gllp);
        wrapper.setTag(animal);

        wrapper.addView(button);
        wrapper.addView(caption);
        ((GridLayout) findViewById(R.id.obs_animal_image_grid)).addView(wrapper);

    }

    private int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void doDirectionSpinners() {
        ArrayList<String> latitudeDirectionsList =
                new ArrayList<>(Arrays.asList(getString(R.string.n), getString(R.string.s)));
        ArrayAdapter<String> latitudeDirectionsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_activated_1, latitudeDirectionsList
        );
        latitudeDirectionsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        Spinner latitudeDirectionsSpinner = findViewById(R.id.latitude_direction);
        latitudeDirectionsSpinner.setAdapter(latitudeDirectionsAdapter);
        latitudeDirectionsSpinner.setOnItemSelectedListener(this);

        ArrayList<String> longitudeDirectionsList =
                new ArrayList<>(Arrays.asList(getString(R.string.e), getString(R.string.w)));
        ArrayAdapter<String> longitudeDirectionsAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_activated_1, longitudeDirectionsList
        );
        longitudeDirectionsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        Spinner longitudeDirectionsSpinner = findViewById(R.id.longitude_direction);
        longitudeDirectionsSpinner.setAdapter(longitudeDirectionsAdapter);
        longitudeDirectionsSpinner.setOnItemSelectedListener(this);
    }

    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag instanceof ObservationClass) {
            this.animalSeen = (ObservationClass) tag;
            this.nextSection();
        }
    }

    public void displaySpeciesList(View v) {
        findViewById(R.id.obs_species_selection_section).setVisibility(View.VISIBLE);

    }

    public void hideSpeciesList(View v) {
        findViewById(R.id.obs_species_selection_section).setVisibility(View.INVISIBLE);
        nextSection();
    }

    private void updateSectionsAfter(int index) {
        for (int i = index; i < formSections.size(); i++) {
            updateSection(i);
        }
    }

    private void updateSection(int index) {
        if (index == 1) {
            findViewById(R.id.obs_species_selection_section).setVisibility(View.INVISIBLE);
            if (animalSeen != null) {
                Callable<List<ObservationSpecies>> c = new Callable<List<ObservationSpecies>>() {
                    @Override
                    public List<ObservationSpecies> call() {
                        return db.catchDao().getObservationSpecies(animalSeen.getId());
                    }
                };
                ExecutorService service = Executors.newSingleThreadExecutor();
                Future<List<ObservationSpecies>> future = service.submit(c);
                List<ObservationSpecies> speciesList = new ArrayList<>();
                try {
                    speciesList = future.get();
                } catch (Exception e) {
                }
                speciesList.add(0, new ObservationSpecies());
                ArrayAdapter<ObservationSpecies> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_activated_1,
                        speciesList
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner spinner = findViewById(R.id.obs_species);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);
            }
        } else if (index == 3) {
            if (timeSeen != null) {
                Callable<CatchLocation> c = new Callable<CatchLocation>() {
                    @Override
                    public CatchLocation call() {
                        return db.catchDao().getLocationAt(timeSeen);
                    }
                };
                ExecutorService service = Executors.newSingleThreadExecutor();
                Future<CatchLocation> future = service.submit(c);
                try {
                    this.suggestedLocation = future.get();
                } catch (Exception e) {
                }
                Button suggestedLocationButton =
                        (Button) findViewById(R.id.obs_use_suggested_location);
                if (this.suggestedLocation != null) {
                    suggestedLocationButton.setText(
                            String.format(getString(R.string.observation_suggested_location),
                                    this.suggestedLocation.getCoordinates()));
                    suggestedLocationButton.setVisibility(View.VISIBLE);
                } else {
                    suggestedLocationButton.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Object item = parent.getItemAtPosition(pos);
        switch (parent.getId()) {
            case R.id.obs_species:
                if (pos > 0) {
                    this.speciesSeen = (ObservationSpecies) item;
                    nextSection();
                    break;
                }
            case R.id.latitude_direction:
                if (item instanceof String) {
                    this.latitudeDirectionValue = (String) item;
                }
                break;
            case R.id.longitude_direction:
                if (item instanceof String) {
                    this.longitudeDirectionValue = (String) item;
                }
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void setTimeNow(View view) {
        this.timeSeen = new Date();
        nextSection();
    }

    public void showObservationTimePickerDialog(View view) {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(), "time_seen");
    }

    public static class TimePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of DatePickerDialog and return it
            TimePickerDialog dialog =
                    new TimePickerDialog(
                            getActivity(),
                            (RecordObservationActivity) getActivity(),
                            hour, minute, true);
            return dialog;
        }
    }

    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        this.timeSeen = c.getTime();
        nextSection();
    }

    public void useSuggestedLocation(View view) {
        this.locationSeen = this.suggestedLocation;
        nextSection();
    }

    public void useCurrentLocation(View view) {
        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_FINE_LOCATION);
        } else {
            FusedLocationProviderClient fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this,
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                locationSeen = new CatchLocation();
                                locationSeen.setLatitude(location.getLatitude());
                                locationSeen.setLongitude(location.getLongitude());
                            } else {
                                Toast.makeText(getBaseContext(), "Unable to get current location",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            if (this.locationSeen != null) {
                nextSection();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (
                requestCode == PERMISSION_REQUEST_FINE_LOCATION
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            useCurrentLocation(null);
        }
    }

    public void showObservationPlacePickerDialog(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this)
                    , 236);
        } catch (GooglePlayServicesRepairableException
                | GooglePlayServicesNotAvailableException e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 236) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.locationSeen = new CatchLocation();
                this.locationSeen.setLatitude(place.getLatLng().latitude);
                this.locationSeen.setLongitude(place.getLatLng().longitude);
                nextSection();
            }
        }
    }

    public void showLatLongInputs(View view) {
        LinearLayout latLongInputSection =
                (LinearLayout) findViewById(R.id.obs_manual_location_section);
        if (latLongInputSection.getVisibility() == View.INVISIBLE
                || latLongInputSection.getVisibility() == View.GONE) {
            latLongInputSection.setVisibility(View.VISIBLE);
        } else {
            latLongInputSection.setVisibility(View.INVISIBLE);
        }
    }

    public void submitManualLatLong(View view) {
        this.locationSeen = new CatchLocation();
        locationSeen.setLatitude(
                Integer.parseInt(((EditText) findViewById(R.id.latitude_degrees)).getText().toString()),
                Integer.parseInt(((EditText) findViewById(R.id.latitude_minutes)).getText().toString()),
                latitudeDirectionValue.charAt(0)
        );
        locationSeen.setLongitude(
                Integer.parseInt(((EditText) findViewById(R.id.longitude_degrees)).getText().toString()),
                Integer.parseInt(((EditText) findViewById(R.id.longitude_minutes)).getText().toString()),
                longitudeDirectionValue.charAt(0)
        );
        nextSection();
    }

    public void submitCount(View view) {
        this.numberSeen = Integer.parseInt(view.getTag().toString());
        nextSection();
    }

    public void submitObservation(View view) {
        this.observationNotes = ((EditText) findViewById(R.id.obs_notes)).getText().toString();
        if (
                this.animalSeen != null
                        && this.timeSeen != null
                        && this.locationSeen != null
                        && this.numberSeen != 0
                ) {
            this.observation = new Observation();
            observation.setObservationClassId(this.animalSeen.getId());
            if (this.speciesSeen != null) {
                observation.setObservationSpeciesId(this.speciesSeen.getId());
            }
            observation.setTimestamp(this.timeSeen);
            observation.setLatitude(this.locationSeen.getLatitude());
            observation.setLongitude(this.locationSeen.getLongitude());
            observation.setCount(this.numberSeen);
            observation.setNotes(this.observationNotes);
            this.persistObservation();
            PostDataTask.postObservation(this, observation, new PostDataTask.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    db.catchDao().markObservationSubmitted(observation.getId());
                    updateSubmissionMessage(true);
                    Toast.makeText(RecordObservationActivity.this,
                            "Observation successfully submitted.",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String result) {
                    updateSubmissionMessage(false);
                    Toast.makeText(RecordObservationActivity.this,
                            "Error submitting observation. Will try again later.",
                            Toast.LENGTH_LONG).show();
                }
            });
            nextSection();
        } else {
            Toast.makeText(getBaseContext(),
                    "Insufficient information supplied. Please go back and try again",
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void updateSubmissionMessage(boolean success) {
        TextView msgView = findViewById(R.id.obs_submission_msg);
        int[] submissionDetails = {0,0};
        Callable<int[]> c = new Callable<int[]>() {
            @Override
            public int[] call() {
                int[] submissionDetails = new int[2];
                submissionDetails[0] = db.catchDao().countSubmittedObservations();
                submissionDetails[1] = db.catchDao().countUnsubmittedObservations();
                return submissionDetails;
            }
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<int[]> future = service.submit(c);
        try {
            submissionDetails = future.get();
        } catch (Exception e) {
        }
        String msg = String.format(getString(R.string.observation_thank_you),
                (success ? getString(R.string.observation_submission_successful) :
                        getString(R.string.observation_submission_unsuccessful)),
                Integer.toString(submissionDetails[0]), Integer.toString(submissionDetails[1]));
        msgView.setText(msg);
    }

    private void persistObservation() {
        if (this.observation != null) {
            Callable<Long> c = new Callable<Long>() {
                @Override
                public Long call() {
                    return db.catchDao().insertObservation(observation);
                }
            };
            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<Long> future = service.submit(c);
            try {
                long observationId = future.get();
                observation.setId((int) observationId);
            } catch (Exception e) {
            }
        }
    }

    public void reload(View v) {
        this.finish();
        this.startActivity(this.getIntent());
    }

    public void goHome(View v) {
        this.finish();
    }
}
