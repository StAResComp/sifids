package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

@Entity(tableName = "location")
public class CatchLocation {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double latitude;

    public double longitude;

    public Date timestamp;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    @ColumnInfo(name = "modified_at")
    public Date modifiedAt;

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

    public static String getIcesRectangle(double lat, double lon) {
        //As per http://www.ices.dk/marine-data/maps/Pages/ICES-statistical-rectangles.aspx
        if (lat >= 36.0 && lat <= 85.5 && lon >= -44.0 && lon <= 68.3) {
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
}
