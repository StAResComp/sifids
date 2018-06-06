package uk.ac.masts.sifids.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;

public class MapActivity extends AppCompatActivityWithMenuBar implements OnMapReadyCallback {

    CatchDatabase db;
    List<CatchLocation> points;
    int lastPointId = 0;
    int delay = 30000;
    Handler h;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        db = CatchDatabase.getInstance(getApplicationContext());

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        h = new Handler();

    }

    Runnable r = new Runnable(){
        @Override
        public void run(){
            boolean first = true;
            MapActivity.this.getPoints();
            for (CatchLocation point : points) {
                map.addMarker(new MarkerOptions().position(point.getLatLng()).title(point.getCoordinates()));
                if (lastPointId == 0 && first) {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(point.getLatLng(), (float) 9.0));
                    first = false;
                }
                if (point.getId() > lastPointId) {
                    lastPointId = point.getId();
                }
            }
            h.postDelayed(r, delay);
        }
    };

    @Override
    public void onMapReady(GoogleMap map) {

        this.map = map;

        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        r.run();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        h.removeCallbacks(r);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(r);
    }

    private void getPoints() {
        points = new ArrayList<>();

        Runnable r = null;

        if (lastPointId == 0) {
            r = new Runnable() {
                @Override
                public void run() {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1 * (cal.get(Calendar.DAY_OF_WEEK) - 1));
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    points = db.catchDao().getLocationsSince(cal.getTime());
                }
            };
        }
        else {
            r = new Runnable() {
                @Override
                public void run() {
                    points = db.catchDao().getLocationsSince(lastPointId);
                }
            };
        }

        Thread newThread= new Thread(r);
        newThread.start();
        try {
            newThread.join();
        }
        catch (InterruptedException ie) {}
    }
}
