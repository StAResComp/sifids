package uk.ac.masts.sifids.activities;

import android.os.Bundle;
import android.widget.TextView;

import uk.ac.masts.sifids.CatchApplication;
import uk.ac.masts.sifids.R;

public class AboutActivity extends AppCompatActivityWithMenuBar {

    /**
     * Runs when activity is created
     *
     * @param savedInstanceState Activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        super.onCreate(savedInstanceState);
        setupActionBar();
        TextView versionText = findViewById(R.id.version_text);
        versionText.setText(
                String.format(getString(R.string.about_version_current_version),
                        CatchApplication.VERSION)
        );
    }

}
