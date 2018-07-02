package uk.ac.masts.sifids;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.TimeZone;

import uk.ac.masts.sifids.activities.SettingsActivity;
import uk.ac.masts.sifids.receivers.AlarmReceiver;
import uk.ac.masts.sifids.services.CatchLocationService;

/**
 * This class is extends android.app.Application to provide a flag which can be used to indicate
 * whether or not the user is currently fishing and which can be set and inspected from anywhere in
 * the app.
 */
public class CatchApplication extends Application {

    public final static String VERSION = "0.6";

    public final static TimeZone UTC = TimeZone.getTimeZone("UTC");

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
        if (this.isTrackingLocation()) {
            ServiceConnection connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    CatchLocationService locationService = ((CatchLocationService.CatchLocationBinder) service).getService();
                    locationService.saveFirstFishingLocation();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
            bindService(new Intent(this, CatchLocationService.class), connection, Context.BIND_AUTO_CREATE);
        }
    }

    public boolean isTrackingLocation() {
        return trackingLocation;
    }

    public void setTrackingLocation(boolean trackingLocation) {
        this.trackingLocation = trackingLocation;
    }

    public void redirectIfNecessary() {
        if (!hasConsented()) {
            Toast.makeText(getBaseContext(), getString(R.string.need_to_consent),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.ConsentDetailsPreferenceFragment.class.getName());
            startActivity(intent);
        }
        if (!hasSetMinimumPreferences()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    private boolean hasConsented() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return (prefs.getBoolean(getString(R.string.consent_read_understand_key), false)
                && prefs.getBoolean(getString(R.string.consent_questions_opportunity_key), false)
                && prefs.getBoolean(getString(R.string.consent_questions_answered_key), false)
                && prefs.getBoolean(getString(R.string.consent_can_withdraw_key), false)
                && prefs.getBoolean(getString(R.string.consent_confidential_key), false)
                && prefs.getBoolean(getString(R.string.consent_data_archiving_key), false)
                && prefs.getBoolean(getString(R.string.consent_risks_key), false)
                && prefs.getBoolean(getString(R.string.consent_take_part_key), false)
                && prefs.getBoolean(getString(R.string.consent_photography_capture_key), false)
                && prefs.getBoolean(getString(R.string.consent_photography_publication_key), false)
                && prefs.getBoolean(getString(R.string.consent_photography_future_studies_key), false)
                && !prefs.getString(getString(R.string.consent_name_key), "").isEmpty()
                && !prefs.getString(getString(R.string.consent_email_key), "").isEmpty()
                && !prefs.getString(getString(R.string.consent_phone_key), "").isEmpty()
                && prefs.getBoolean(getString(R.string.consent_fish_1_key), false)
                && !prefs.getString(getString(R.string.consent_name_key), "").isEmpty()
                && !prefs.getString(getString(R.string.pref_vessel_pln_key), "").isEmpty()
                && !prefs.getString(getString(R.string.pref_vessel_name_key), "").isEmpty()
                && !prefs.getString(getString(R.string.pref_owner_master_name_key), "").isEmpty()
                );
    }

    private boolean hasSetMinimumPreferences() {
        return true;
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
