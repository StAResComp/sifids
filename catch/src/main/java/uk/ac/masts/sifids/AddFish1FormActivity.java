package uk.ac.masts.sifids;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class AddFish1FormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText fisheryOffice;
    EditText fisheryOfficeEmail;
    EditText pln;
    EditText vesselName;
    EditText ownerMaster;
    EditText address;
    EditText totalPotsFishing;
    EditText comment;
    Spinner portOfDeparture;
    Spinner portOfLanding;
    Button button;

    String portOfDepartureValue;
    String portOfLandingValue;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_add_fish_1_form);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        fisheryOffice = (EditText) findViewById(R.id.fishery_office);
        fisheryOfficeEmail = (EditText) findViewById(R.id.fishery_office_email);
        pln = (EditText) findViewById(R.id.pln);
        vesselName = (EditText) findViewById(R.id.vessel_name);
        ownerMaster = (EditText) findViewById(R.id.owner_master);
        address = (EditText) findViewById(R.id.address);
        totalPotsFishing = (EditText) findViewById(R.id.total_pots_fishing);
        comment = (EditText) findViewById(R.id.comment);

        ArrayList<String> ports = new ArrayList();
        for (int i = 1; i <= 6; i++) {
            String port = prefs.getString("pref_port_"+i,"");
            if (port != null && port != "") ports.add(port);
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, ports);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        portOfDeparture = (Spinner) findViewById(R.id.port_of_departure);
        portOfDeparture.setAdapter(adapter);
        portOfDeparture.setOnItemSelectedListener(this);

        portOfLanding = (Spinner) findViewById(R.id.port_of_landing);
        portOfLanding.setAdapter(adapter);
        portOfLanding.setOnItemSelectedListener(this);

        button = (Button) findViewById(R.id.button);

        fisheryOffice.setText(prefs.getString("pref_fishery_office_name", "") + " ("+ prefs.getString("pref_fishery_office_address", "") +")");
        fisheryOfficeEmail.setText(prefs.getString("pref_fishery_office_email", ""));
        pln.setText(prefs.getString("pref_vessel_pln", ""));
        vesselName.setText(prefs.getString("pref_vessel_name", ""));
        ownerMaster.setText(prefs.getString("pref_owner_master_name", ""));
        address.setText(prefs.getString("pref_owner_master_address", ""));

        final CatchDatabase db = CatchDatabase.getInstance(getApplicationContext());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!fisheryOffice.getText().toString().equals("")
                        || !fisheryOfficeEmail.getText().toString().equals("")
                        || !pln.getText().toString().equals("")
                        || !vesselName.getText().toString().equals("")
                        || !ownerMaster.getText().toString().equals("")
                        || !address.getText().toString().equals("")
                        || !totalPotsFishing.getText().toString().equals("")
                        || !comment.getText().toString().equals("")) {

                    final Fish1Form fish1Form= new Fish1Form();
                    fish1Form.setFisheryOffice(fisheryOffice.getText().toString());
                    fish1Form.setEmail(fisheryOfficeEmail.getText().toString());
                    fish1Form.setPln(pln.getText().toString());
                    fish1Form.setVesselName(vesselName.getText().toString());
                    fish1Form.setOwnerMaster(ownerMaster.getText().toString());
                    fish1Form.setAddress(address.getText().toString());
                    fish1Form.setPortOfDeparture(portOfDepartureValue);
                    fish1Form.setPortOfLanding(portOfLandingValue);
                    int totalPotsInt;
                    try {
                        totalPotsInt = Integer.parseInt(totalPotsFishing.getText().toString());
                    }
                    catch (Exception e) {
                        totalPotsInt = 0;
                    }
                    fish1Form.setTotalPotsFishing(totalPotsInt);
                    fish1Form.setCommentsAndBuyersInformation(comment.getText().toString());

                    //save the item before leaving the activity


                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            db.catchDao().insertFish1Form(fish1Form);
                        }
                    });


                    Intent i = new Intent(AddFish1FormActivity.this,Fish1FormsActivity.class);
                    startActivity(i);

                    finish();
                }
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch(parent.getId()) {
            case R.id.port_of_departure:
                this.portOfDepartureValue = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.port_of_landing:
                this.portOfLandingValue = parent.getItemAtPosition(pos).toString();
                break;
        }
        adapter.notifyDataSetChanged();

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
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
