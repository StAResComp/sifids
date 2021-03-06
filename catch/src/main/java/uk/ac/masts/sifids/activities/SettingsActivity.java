package uk.ac.masts.sifids.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.singletons.RequestQueueSingleton;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if (!(preference instanceof SwitchPreference)) {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        SharedPreferences prefs = preference.getSharedPreferences();
        Object value;
        if (preference instanceof MultiSelectListPreference) {
            value = prefs.getStringSet(preference.getKey(), new HashSet<String>());
        } else if (preference instanceof SwitchPreference) {
            value = prefs.getBoolean(preference.getKey(), false);
        } else {
            value = prefs.getString(preference.getKey(), "");
        }
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || FisheryOfficeDetailsPreferenceFragment.class.getName().equals(fragmentName)
                || VesselDetailsPreferenceFragment.class.getName().equals(fragmentName)
                || OwnerMasterDetailsPreferenceFragment.class.getName().equals(fragmentName)
                || PortDetailsPreferenceFragment.class.getName().equals(fragmentName)
                || GearDetailsPreferenceFragment.class.getName().equals(fragmentName)
                || SpeciesDetailsPreferenceFragment.class.getName().equals(fragmentName)
                || BuyerDetailsPreferenceFragment.class.getName().equals(fragmentName)
                || ConsentDetailsPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment is a base class for the other preference fragments.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class BasePreferenceFragment extends PreferenceFragment {

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class FisheryOfficeDetailsPreferenceFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_fishery_office_details);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_fishery_office_key)));
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class VesselDetailsPreferenceFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_vessel_details);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vessel_pln_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vessel_name_key)));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                    // if this doesn't work as desired, another possibility is to call `finish()` here.
                    getActivity().onBackPressed();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class OwnerMasterDetailsPreferenceFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_owner_master_details);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_owner_master_name_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_owner_master_address_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_owner_master_email_key)));
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PortDetailsPreferenceFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_port_details);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_port_key)));
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GearDetailsPreferenceFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_gear_details);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_gear_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_total_pots_fishing_key)));
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SpeciesDetailsPreferenceFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_species_details);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_species_key)));
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class BuyerDetailsPreferenceFragment extends BasePreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_buyer_details);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_buyer_details_key)));
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ConsentDetailsPreferenceFragment extends BasePreferenceFragment {

        SharedPreferences prefs;
        SharedPreferences.Editor editor;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_consent_details);
            prefs = PreferenceManager.getDefaultSharedPreferences(
                    getActivity().getApplicationContext());
            editor = prefs.edit();

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(getString(R.string.consent_name_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.consent_email_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.consent_phone_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vessel_pln_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_vessel_name_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_owner_master_name_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.consent_accept_all_key)));

            Preference allTheAbove = (Preference) findPreference(getString(R.string.consent_accept_all_key));
            allTheAbove.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean accepted = (Boolean) newValue;
                    SharedPreferences.Editor editor = prefs.edit();
                    String[] consentPrefKeys = {
                            getString(R.string.consent_read_understand_key),
                            getString(R.string.consent_questions_opportunity_key),
                            getString(R.string.consent_questions_answered_key),
                            getString(R.string.consent_can_withdraw_key),
                            getString(R.string.consent_confidential_key),
                            getString(R.string.consent_data_archiving_key),
                            getString(R.string.consent_risks_key),
                            getString(R.string.consent_take_part_key),
                            getString(R.string.consent_photography_capture_key),
                            getString(R.string.consent_photography_publication_key),
                            getString(R.string.consent_photography_future_studies_key),
                            getString(R.string.consent_fish_1_key)
                    };
                    for (String prefKey : consentPrefKeys) {
                        editor.putBoolean(prefKey, accepted);
                        editor.apply();
                    }
                    return true;
                }
            });

            Preference consentName = (Preference) findPreference(getString(R.string.consent_name_key));
            consentName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String name = (String) newValue;
                    if (!name.isEmpty()) {
                        String ownerMasterName =
                                prefs.getString(
                                        getString(R.string.pref_owner_master_name_key), "");
                        if (ownerMasterName.isEmpty()) {
                            EditTextPreference ownerMasterNamePref =
                                    (EditTextPreference) findPreference(
                                            getString(R.string.pref_owner_master_name_key));
                            ownerMasterNamePref.setText(name);
                            ownerMasterNamePref.setSummary(name);

                        }
                    }
                    EditTextPreference namePref = (EditTextPreference) preference;
                    namePref.setSummary(name);
                    return true;
                }
            });
        }
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
            case R.id.activity_map:
                intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                return true;
            case R.id.activity_record_observation:
                intent = new Intent(this, RecordObservationActivity.class);
                startActivity(intent);
                return true;
            case R.id.activity_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void submitConsentDetails(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
                getBaseContext().getApplicationContext());
        final Map<String, ?> prefsMap = prefs.getAll();
        String[] consentPrefKeys = {
                getString(R.string.consent_read_understand_key),
                getString(R.string.consent_questions_opportunity_key),
                getString(R.string.consent_questions_answered_key),
                getString(R.string.consent_can_withdraw_key),
                getString(R.string.consent_confidential_key),
                getString(R.string.consent_data_archiving_key),
                getString(R.string.consent_risks_key),
                getString(R.string.consent_take_part_key),
                getString(R.string.consent_photography_capture_key),
                getString(R.string.consent_photography_publication_key),
                getString(R.string.consent_photography_future_studies_key),
                getString(R.string.consent_name_key),
                getString(R.string.consent_email_key),
                getString(R.string.consent_phone_key),
                getString(R.string.consent_fish_1_key),
                getString(R.string.consent_name_key),
                getString(R.string.pref_vessel_pln_key),
                getString(R.string.pref_vessel_name_key),
                getString(R.string.pref_owner_master_name_key),
        };
        JSONObject consentJson = new JSONObject();
        boolean goodToGo = true;
        for (String prefKey : consentPrefKeys) {
            Object pref = prefsMap.get(prefKey);
            if (pref == null
                    || (pref instanceof Boolean && !((boolean) pref))
                    || (pref instanceof String && ((String) pref).isEmpty())) {
                goodToGo = false;
                Toast.makeText(getBaseContext(), getString(R.string.participant_consent_incomplete),
                        Toast.LENGTH_LONG).show();
                break;
            }
            try {
                consentJson.put(prefKey, pref.toString());
            } catch (JSONException jse) {
                goodToGo = false;
                break;
            }
        }
        if (goodToGo) {
            final String url = getBaseContext().getString(R.string.post_request_url);
            final SharedPreferences.Editor editor = prefs.edit();
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, consentJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            editor.putBoolean(getString(R.string.consent_confirmed_key), true);
                            editor.apply();
                            Toast.makeText(getBaseContext(), getString(R.string.participant_consent_thank_you),
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            SettingsActivity.this.finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            editor.putBoolean(getString(R.string.consent_confirmed_key), false);
                            editor.apply();
                            Toast.makeText(getBaseContext(), getString(R.string.participant_consent_error),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );
            RequestQueueSingleton.getInstance(getBaseContext()).addToRequestQueue(request);

        }
    }

}
