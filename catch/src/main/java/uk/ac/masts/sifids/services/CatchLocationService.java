package uk.ac.masts.sifids.services;

//Based on https://github.com/codepath/android_guides/issues/220#issuecomment-250756857

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
import android.util.Log;

import java.util.Date;

import uk.ac.masts.sifids.R;
import uk.ac.masts.sifids.activities.Fish1FormsActivity;
import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchLocation;

public class CatchLocationService extends Service {

	private static final String TAG = "CatchLocationService";
	private LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 10f;

	private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            this.writeLocation();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        private void writeLocation() {
            if (mLastLocation != null) {
                final CatchDatabase db = CatchDatabase.getInstance(getApplicationContext());
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        CatchLocation location = new CatchLocation();
                        location.setLatitude(mLastLocation.getLatitude());
                        location.setLongitude(mLastLocation.getLongitude());
                        location.setTimestamp(new Date());
                        db.catchDao().insertLocations(location);
                    }
                };
                Thread newThread = new Thread(r);
                newThread.start();
            }
        }
    }

	LocationListener[] mLocationListeners = new LocationListener[]{
			new LocationListener(LocationManager.GPS_PROVIDER),
			new LocationListener(LocationManager.NETWORK_PROVIDER)
	};

//	LocationListener[] mLocationListeners = new LocationListener[]{
//			new LocationListener(LocationManager.PASSIVE_PROVIDER)
//	};

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

//		try {
//			mLocationManager.requestLocationUpdates(
//				LocationManager.PASSIVE_PROVIDER,
//				LOCATION_INTERVAL,
//				LOCATION_DISTANCE,
//				mLocationListeners[0]
//			);
//		} catch (java.lang.SecurityException ex) {
//			Log.i(TAG, "fail to request location update, ignore", ex);
//		} catch (IllegalArgumentException ex) {
//			Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//		}

		try {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					LOCATION_INTERVAL,
					LOCATION_DISTANCE,
					mLocationListeners[1]
			);
            Intent notificationIntent = new Intent(this, Fish1FormsActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "sifids_location_tracking_channel";
                String channelName = "SIFIDS Location Tracking";
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null)
                        .setContentTitle("Tracking Location")
                        .setContentText("Tracking Location")
                        .setContentIntent(pendingIntent)
                        .setTicker("Ticker text");
                manager.notify(1, builder.build());
            }


		} catch (java.lang.SecurityException ex) {
		} catch (IllegalArgumentException ex) {
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLocationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						return;
					}
					mLocationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception ex) {
				}
			}
		}
	}

	private void initializeLocationManager() {
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		}
	}
}

