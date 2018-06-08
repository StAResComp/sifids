package uk.ac.masts.sifids.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.ObservationClass;

public class RecordObservationActivity extends AppCompatActivityWithMenuBar implements View.OnClickListener {

    CatchDatabase db;
    ArrayList<LinearLayout> formSections;
    int currentSectionIndex = 0;
    ObservationClass animalSeen = null;

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

        Callable<List<ObservationClass>> c = new Callable<List<ObservationClass>>() {
            @Override
            public List<ObservationClass> call() {
                return db.catchDao().getObservationClasses();
            }
        };
        ExecutorService service =  Executors.newSingleThreadExecutor();
        Future<List<ObservationClass>> future = service.submit(c);
        try {
            List<ObservationClass> animals = future.get();
            Log.e("REC_OBS", "Found " + animals.size() + " amimals");
            for (ObservationClass animal : animals) {
                addAnimalToGrid(animal);
            }
        }
        catch (Exception e) {}
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

    private void addAnimalToGrid(ObservationClass animal) {

        Log.e("REC_OBS", "Adding " + animal.getName() + " to grid");

        Log.e("REC_OBS", "Creating button");
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

        Log.e("REC_OBS", "Creating caption");
        TextView caption = new TextView(this);
        lllp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        caption.setLayoutParams(lllp);
        caption.setGravity(Gravity.CENTER);
        caption.setText(animal.getName());
        caption.setTag(animal);

        Log.e("REC_OBS", "Creating wrapper");
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        GridLayout.LayoutParams gllp = new GridLayout.LayoutParams();
        gllp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        gllp.width = 0;
        gllp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 0.5f);
        wrapper.setLayoutParams(gllp);
        wrapper.setTag(animal);

        Log.e("REC_OBS", "Adding views");
        wrapper.addView(button);
        wrapper.addView(caption);
        ((GridLayout) findViewById(R.id.obs_animal_image_grid)).addView(wrapper);

    }

    private int dpToPx(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag instanceof ObservationClass) {
            this.animalSeen = (ObservationClass) tag;
            this.nextSection();
        }
    }
}
