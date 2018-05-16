package uk.ac.masts.sifids.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;

public abstract class EditingActivity extends AppCompatActivityWithMenuBar {

    //Data Sources
    CatchDatabase db;
    SharedPreferences prefs;

    /**
     * Runs when activity is created
     *
     * @param savedInstanceState Activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Set up the action bar/menu
        setupActionBar();

        //Initialise database
        this.db = CatchDatabase.getInstance(getApplicationContext());

        //Get user preferences
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Handle whatever has been passed to this by previous Activity
        this.processIntent();

        //Put the user interface form together
        this.buildForm();
    }

    abstract void processIntent();

    abstract void buildForm();
}
