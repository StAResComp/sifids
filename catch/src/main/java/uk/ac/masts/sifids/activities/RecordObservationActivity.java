package uk.ac.masts.sifids.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;

public class RecordObservationActivity extends AppCompatActivityWithMenuBar {

    CatchDatabase db;
    ArrayList<LinearLayout> formSections;
    int currentSectionIndex = 0;

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

        this.setFormSections();

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
        formSections.get(currentSectionIndex).setVisibility(View.GONE);
        currentSectionIndex++;
        if (currentSectionIndex >= formSections.size()) {
            currentSectionIndex = 0;
        }
        formSections.get(currentSectionIndex).setVisibility(View.VISIBLE);
    }

    private void previousSection() {
        if (currentSectionIndex > 0) {
            formSections.get(currentSectionIndex).setVisibility(View.GONE);
            currentSectionIndex--;
            formSections.get(currentSectionIndex).setVisibility(View.VISIBLE);
        }
    }
}
