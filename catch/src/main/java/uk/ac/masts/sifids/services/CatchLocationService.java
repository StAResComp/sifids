package uk.ac.masts.sifids.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import java.util.Date;

import uk.ac.masts.sifids.CatchApplication;
import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.activities.Fish1FormsActivity;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;

/**
 * Service for logging location to database
 *
 * Based largely on https://github.com/codepath/android_guides/issues/220#issuecomment-250756857
 */
public class CatchLocationService extends Service {

	private static final String TAG = "CatchLocationService";
	private LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 10000;
	private static final float LOCATION_DISTANCE = 10f;

	private class LocationListener implements android.location.LocationListener {

	    Location mLastLocation;

        //Attempt to persist location immediately on creation
        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
            this.writeLocation();
        }

        //Update location and persist
        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            this.writeLocation();
        }

        //Attempt to persist location when provider is disabled
        @Override
        public void onProviderDisabled(String provider) {
            this.writeLocation();
        }

        //Attempt to persist location immediately when provider is enabled
        @Override
        public void onProviderEnabled(String provider) {
            this.writeLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Writes the current (technically, last known) location to the database
        private void writeLocation() {
            if (mLastLocation != null) {
                final CatchDatabase db = CatchDatabase.getInstance(getApplicationContext());

                //Database queries need their own thread
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        CatchLocation location = new CatchLocation();
                        location.setLatitude(mLastLocation.getLatitude());
                        location.setLongitude(mLastLocation.getLongitude());
                        location.setTimestamp(new Date());
                        location.setFishing(((CatchApplication) getApplication()).isFishing());
                        db.catchDao().insertLocations(location);
                    }
                };
                Thread newThread = new Thread(r);
                newThread.start();
            }
        }
    }

    private LocationListener mLocationListener;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onCreate() {

		initializeLocationManager();

		/*
		App is intended for use at sea, where mobile signal will be unreliable and location derived
		from network triangulation even less so. Need to always use GPS for location, if available.
		 */

		String provider = LocationManager.GPS_PROVIDER;

        if (!mLocationManager.isProviderEnabled(provider)) {
            provider = LocationManager.NETWORK_PROVIDER;
        }

        mLocationListener = new LocationListener(provider);

		try {
			mLocationManager.requestLocationUpdates(
				provider,
				LOCATION_INTERVAL,
				LOCATION_DISTANCE,
				mLocationListener
			);
		} catch (java.lang.SecurityException ex) {
		} catch (IllegalArgumentException ex) {
		}

		//Need to notify user that app is using location
        Intent notificationIntent = new Intent(this, Fish1FormsActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //Newer versions of Android need a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "sifids_location_tracking_channel";
            String channelName = "SIFIDS Location Tracking";
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification notification =
                    new Notification.Builder(this, channelId)
                            .setContentTitle("Tracking Location")
                            .setContentText("Tracking Location")
                            .setSmallIcon(R.drawable.ic_info_black_24dp)
                            .setContentIntent(pendingIntent)
                            .setTicker("Ticker text")
                            .build();
            startForeground(121, notification);
        }
        else {
            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, null)
                            .setContentTitle("Tracking Location")
                            .setContentText("Tracking Location")
                            .setSmallIcon(R.drawable.ic_info_black_24dp)
                            .setContentIntent(pendingIntent)
                            .setTicker("Ticker text");
            manager.notify(1, builder.build());
        }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLocationManager != null) {
            try {
                if (
                        ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
            }
		}
	}

	private void initializeLocationManager() {
		if (mLocationManager == null) {
			mLocationManager =
                    (LocationManager) getApplicationContext()
                            .getSystemService(Context.LOCATION_SERVICE);
		}
	}
}

