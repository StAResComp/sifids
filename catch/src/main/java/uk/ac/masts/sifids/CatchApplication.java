package uk.ac.masts.sifids;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import uk.ac.masts.sifids.activities.SettingsActivity;
import uk.ac.masts.sifids.receivers.AlarmReceiver;

/**
 * This class is extends android.app.Application to provide a flag which can be used to indicate
 * whether or not the user is currently fishing and which can be set and inspected from anywhere in
 * the app.
 */
public class CatchApplication extends Application {

    private boolean fishing = false;

    private boolean trackingLocation = false;

    /**
     * Indicates whether or not the user is currently fishing.
     *
     * @return whether or not the user is currently fishing
     */
    public boolean isFishing() {
        return fishing;
    }

    /**
     * Set whether or not the user is currently fishing.
     *
     * @param fishing whether or not the user is currently fishing
     */
    public void setFishing(boolean fishing) {
        this.fishing = fishing;
    }

    public boolean isTrackingLocation() {
        return trackingLocation;
    }

    public void setTrackingLocation(boolean trackingLocation) {
        this.trackingLocation = trackingLocation;
    }

    /**
     * From https://stackoverflow.com/a/30274315/634170
     */
    public void checkFirstRun() {

        final String PREF_VERSION_CODE_KEY = getString(R.string.pref_version_code_key);
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            Intent intent = new Intent(this, SettingsActivity.class);
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
            startActivity(intent);
            return;

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = this.getApplicationContext();
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent =
                PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                SystemClock.elapsedRealtime() + 5000,
                AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
    }
}
