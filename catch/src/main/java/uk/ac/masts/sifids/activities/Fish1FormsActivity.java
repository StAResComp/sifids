package uk.ac.masts.sifids.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.ac.masts.sifids.CatchApplication;
import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.Fish1Form;
import uk.ac.masts.sifids.services.CatchLocationService;

public class Fish1FormsActivity extends AppCompatActivityWithMenuBar {

    FloatingActionButton fab;
    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    List<Fish1Form> forms;
    CatchDatabase db;
    Calendar selectedWeekStart;

    final static int PERMISSION_REQUEST_FINE_LOCATION = 568;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CatchApplication app = (CatchApplication) getApplication();
        app.checkFirstRun();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fish_1_forms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.pref_fishery_office_details, false);

        db = CatchDatabase.getInstance(getApplicationContext());

        Runnable r = new Runnable(){
            @Override
            public void run() {
                forms = db.catchDao().getForms();
                adapter= new Fish1FormAdapter(forms);
                adapter.notifyDataSetChanged();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView= (RecyclerView)findViewById(R.id.form_recycler_view);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        };

        Thread newThread= new Thread(r);
        newThread.start();

        fab=(FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Fish1FormsActivity.this);
                final Calendar mostRecentSunday = Calendar.getInstance();
                mostRecentSunday.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                mostRecentSunday.set(Calendar.HOUR_OF_DAY,0);
                mostRecentSunday.set(Calendar.MINUTE,0);
                mostRecentSunday.set(Calendar.SECOND,0);
                final Calendar sundayPreviousToMostRecent = (Calendar) mostRecentSunday.clone();
                sundayPreviousToMostRecent.add(Calendar.DATE, -7);
                DateFormat df=new SimpleDateFormat("dd MMM");
                selectedWeekStart = null;
                final CharSequence[] items = {
                        String.format(getString(R.string.this_week), df.format(mostRecentSunday.getTime())),
                        String.format(getString(R.string.last_week), df.format(sundayPreviousToMostRecent.getTime())),
                        getString(R.string.other_week)
                };
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which <= 1) {
                            Intent i = new Intent(Fish1FormsActivity.this,EditFish1FormActivity.class);
                            if (which == 0) selectedWeekStart = mostRecentSunday;
                            else if (which == 1) selectedWeekStart = sundayPreviousToMostRecent;
                            i.putExtra("start_date", selectedWeekStart.getTime());
                            selectedWeekStart.add(Calendar.DATE, 7);
                            i.putExtra("end_date", selectedWeekStart.getTime());
                            //create bundle here...
                            startActivity(i);
                            finish();
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    public void startTrackingLocation(View v) {
        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_FINE_LOCATION);
        }
        else {
            startLocationService();
        }
    }

    public void stopTrackingLocation(View v) {
        stopService(new Intent(this, CatchLocationService.class));
    }

    public void startFishing(View v) {
        ((CatchApplication) this.getApplication()).setFishing(true);
        this.startTrackingLocation(v);
    }

    public void stopFishing(View v) {
        ((CatchApplication) this.getApplication()).setFishing(false);
    }

    private void startLocationService() {
        startService(new Intent(this, CatchLocationService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (
                requestCode == PERMISSION_REQUEST_FINE_LOCATION
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
        }
    }
}
