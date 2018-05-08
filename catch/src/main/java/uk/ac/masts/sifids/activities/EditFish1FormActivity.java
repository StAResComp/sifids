package uk.ac.masts.sifids.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.HashSet;
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
import uk.ac.masts.sifids.entities.FisheryOffice;
import uk.ac.masts.sifids.entities.Gear;
import uk.ac.masts.sifids.entities.Port;

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
    Button deleteButton;

    List<String> ports;
    String portOfDepartureValue;
    String portOfLandingValue;
    ArrayAdapter<CharSequence> portOfDepartureAdapter;
    ArrayAdapter<CharSequence> portOfLandingAdapter;

    SharedPreferences prefs;
    CatchDatabase db;

    List<Fish1FormRow> formRows;
    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;

    final static int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 6954;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_edit_fish_1_form);

        db = CatchDatabase.getInstance(getApplicationContext());

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        this.processIntent();

        this.buildForm();

        this.doRows();

        this.doFab();
    }

    private void processIntent() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.get(Fish1Form.ID) != null) {
                final int id = (Integer) extras.get(Fish1Form.ID);

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        fish1Form = db.catchDao().getForm(id);
                    }
                };

                Thread newThread = new Thread(r);
                newThread.start();
                try {
                    newThread.join();
                } catch (InterruptedException ie) {

                }
            } else if (
                    extras.get(Fish1Form.START_DATE) != null
                            && extras.get(Fish1Form.START_DATE) instanceof java.util.Date
                            && extras.get(Fish1Form.END_DATE) != null
                            && extras.get(Fish1Form.END_DATE) instanceof java.util.Date
                    ) {
                fish1Form = new Fish1Form();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        FisheryOffice fisheryOfficeObject =
                                db.catchDao()
                                        .getOffice(
                                                Integer.parseInt(
                                                        prefs.getString(
                                                                getString(R.string.pref_fishery_office_key),
                                                                "1"
                                                        )
                                                )
                                        );
                        if (fisheryOfficeObject != null) {
                            fish1Form.setFisheryOffice(
                                    String.format(
                                            getString(R.string.fish_1_form_fishery_office_string),
                                            fisheryOfficeObject.getName(),
                                            fisheryOfficeObject.getAddress()
                                    )
                            );
                            fish1Form.setEmail(fisheryOfficeObject.getEmail());
                        }
                        fish1Form.setPln(prefs.getString(getString(R.string.pref_vessel_pln_key), ""));
                        fish1Form.setVesselName(prefs.getString(getString(R.string.pref_vessel_name_key), ""));
                        fish1Form.setOwnerMaster(prefs.getString(getString(R.string.pref_owner_master_name_key), ""));
                        fish1Form.setAddress(prefs.getString(getString(R.string.pref_owner_master_address_key), ""));
                        fish1Form.setTotalPotsFishing(Integer.parseInt(prefs.getString(getString(R.string.pref_total_pots_fishing_key), "0")));
                        long[] ids = db.catchDao().insertFish1Forms(fish1Form);
                        fish1Form = db.catchDao().getForm((int) ids[0]);
                        Calendar start = Calendar.getInstance();
                        start.setTime((Date) extras.get(Fish1Form.START_DATE));
                        Calendar end = Calendar.getInstance();
                        end.setTime((Date) extras.get(Fish1Form.END_DATE));
                        List<Fish1FormRow> rows = new ArrayList();
                        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                            Calendar upper = Calendar.getInstance();
                            upper.setTime(date);
                            upper.add(Calendar.DATE, 1);
                            CatchLocation point = db.catchDao().getFirstFishingLocationBetweenDates(date, upper.getTime());
                            if (point != null) {
                                rows.add(new Fish1FormRow(fish1Form, point));
                                while (point != null && point.getTimestamp().before(upper.getTime())) {
                                    Map<Integer, Double> bounds = point.getIcesRectangleBounds();
                                    if (bounds == null)
                                        point = db.catchDao().getFirstValidIcesFishingLocationBetweenDates(point.getTimestamp(), upper.getTime());
                                    else
                                        point = db.catchDao().getFirstFishingLocationOutsideBoundsBetweenDates(point.getTimestamp(), upper.getTime(), bounds.get(CatchLocation.LOWER_LAT), bounds.get(CatchLocation.UPPER_LAT), bounds.get(CatchLocation.LOWER_LONG), bounds.get(CatchLocation.UPPER_LONG));
                                    if (point != null) {
                                        rows.add(new Fish1FormRow(fish1Form, point));
                                    }
                                }
                            }
                        }
                        db.catchDao().insertFish1FormRows(rows);
                    }
                };
                Thread newThread = new Thread(r);
                newThread.start();
                try {
                    newThread.join();
                } catch (InterruptedException ie) {

                }
            }
        }
    }

    private void buildForm() {
        fisheryOffice = (EditText) findViewById(R.id.fishery_office);
        fisheryOfficeEmail = (EditText) findViewById(R.id.fishery_office_email);
        pln = (EditText) findViewById(R.id.pln);
        vesselName = (EditText) findViewById(R.id.vessel_name);
        ownerMaster = (EditText) findViewById(R.id.owner_master);
        address = (EditText) findViewById(R.id.address);
        totalPotsFishing = (EditText) findViewById(R.id.total_pots_fishing);
        comment = (EditText) findViewById(R.id.comment);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                ports = db.catchDao().getPortNames(prefs.getStringSet(getString(R.string.pref_port_key), new HashSet<String>()));
            }
        };
        Thread newThread = new Thread(r);
        newThread.start();
        try {
            newThread.join();
        } catch (InterruptedException ie) {

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
        deleteButton = (Button) findViewById(R.id.delete_form_button);

        this.applyExistingValues();

        this.setListeners();
    }

    private void applyExistingValues() {
        if (fish1Form != null) {
            fisheryOffice.setText(fish1Form.getFisheryOffice());
            fisheryOfficeEmail.setText(fish1Form.getEmail());
            pln.setText(fish1Form.getPln());
            vesselName.setText(fish1Form.getVesselName());
            ownerMaster.setText(fish1Form.getOwnerMaster());
            address.setText(fish1Form.getAddress());
            totalPotsFishing.setText(Integer.toString(fish1Form.getTotalPotsFishing()));
            portOfDeparture.setSelection(portOfDepartureAdapter.getPosition(fish1Form.getPortOfDeparture()));
            portOfLanding.setSelection(portOfLandingAdapter.getPosition(fish1Form.getPortOfDeparture()));
        }
    }

    private void setListeners() {
        addRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveForm();
                Intent i = new Intent(EditFish1FormActivity.this, EditFish1FormRowActivity.class);
                i.putExtra(Fish1FormRow.FORM_ID, fish1Form.getId());
                startActivity(i);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveForm();
                Intent i = new Intent(EditFish1FormActivity.this, Fish1FormsActivity.class);
                startActivity(i);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteForm();
            }
        });


    }

    private void doRows() {
        if (fish1Form != null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    formRows = db.catchDao().getRowsForForm(fish1Form.getId());
                    adapter = new Fish1FormRowAdapter(formRows, EditFish1FormActivity.this);
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
    }

    private void doFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (
                        ContextCompat.checkSelfPermission(
                                EditFish1FormActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            EditFish1FormActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                else {
                    createAndEmailFile();
                }
            }
        });
    }

    private void createAndEmailFile() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(Fish1Form.MAILTO, fish1Form.getEmail(), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.fish_1_form_email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.fish_1_form_email_text));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{fish1Form.getEmail()});
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.format(Fish1Form.ATTACHMENT_URL, createFileToSend().getAbsoluteFile())));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.fish_1_form_email_intent_title)));
    }

    private void saveForm() {

        boolean create = false;

        if (fish1Form == null) {
            create = true;
            fish1Form = new Fish1Form();
        }
        if (
                fish1Form.setFisheryOffice(fisheryOffice.getText().toString())
                        || fish1Form.setEmail(fisheryOfficeEmail.getText().toString())
                        || fish1Form.setPln(pln.getText().toString())
                        || fish1Form.setVesselName(vesselName.getText().toString())
                        || fish1Form.setOwnerMaster(ownerMaster.getText().toString())
                        || fish1Form.setAddress(address.getText().toString())
                        || fish1Form.setTotalPotsFishing(Integer.parseInt(totalPotsFishing.getText().toString()))
                        || fish1Form.setCommentsAndBuyersInformation(comment.getText().toString())
                        || fish1Form.setPortOfDeparture(portOfDepartureValue)
                        || fish1Form.setPortOfLanding(portOfLandingValue)
                ) {
            if (create) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        db.catchDao().insertFish1Forms(fish1Form);
                    }
                });
            } else {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        db.catchDao().updateFish1Forms(fish1Form);
                    }
                });
            }
        }
    }

    private void deleteForm() {

        if (fish1Form != null) {
            this.confirmDialog();
        }
        else {
            Intent i = new Intent(this, Fish1FormsActivity.class);
            this.finish();
            this.startActivity(i);
        }
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(getString(R.string.fish_1_form_deletion_confirmation_message))
                .setPositiveButton(getString(R.string.yes),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                db.catchDao().deleteFish1Form(fish1Form.getId());
                            }
                        };
                        Thread newThread= new Thread(r);
                        newThread.start();
                        try {
                            newThread.join();
                        }
                        catch (InterruptedException ie) {

                        }
                        Intent i = new Intent(EditFish1FormActivity.this, Fish1FormsActivity.class);
                        EditFish1FormActivity.this.finish();
                        EditFish1FormActivity.this.startActivity(i);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
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
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fish1Form.getCsvFileName());
            final FileWriter writer= new FileWriter(file);

            writer.write(String.format(getString(R.string.csv_fishery_office), this.fisheryOffice.getText().toString()) + "\n");
            writer.write(String.format(getString(R.string.csv_email), this.fisheryOfficeEmail.getText().toString()) + "\n");
            writer.write(String.format(getString(R.string.csv_port_of_departure), this.portOfDepartureValue) + "\n");
            writer.write(String.format(getString(R.string.csv_port_of_landing), this.portOfLandingValue) + "\n");
            writer.write(String.format(getString(R.string.csv_pln), pln.getText().toString()) + "\n");
            writer.write(String.format(getString(R.string.csv_vessel_name), vesselName.getText().toString()) + "\n");
            writer.write(String.format(getString(R.string.csv_owner_master), ownerMaster.getText().toString()) + "\n");
            writer.write(String.format(getString(R.string.csv_address), address.getText().toString()) + "\n");
            writer.write(String.format(getString(R.string.csv_total_pots_fishing), totalPotsFishing.getText().toString()) + "\n");
            writer.write(String.format(getString(R.string.csv_comments_buyers_information), comment.getText().toString()));
            writer.write("\n");
            writer.write(getString(R.string.csv_header_row) + "\n");
            for (final Fish1FormRow formRow : formRows) {
                Calendar cal = Calendar.getInstance();
                if (formRow.getFishingActivityDate() != null) {
                    cal.setTime(formRow.getFishingActivityDate());
                    writer.write(new SimpleDateFormat(getString(R.string.ymd)).format(cal.getTime()) + ",");
                }
                else writer.write(",");
                writer.write(formRow.getCoordinates() + ",");
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
                    writer.write(new SimpleDateFormat(getString(R.string.ymd)).format(cal.getTime()) + ",");
                }
                else writer.write(",");
                if (formRow.getTransporterRegEtc() != null) {
                    writer.write("\"" + formRow.getTransporterRegEtc() + "\"\n");
                }
                else writer.write("\n");
            }

            writer.close();
            Toast.makeText(getBaseContext(), String.format(getString(R.string.csv_saved), file.getPath()), Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Toast.makeText(getBaseContext(), getString(R.string.csv_not_saved), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (
                requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createAndEmailFile();
        }
    }

}
