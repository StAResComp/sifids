package uk.ac.masts.sifids.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import uk.ac.masts.sifids.CatchApplication;
import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;
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
                adapter= new Fish1FormAdapter(forms, Fish1FormsActivity.this);
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
                            startActivity(i);
                        }
                        else {
                            DatePickerDialog picker = new DatePickerDialog(
                                    Fish1FormsActivity.this,
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                            Calendar chosenDate = Calendar.getInstance();
                                            chosenDate.set(year, month, dayOfMonth, 0, 0);
                                            chosenDate.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                                            Intent i = new Intent(Fish1FormsActivity.this,EditFish1FormActivity.class);
                                            i.putExtra("start_date", chosenDate.getTime());
                                            chosenDate.add(Calendar.DATE, 7);
                                            i.putExtra("end_date", chosenDate.getTime());
                                            startActivity(i);
                                        }
                                    },
                                    sundayPreviousToMostRecent.get(Calendar.YEAR),
                                    sundayPreviousToMostRecent.get(Calendar.MONTH),
                                    sundayPreviousToMostRecent.get(Calendar.DAY_OF_MONTH)
                            );
                            picker.show();
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

    public void submitTrack(View v) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pln = prefs.getString(getString(R.string.pref_vessel_pln_key),"");
        Callable<List<CatchLocation>> c = new Callable<List<CatchLocation>>() {
            @Override
            public List<CatchLocation> call() throws Exception {
                return db.catchDao().getLastLocations(1000);
            }
        };
        ExecutorService service =  Executors.newSingleThreadExecutor();
        Future<List<CatchLocation>> future = service.submit(c);
        List<CatchLocation> locations = null;
        try {
            locations = future.get();
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(),
                    getString(R.string.csv_not_saved), Toast.LENGTH_LONG).show();
        }
        if (locations != null) {
            File file = null;
            try {
                file = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "last_1000_locations");
                FileWriter fw = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fw);
                for (final CatchLocation loc : locations) {
                    String rowToWrite = loc.getTimestamp().toString();
                    rowToWrite = appendToCsvRow(rowToWrite, loc.isFishing() ? 1 : 0, false);
                    rowToWrite = appendToCsvRow(rowToWrite, loc.getLatitude(), false);
                    rowToWrite = appendToCsvRow(rowToWrite, loc.getLongitude(), false);
                    Log.e("CSV", rowToWrite);
                    writer.write(rowToWrite);
                    writer.newLine();
                }
                writer.close();
                fw.close();
                Toast.makeText(getBaseContext(),
                        String.format(getString(R.string.csv_saved), file.getPath()),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), getString(R.string.csv_not_saved), Toast.LENGTH_LONG)
                        .show();
                Log.e("IOException", e.getMessage());
            }
            new SubmitPost().execute();
        }
    }

    /**
     * Appends data to a string intended to be a row of a CSV file by adding a comma, followed by
     * a string representing the data
     * @param rowSoFar the row to which data should be appended
     * @param dataToAppend the data to be appended
     * @param isComplex if this is true, the data will be enclosed in double quotes
     * @return the row with the data appended
     */
    private String appendToCsvRow(String rowSoFar, Object dataToAppend, boolean isComplex) {
        String row = rowSoFar + ",";
        if (dataToAppend != null) {
            if (dataToAppend instanceof Calendar) {
                Calendar cal = (Calendar) dataToAppend;
                row += new SimpleDateFormat(getString(R.string.ymd)).format(cal.getTime());
            }
            else {
                if (isComplex) {
                    row += "\"" + dataToAppend + "\"";
                }
                else {
                    row += dataToAppend;
                }
            }
        }
        return row;
    }

    private class SubmitPost extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = "http://rescomp-dev-1.st-andrews.ac.uk/~sifids/";
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");

                MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
                meBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                meBuilder.addTextBody("vessel_name", "PLN");

                File f = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "last_1000_locations");
                FileBody csvBody = new FileBody(f, ContentType.TEXT_PLAIN);

                meBuilder.addPart("tracks", csvBody);

                HttpEntity mpe = meBuilder.build();

                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.addRequestProperty("Content-length", mpe.getContentLength()+"");
                urlConnection.addRequestProperty(mpe.getContentType().getName(), mpe.getContentType().getValue());

                OutputStream out = urlConnection.getOutputStream();

                mpe.writeTo(out);
                Log.e("HTTPRequest", out.toString());
                urlConnection.connect();
                out.flush();
                out.close();

                Log.e("HTTPResponse", Integer.toString(urlConnection.getResponseCode()));

                InputStream input;

                if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    input = urlConnection.getInputStream();
                }
                else {
                    input = urlConnection.getErrorStream();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                urlConnection.disconnect();

                Log.e("HTTPResponse", result.toString());

            }
            catch (Exception e) {
                Log.e( "FileUpload ", e.getMessage() );
            }
            return "";
        }

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
