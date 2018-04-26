package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Entity(tableName = "location")
public class CatchLocation {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double latitude;

    public double longitude;

    public Date timestamp;

    public boolean fishing;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    @ColumnInfo(name = "modified_at")
    public Date modifiedAt;

    public final static int LOWER_LAT = 0;
    public final static int UPPER_LAT = 1;
    public final static int LOWER_LONG = 2;
    public final static int UPPER_LONG = 3;

    public CatchLocation() {
        this.updateDates();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.getId() == 0) {
            this.id = id;
            this.updateDates();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public String getLatitudeString() {
        return String.format("%02d",this.getLatitudeDegrees()) + " " + String.format("%02d",this.getLatitudeMinutes()) + " " + this.getLatitudeDirection();
    }

    public char getLatitudeDirection() {
        double lat = this.getLatitude();
        if (lat >= 0.0) return 'N';
        else return 'S';
    }

    public int getLatitudeDegrees() {
        double lat = Math.abs(this.getLatitude());
        return (int) Math.floor(lat);
    }

    public int getLatitudeMinutes() {
        double lat = Math.abs(this.getLatitude());
        int deg = this.getLatitudeDegrees();
        return (int) Math.round((lat - deg) * 60);
    }

    public void setLatitude(double latitude) {
        if (latitude != this.getLatitude()) {
            this.latitude = latitude;
            this.updateDates();
        }
    }

    public void setLatitude(int deg, int min, char dir) {
        double lat = deg + ((double) min/60);
        if (dir == 'N') this.setLatitude(lat);
        else if (dir == 'S') this.setLatitude(lat * -1);
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLongitudeString() {
        return String.format("%02d",this.getLongitudeDegrees()) + " " + String.format("%02d",this.getLongitudeMinutes()) + " " + this.getLongitudeDirection();
    }

    public char getLongitudeDirection() {
        double lat = this.getLongitude();
        if (lat >= 0.0) return 'E';
        else return 'W';
    }

    public int getLongitudeDegrees() {
        double lat = Math.abs(this.getLongitude());
        return (int) Math.floor(lat);
    }

    public int getLongitudeMinutes() {
        double lat = Math.abs(this.getLongitude());
        int deg = this.getLongitudeDegrees();
        return (int) Math.round((lat - deg) * 60);
    }

    public void setLongitude(double longitude) {
        if (longitude != this.getLongitude()) {
            this.longitude = longitude;
            this.updateDates();
        }
    }

    public void setLongitude(int deg, int min, char dir) {
        double lat = deg + ((double) min/60);
        if (dir == 'E') this.setLongitude(lat);
        else if (dir == 'W') this.setLongitude(lat * -1);
    }

    public String getCoordinates() {
        return this.getLatitudeString() + " " + this.getLongitudeString();
    }

    public LatLng getLatLng() {
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        if (timestamp == null || !timestamp.equals(this.getTimestamp())) {
            this.timestamp = timestamp;
            this.updateDates();
        }
    }

    public boolean isFishing() {
        return fishing;
    }

    public void setFishing(boolean fishing) {
        if (fishing != this.isFishing()) {
            this.fishing = fishing;
            this.updateDates();
        }
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    private void updateDates() {
        if (this.getCreatedAt() == null) {
            this.setCreatedAt(new Date());
        }
        this.setModifiedAt(new Date());
    }

    public String getIcesRectangle() {
        return CatchLocation.getIcesRectangle(this.getLatitude(), this.getLongitude());
    }

    public Map<Integer,Double> getIcesRectangleBounds() {
        return CatchLocation.getIcesRectangleBounds(this.getLatitude(), this.getLongitude());
    }

    public static String getIcesRectangle(double lat, double lon) {
        //As per http://www.ices.dk/marine-data/maps/Pages/ICES-statistical-rectangles.aspx
        if (lat >= 36.0 && lat < 85.5 && lon >= -44.0 && lon < 68.5) {
            String icesRect = "";
            int latval = (int) Math.floor((lat - 36.0) * 2) + 1;
            icesRect += String.format("%02d",latval);
            String letterString = "ABCDEFGHJKLM";
            char[] letters = letterString.toCharArray();
            icesRect += letters[((int) Math.floor(lon/10)) + 5];
            if (lon < -40.0) {
                icesRect += (int) Math.floor(Math.abs(-44.0 - lon));
            }
            else if (lon < 0.0) {
                icesRect += (int) Math.floor(9 - (lon % 10));
            }
            else {
                icesRect += (int) Math.floor( lon % 10);
            }
            return icesRect;
        }
        else return null;
    }

    public static Map<Integer,Double> getIcesRectangleBounds(double lat, double lon) {
        //As per http://www.ices.dk/marine-data/maps/Pages/ICES-statistical-rectangles.aspx
        if (lat >= 36.0 && lat < 85.5 && lon >= -44.0 && lon < 68.5) {
            Map<Integer,Double> bounds = new HashMap();
            bounds.put(LOWER_LAT,(Math.floor(lat * 2)/2));
            bounds.put(UPPER_LAT,(Math.ceil(lat * 2)/2));
            bounds.put(LOWER_LONG,Math.floor(lon));
            bounds.put(UPPER_LONG,Math.ceil(lon));
            return bounds;
        }
        else return null;
    }

    public static List<CatchLocation> createTestLocations() {
        List<CatchLocation> locations = new ArrayList();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.add(Calendar.DATE, -7);
        Calendar now = Calendar.getInstance();
        for (Calendar c = cal; cal.before(now); cal.add(Calendar.DATE, 1)) {
            Calendar s = (Calendar) cal.clone();
            s.set(Calendar.HOUR_OF_DAY,9);
            Random rand = new Random();
            double lat = 36.0 + ((85.5 - 36.0) * rand.nextDouble());
            double lon = -44.0 +((68.5 - -44.0) * rand.nextDouble());
            while (s.get(Calendar.HOUR_OF_DAY) < 16 && lat >= 36.0 && lat < 85.5 && lon >= -44.0 && lon < 68.5) {
                CatchLocation location = new CatchLocation();
                location.setLatitude(lat);
                location.setLongitude(lon);
                location.setTimestamp(s.getTime());
                location.setFishing(true);
                locations.add(location);
                lat = (lat - 0.0001) + (((lat + 0.0001) - (lat - 0.0001)) * rand.nextDouble());
                lon = (lon - 0.0001) + (((lon + 0.0001) - (lon - 0.0001)) * rand.nextDouble());
                s.add(Calendar.SECOND,10);
            }
        }
        return locations;
    }
}
