package uk.ac.masts.sifids.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;
import uk.ac.masts.sifids.entities.Fish1Form;
import uk.ac.masts.sifids.services.CatchLocationService;

public class Fish1FormsActivity extends AppCompatActivity implements OnMapReadyCallback {

    FloatingActionButton fab;
    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    List<Fish1Form> forms;
    CatchDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        fab=(FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Fish1FormsActivity.this,EditFish1FormActivity.class);
                startActivity(i);
                finish();
            }
        });
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

    public void startTrackingLocation(View v) {
        startService(new Intent(this, CatchLocationService.class));
    }

    public void stopTrackingLocation(View v) {
        stopService(new Intent(this, CatchLocationService.class));
    }

    List<CatchLocation> points;

    @Override
    public void onMapReady(GoogleMap map) {

         points = new ArrayList();

        Runnable r = new Runnable(){
            @Override
            public void run() {
                points = db.catchDao().getLastLocations(100);
            }
        };

        Thread newThread= new Thread(r);
        newThread.start();
        try {
            newThread.join();
        }
        catch (InterruptedException ie) {

        }

        boolean first = true;
        for (CatchLocation point : points) {
            Log.e("Map", "Got " + point.getCoordinates());
            map.addMarker(new MarkerOptions().position(point.getLatLng()).title(point.getCoordinates()));
            if (first) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(point.getLatLng(), (float) 10.0));
                first = false;
            }
        }
    }

}
