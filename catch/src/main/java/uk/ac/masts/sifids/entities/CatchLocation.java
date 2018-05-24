package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.Entity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Entity(tableName = "location")
public class CatchLocation extends ChangeLoggingEntity {

    public double latitude;

    public double longitude;

    public Date timestamp;

    public boolean fishing;

    public boolean uploaded;

    public final static int LOWER_LAT = 0;
    public final static int UPPER_LAT = 1;
    public final static int LOWER_LONG = 2;
    public final static int UPPER_LONG = 3;

    public CatchLocation() {
        this.uploaded = false;
        this.updateDates();
    }

    public double getLatitude() {
        return latitude;
    }

    public String getLatitudeString() {
        return String.format("%02d",this.getLatitudeDegrees()) + " " + String.format("%02d",this.getLatitudeMinutes()) + " " + this.getLatitudeDirection();
    }

    public static String getLatitudeString(double lat) {
        return String.format("%02d",getLatitudeDegrees(lat)) + " " + String.format("%02d",getLatitudeMinutes(lat)) + " " + getLatitudeDirection(lat);
    }

    public char getLatitudeDirection() {
        double lat = this.getLatitude();
        if (lat >= 0.0) return 'N';
        else return 'S';
    }

    public static char getLatitudeDirection(double lat) {
        if (lat >= 0.0) return 'N';
        else return 'S';
    }

    public int getLatitudeDegrees() {
        double lat = Math.abs(this.getLatitude());
        return (int) Math.floor(lat);
    }

    public static int getLatitudeDegrees(double lat) {
        return (int) Math.floor(Math.abs(lat));
    }

    public int getLatitudeMinutes() {
        double lat = Math.abs(this.getLatitude());
        int deg = this.getLatitudeDegrees();
        return (int) Math.round((lat - deg) * 60);
    }

    public static int getLatitudeMinutes(double lat) {
        int deg = getLatitudeDegrees(lat);
        return (int) Math.round((Math.abs(lat) - deg) * 60);
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

    public static String getLongitudeString(double lon) {
        return String.format("%02d",getLongitudeDegrees(lon)) + " " + String.format("%02d",getLongitudeMinutes(lon)) + " " + getLongitudeDirection(lon);
    }

    public char getLongitudeDirection() {
        double lon = this.getLongitude();
        if (lon >= 0.0) return 'E';
        else return 'W';
    }

    public static char getLongitudeDirection(double lon) {
        if (lon >= 0.0) return 'E';
        else return 'W';
    }

    public int getLongitudeDegrees() {
        double lon = Math.abs(this.getLongitude());
        return (int) Math.floor(lon);
    }

    public static int getLongitudeDegrees(double lon) {
        return (int) Math.floor(Math.abs(lon));
    }

    public int getLongitudeMinutes() {
        double lon = Math.abs(this.getLongitude());
        int deg = this.getLongitudeDegrees();
        return (int) Math.round((lon - deg) * 60);
    }

    public static int getLongitudeMinutes(double lon) {
        int deg = getLongitudeDegrees(lon);
        return (int) Math.round((Math.abs(lon) - deg) * 60);
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

    public static String getCoordinates(double lat, double lon) {
        return getLatitudeString(lat) + " " + getLongitudeString(lon);
    }

    public LatLng getLatLng() {
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

    public static double getDecimalCoordinate(int deg, int min, String dir) {
        double coord = deg + ((double) min/60);
        if (dir.equals("N") || dir.equals("E")) return coord;
        else if (dir.equals("S") || dir.equals("W")) return (coord * -1);
        return 1000;
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

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        if (uploaded != this.isUploaded()) {
            this.uploaded = uploaded;
            this.updateDates();
        }
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
            Map<Integer,Double> bounds = new HashMap<>();
            bounds.put(LOWER_LAT,(Math.floor(lat * 2)/2));
            bounds.put(UPPER_LAT,(Math.ceil(lat * 2)/2));
            bounds.put(LOWER_LONG,Math.floor(lon));
            bounds.put(UPPER_LONG,Math.ceil(lon));
            return bounds;
        }
        else return null;
    }

    public static List<CatchLocation> createTestLocations() {
        List<CatchLocation> locations = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        Calendar today = (Calendar) cal.clone();
        cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        cal.add(Calendar.DATE, -365);
        while (cal.before(today)) {
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
                lat = (lat - 0.001) + (((lat + 0.001) - (lat - 0.001)) * rand.nextDouble());
                lon = (lon - 0.001) + (((lon + 0.001) - (lon - 0.001)) * rand.nextDouble());
                s.add(Calendar.SECOND,10);
            }
            cal.add(Calendar.DATE, 1);
        }
        return locations;
    }
}
