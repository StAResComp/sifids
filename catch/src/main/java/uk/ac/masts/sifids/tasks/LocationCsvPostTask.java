package uk.ac.masts.sifids.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;
import uk.ac.masts.sifids.utilities.Csv;

/**
 * Posts location data to the server for storage in database.
 * Usage:
 * new LocationCsvPostTask().execute(String url, String pln, String csv);
 * CSV fields: timestamp (string), isFishing (int), latitude (double), longitude (double)
 */
public class LocationCsvPostTask extends AsyncTask<Void, Void, Void> {

    Context context;
    CatchDatabase db;

    public LocationCsvPostTask(Context context) {
        this.context = context;
        this.db = CatchDatabase.getInstance(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        boolean noErrorsEncountered = true;
        while (this.anythingToPost() && noErrorsEncountered) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String pln = prefs.getString(context.getString(R.string.pref_vessel_pln_key), "PLN");
            List<CatchLocation> locations = db.catchDao().getUnuploadedLocations();
            String csv = "";
            for (final CatchLocation loc : locations) {
                String rowToWrite = loc.getTimestamp().toString();
                rowToWrite = Csv.appendToCsvRow(rowToWrite, loc.isFishing() ? 1 : 0, false, context);
                rowToWrite = Csv.appendToCsvRow(rowToWrite, loc.getLatitude(), false, context);
                rowToWrite = Csv.appendToCsvRow(rowToWrite, loc.getLongitude(), false, context);
                csv = Csv.appendRowToCsv(csv, rowToWrite);
            }
            try {
                URL url = new URL(context.getString(R.string.post_request_url));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                urlConnection.setRequestMethod("POST");

                MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
                meBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                meBuilder.addTextBody("vessel_name", pln);
                meBuilder.addPart("tracks", new StringBody(csv, ContentType.TEXT_PLAIN));

                HttpEntity mpe = meBuilder.build();

                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.addRequestProperty("Content-length", mpe.getContentLength() + "");
                urlConnection.addRequestProperty(
                        mpe.getContentType().getName(), mpe.getContentType().getValue());

                OutputStream out = urlConnection.getOutputStream();

                mpe.writeTo(out);
                urlConnection.connect();
                out.flush();
                out.close();

                InputStream input;

                if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    input = urlConnection.getInputStream();
                } else {
                    input = urlConnection.getErrorStream();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                urlConnection.disconnect();

                if (urlConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    List<String> ids = new ArrayList<>();
                    for (CatchLocation loc : locations) {
                        ids.add(Integer.toString(loc.getId()));
                    }
                    db.catchDao().markLocationsUploaded(ids);
                }
                else {
                    noErrorsEncountered = false;
                }

                urlConnection.disconnect();

            } catch (Exception e) {
            }
        }
        return null;
    }

    private boolean anythingToPost() {
        return db.catchDao().countUnuploadedLocations() > 0;
    }
}
