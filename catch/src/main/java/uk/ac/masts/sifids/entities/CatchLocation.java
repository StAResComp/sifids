package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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

    public void setLatitude(double latitude) {
        if (latitude != this.getLatitude()) {
            this.latitude = latitude;
            this.updateDates();
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (longitude != this.getLongitude()) {
            this.longitude = longitude;
            this.updateDates();
        }
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
}
