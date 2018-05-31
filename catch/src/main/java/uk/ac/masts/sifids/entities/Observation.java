package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = ObservationClass.class,
                parentColumns = "id",
                childColumns = "observation_class_id"
        ),
        @ForeignKey(
                entity = ObservationSpecies.class,
                parentColumns = "id",
                childColumns = "observation_species_id"
        )
})
public class Observation extends ChangeLoggingEntity {

    @ColumnInfo(name = "observation_class_id")
    public int observationClassId;

    @ColumnInfo(name = "observation_species_id")
    public Integer observationSpeciesId;

    public Date timestamp;

    public double latitude;

    public double longitude;

    public int count;

    public String notes;

    public boolean submitted;

    public int getObservationClassId() {
        return observationClassId;
    }

    public void setObservationClassId(int observationClassId) {
        if (observationClassId != this.observationClassId) {
            this.observationClassId = observationClassId;
            this.updateDates();
        }
    }

    public Integer getObservationSpeciesId() {
        return observationSpeciesId;
    }

    public void setObservationSpeciesId(Integer observationSpeciesId) {
        if (
                (this.getObservationSpeciesId() != null
                        && !this.getObservationSpeciesId().equals(observationSpeciesId))
                || (this.getObservationSpeciesId() == null
                        && observationSpeciesId != null)) {
            this.observationSpeciesId = observationSpeciesId;
            this.updateDates();
        }

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        if (!this.getTimestamp().equals(timestamp)) {
            this.timestamp = timestamp;
            this.updateDates();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        if (this.getLatitude() != latitude) {
            this.latitude = latitude;
            this.updateDates();
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        if (this.getLongitude() != longitude) {
            this.longitude = longitude;
            this.updateDates();
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if (this.getCount() != count) {
            this.count = count;
            this.updateDates();
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (
                (this.getNotes() != null && !this.getNotes().equals(notes))
                        || (this.getNotes() == null && notes != null)) {
            this.notes = notes;
            this.updateDates();
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        if (this.isSubmitted() != submitted) {
            this.submitted = submitted;
            this.updateDates();
        }
    }
}
