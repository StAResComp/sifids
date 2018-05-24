package uk.ac.masts.sifids.tasks;

import android.os.AsyncTask;

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

/**
 * Posts location data to the server for storage in database.
 * Use:
 *      new LocationCsvPostTask().execute(String url, String pln, String csv);
 * CSV fields: timestamp (string), isFishing (int), latitude (double), longitude (double)
 */
public class LocationCsvPostTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        String urlString = strings[0];
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");

            MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
            meBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            meBuilder.addTextBody("vessel_name", strings[1]);
            meBuilder.addPart("tracks", new StringBody(strings[2], ContentType.TEXT_PLAIN));

            HttpEntity mpe = meBuilder.build();

            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.addRequestProperty("Content-length", mpe.getContentLength()+"");
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

        }
        catch (Exception e) {}
        return null;
    }
}
