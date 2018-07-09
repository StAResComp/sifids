package uk.ac.masts.sifids.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    FloatingActionButton fab;
    Calendar date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        db = CatchDatabase.getInstance(getApplicationContext());

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        h = new Handler();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog picker = new DatePickerDialog(MapActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                map.clear();
                                date = Calendar.getInstance();
                                date.set(year, month, dayOfMonth, 0, 0);
                                DateFormat df = new SimpleDateFormat("dd MMM");
                                MapActivity.this.setTitle(
                                        String.format(
                                                getString(
                                                        R.string.title_activity_map_custom),
                                                df.format(date.getTime())
                                        )
                                );
                                MapActivity.this.r.run();
                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                );
                picker.show();
            }
        });

    }

    Runnable r = new Runnable(){
        @Override
        public void run(){
            boolean first = true;
            MapActivity.this.getPoints();
            for (CatchLocation point : points) {
                map.addMarker(new MarkerOptions().position(point.getLatLng()).title(point.getCoordinates() + "; " + point.getTimestamp().toString()));
                if ((lastPointId == 0 || MapActivity.this.date != null) && first) {
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

        if (date == null) {
            if (lastPointId == 0) {
                r = new Runnable() {
                    @Override
                    public void run() {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -1);
                        points = db.catchDao().getLocationsSince(cal.getTime());
                    }
                };
            } else {
                r = new Runnable() {
                    @Override
                    public void run() {
                        points = db.catchDao().getLocationsSince(lastPointId);
                    }
                };
            }
        }
        else {
            r = new Runnable() {
                @Override
                public void run() {
                    Calendar c = (Calendar) date.clone();
                    Date startDate = c.getTime();
                    c.add(Calendar.DATE, 1);
                    Date endDate = c.getTime();
                    points = db.catchDao().getLocationsBetween(startDate,endDate);
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
