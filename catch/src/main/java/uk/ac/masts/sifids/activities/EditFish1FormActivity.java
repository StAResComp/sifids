package uk.ac.masts.sifids.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;
import uk.ac.masts.sifids.entities.CatchPresentation;
import uk.ac.masts.sifids.entities.CatchSpecies;
import uk.ac.masts.sifids.entities.CatchState;
import uk.ac.masts.sifids.entities.Fish1Form;
import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.entities.Fish1FormRow;
import uk.ac.masts.sifids.entities.Gear;

/**
 * Created by pgm5 on 21/02/2018.
 */

public class EditFish1FormActivity extends AppCompatActivityWithMenuBar implements AdapterView.OnItemSelectedListener {

    Fish1Form fish1Form;

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
    Button saveButton;
    Button addRowButton;

    String portOfDepartureValue;
    String portOfLandingValue;
    ArrayAdapter<CharSequence> portOfDepartureAdapter;
    ArrayAdapter<CharSequence> portOfLandingAdapter;

    CatchDatabase db;

    List<Fish1FormRow> formRows;
    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_edit_fish_1_form);

        db = CatchDatabase.getInstance(getApplicationContext());

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

        portOfDepartureAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, ports);
        portOfDepartureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        portOfDeparture = (Spinner) findViewById(R.id.port_of_departure);
        portOfDeparture.setAdapter(portOfDepartureAdapter);
        portOfDeparture.setOnItemSelectedListener(this);

        portOfLandingAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, ports);
        portOfLandingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        portOfLanding = (Spinner) findViewById(R.id.port_of_landing);
        portOfLanding.setAdapter(portOfLandingAdapter);
        portOfLanding.setOnItemSelectedListener(this);

        saveButton = (Button) findViewById(R.id.save_form_button);
        addRowButton = (Button) findViewById(R.id.add_row_button);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.get("id") != null) {
                final int id = (Integer) extras.get("id");

                Runnable r = new Runnable(){
                    @Override
                    public void run() {
                        fish1Form = db.catchDao().getForm(id);
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
            else if (extras.get("start_date") != null && extras.get("start_date") instanceof java.util.Date && extras.get("end_date") != null && extras.get("end_date") instanceof java.util.Date) {
                fish1Form = new Fish1Form();
                Runnable r = new Runnable(){
                    @Override
                    public void run() {
                        long[] ids = db.catchDao().insertFish1Forms(fish1Form);
                        fish1Form = db.catchDao().getForm((int) ids[0]);
                        Calendar start = Calendar.getInstance();
                        start.setTime((Date) extras.get("start_date"));
                        Calendar end = Calendar.getInstance();
                        end.setTime((Date) extras.get("end_date"));
                        List<Fish1FormRow> rows = new ArrayList();
                        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                            Calendar upper = Calendar.getInstance();
                            upper.setTime(date);
                            upper.add(Calendar.DATE, 1);
                            CatchLocation point = db.catchDao().getFirstFishingLocationBetweenDates(date, upper.getTime());
                            if (point != null) {
                                rows.add(new Fish1FormRow(fish1Form, point));
                                Log.e("EditFish1FormActivity","created row for: " + point.getLatitude() + "/" + point.getLongitude() + " at " + point.getTimestamp());
                                while (point != null && point.getTimestamp().before(upper.getTime())) {
                                    Map<Integer, Double> bounds = point.getIcesRectangleBounds();
                                    Log.e("EditFish1FormActivity", "Got bounds: " + bounds.get(CatchLocation.LOWER_LAT) + "/" + bounds.get(CatchLocation.UPPER_LAT) + "/" + bounds.get(CatchLocation.LOWER_LONG) + "/" + bounds.get(CatchLocation.UPPER_LONG));
                                    if (bounds == null) point = db.catchDao().getFirstValidIcesFishingLocationBetweenDates(point.getTimestamp(),upper.getTime());
                                    else point = db.catchDao().getFirstFishingLocationOutsideBoundsBetweenDates(point.getTimestamp(), upper.getTime(), bounds.get(CatchLocation.LOWER_LAT), bounds.get(CatchLocation.UPPER_LAT), bounds.get(CatchLocation.LOWER_LONG), bounds.get(CatchLocation.UPPER_LONG));
                                    if (point != null) {
                                        rows.add(new Fish1FormRow(fish1Form, point));
                                        Log.e("EditFish1FormActivity","created row for: " + point.getLatitude() + "/" + point.getLongitude() + " at " + point.getTimestamp());
                                    }
                                }
                            }
                        }
                        db.catchDao().insertFish1FormRows(rows);
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

        if (fish1Form != null && fish1Form.getFisheryOffice() != null && !fish1Form.getFisheryOffice().equals(""))
            fisheryOffice.setText(fish1Form.getFisheryOffice());
        else fisheryOffice.setText(prefs.getString("pref_fishery_office_name", "") + " ("+ prefs.getString("pref_fishery_office_address", "") +")");

        if (fish1Form != null && fish1Form.getEmail() != null && !fish1Form.getEmail().equals(""))
            fisheryOfficeEmail.setText(fish1Form.getEmail());
        else fisheryOfficeEmail.setText(prefs.getString("pref_fishery_office_email", ""));

        if (fish1Form != null && fish1Form.getPln() != null && !fish1Form.getPln().equals(""))
            pln.setText(fish1Form.getPln());
        else pln.setText(prefs.getString("pref_vessel_pln", ""));

        if (fish1Form != null && fish1Form.getVesselName() != null && !fish1Form.getVesselName().equals(""))
            vesselName.setText(fish1Form.getVesselName());
        else vesselName.setText(prefs.getString("pref_vessel_name", ""));

        if (fish1Form != null && fish1Form.getOwnerMaster() != null && !fish1Form.getOwnerMaster().equals(""))
            ownerMaster.setText(fish1Form.getOwnerMaster());
        else ownerMaster.setText(prefs.getString("pref_owner_master_name", ""));

        if (fish1Form != null && fish1Form.getAddress() != null && !fish1Form.getAddress().equals(""))
            address.setText(fish1Form.getAddress());
        else address.setText(prefs.getString("pref_owner_master_address", ""));

        if (fish1Form != null) totalPotsFishing.setText(Integer.toString(fish1Form.getTotalPotsFishing()));
        else totalPotsFishing.setText(prefs.getString("pref_total_pots_fishing", ""));

        if (fish1Form != null && fish1Form.getPortOfDeparture() != null && !fish1Form.getPortOfDeparture().equals("")) {
            int position = portOfDepartureAdapter.getPosition(fish1Form.getPortOfDeparture());
            portOfDeparture.setSelection(position);
        }

        if (fish1Form != null && fish1Form.getPortOfLanding() != null && !fish1Form.getPortOfLanding().equals("")) {
            int position = portOfLandingAdapter.getPosition(fish1Form.getPortOfLanding());
            portOfLanding.setSelection(position);
        }

        addRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveForm();
                Intent i = new Intent(EditFish1FormActivity.this, EditFish1FormRowActivity.class);
                i.putExtra("form_id", fish1Form.getId());
                startActivity(i);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            saveForm();

            Intent i = new Intent(EditFish1FormActivity.this, Fish1FormsActivity.class);
            startActivity(i);

            finish();

            }
        });

        if (fish1Form != null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    formRows = db.catchDao().getRowsForForm(fish1Form.getId());
                    adapter = new Fish1FormRowAdapter(formRows);
                    adapter.notifyDataSetChanged();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView = (RecyclerView) findViewById(R.id.form_row_recycler_view);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            };
            Thread newThread= new Thread(r);
            newThread.start();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "pgm5@st-andrews.ac.uk", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FISH1 Form");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find FISH1 Form attached.");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"pgm5@st-andrews.ac.uk"});
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + createFileToSend().getAbsoluteFile()));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }

    private void saveForm() {

        if (fish1Form == null) {

            if (!fisheryOffice.getText().toString().equals("")
                    || !fisheryOfficeEmail.getText().toString().equals("")
                    || !pln.getText().toString().equals("")
                    || !vesselName.getText().toString().equals("")
                    || !ownerMaster.getText().toString().equals("")
                    || !address.getText().toString().equals("")
                    || !totalPotsFishing.getText().toString().equals("")
                    || !comment.getText().toString().equals("")) {

                fish1Form = new Fish1Form();
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
                } catch (Exception e) {
                    totalPotsInt = 0;
                }
                fish1Form.setTotalPotsFishing(totalPotsInt);
                fish1Form.setCommentsAndBuyersInformation(comment.getText().toString());

                //save the item before leaving the activity

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        db.catchDao().insertFish1Forms(fish1Form);
                    }
                });

            }
        }
        else {

            boolean changes = false;

            if (!fisheryOffice.getText().toString().equals(fish1Form.getFisheryOffice())) {
                fish1Form.setFisheryOffice(fisheryOffice.getText().toString());
                changes = true;
            }

            if (!fisheryOfficeEmail.getText().toString().equals(fish1Form.getEmail())) {
                fish1Form.setEmail(fisheryOfficeEmail.getText().toString());
                changes = true;
            }

            if (!pln.getText().toString().equals(fish1Form.getPln())) {
                fish1Form.setPln(pln.getText().toString());
                changes = true;
            }

            if (!vesselName.getText().toString().equals(fish1Form.getVesselName())) {
                fish1Form.setVesselName(vesselName.getText().toString());
                changes = true;
            }

            if (!ownerMaster.getText().toString().equals(fish1Form.getOwnerMaster())) {
                fish1Form.setOwnerMaster(ownerMaster.getText().toString());
                changes = true;
            }

            if (!address.getText().toString().equals(fish1Form.getAddress())) {
                fish1Form.setAddress(address.getText().toString());
                changes = true;
            }

            int totalPotsInt = Integer.parseInt(totalPotsFishing.getText().toString());
            if (totalPotsInt != fish1Form.getTotalPotsFishing()) {
                fish1Form.setTotalPotsFishing(totalPotsInt);
                changes = true;
            }

            if (!comment.getText().toString().equals(fish1Form.getCommentsAndBuyersInformation())) {
                fish1Form.setCommentsAndBuyersInformation(comment.getText().toString());
                changes = true;
            }

            if (!portOfDepartureValue.equals(fish1Form.getPortOfDeparture())) {
                fish1Form.setPortOfDeparture(portOfDepartureValue);
                changes = true;
            }

            if (!portOfLandingValue.equals(fish1Form.getPortOfLanding())) {
                fish1Form.setPortOfLanding(portOfLandingValue);
                changes = true;
            }

            if (changes) {

                //save the item before leaving the activity

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        db.catchDao().updateFish1Forms(fish1Form);
                    }
                });

            }

        }

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
        portOfDepartureAdapter.notifyDataSetChanged();

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private File createFileToSend() {
        this.saveForm();
        File file = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fish1Form.toString() + ".csv");
            final FileWriter writer= new FileWriter(file);

            writer.write("# Fishery Office: " + this.fisheryOffice.getText().toString() + "\n");
            writer.write("# Email: " + this.fisheryOfficeEmail.getText().toString() + "\n");
            writer.write("# Port of Departure: " + this.portOfDepartureValue + "\n");
            writer.write("# Port of Landing: " + this.portOfLandingValue + "\n");
            writer.write("# PLN: " + pln.getText().toString() + "\n");
            writer.write("# Vessel Name: " + vesselName.getText().toString() + "\n");
            writer.write("# Owner/Master: " + ownerMaster.getText().toString() + "\n");
            writer.write("# Address: " + address.getText().toString() + "\n");
            writer.write("# Total Pots Fishing: " + totalPotsFishing.getText().toString() + "\n");
            writer.write("# Comments and Buyers Information: " + comment.getText().toString());
            writer.write("\n");
            writer.write("Fishing Activity Date,"
                    + "Lat/Long,"
                    + "Stat Rect / ICES Area,"
                    + "Gear,"
                    + "Mesh Size,"
                    + "Species,"
                    + "State,"
                    + "Presentation,"
                    + "Weight,"
                    + "DIS,"
                    + "BMS,"
                    + "Number of Pots Hauled,"
                    + "Landing or Discard Date,"
                    + "\"Transporter Reg, Not Transported or Landed to Keeps\"\n");
            for (final Fish1FormRow formRow : formRows) {
                Calendar cal = Calendar.getInstance();
                if (formRow.getFishingActivityDate() != null) {
                    cal.setTime(formRow.getFishingActivityDate());
                    writer.write(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + ",");
                }
                else writer.write(",");
                writer.write(Double.toString(formRow.getLatitude()) + "/" );
                writer.write(Double.toString(formRow.getLongitude()) + "," );
                if (formRow.getIcesArea() != null)
                    writer.write("\"" + formRow.getIcesArea() + "\",");
                else writer.write(",");
                Runnable r = new Runnable(){
                    @Override
                    public void run() {
                        Gear gear = EditFish1FormActivity.this.db.catchDao().getGearById(formRow.getGearId());
                        CatchSpecies species = EditFish1FormActivity.this.db.catchDao().getSpeciesById(formRow.getSpeciesId());
                        CatchState state = EditFish1FormActivity.this.db.catchDao().getStateById(formRow.getStateId());
                        CatchPresentation presentation = EditFish1FormActivity.this.db.catchDao().getPresentationById(formRow.getPresentationId());
                        try {
                            if (gear != null)
                                writer.write("\"" + gear.getName() + "\",");
                            else writer.write(",");
                            writer.write( formRow.getMeshSize() + ",");
                            if (species != null)
                                writer.write("\"" + species.toString() + "\",");
                            else writer.write(",");
                            if (state != null)
                                writer.write("\"" + state.getName() + "\",");
                            else writer.write(",");
                            if (presentation != null)
                                writer.write("\"" + presentation.getName() + "\",");
                            else writer.write(",");
                        }
                        catch (IOException e) { }
                    }
                };
                Thread newThread= new Thread(r);
                newThread.start();
                try {
                    newThread.join();
                }
                catch (InterruptedException ie) {

                }
                writer.write( Double.toString(formRow.getWeight()) + ",");
                writer.write( Boolean.toString(formRow.isDis()) + ",");
                writer.write( Boolean.toString(formRow.isBms()) + ",");
                writer.write( Integer.toString(formRow.getNumberOfPotsHauled()) + ",");
                if (formRow.getLandingOrDiscardDate() != null) {
                    cal.setTime(formRow.getLandingOrDiscardDate());
                    writer.write(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + ",");
                }
                else writer.write(",");
                if (formRow.getTransporterRegEtc() != null) {
                    writer.write("\"" + formRow.getTransporterRegEtc() + "\"\n");
                }
                else writer.write("\n");
            }

            writer.close();
            Toast.makeText(getBaseContext(), "Temporarily saved contents in " + file.getPath(), Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Toast.makeText(getBaseContext(), "Unable create temp file. Check logcat for stackTrace", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return file;
    }

}
