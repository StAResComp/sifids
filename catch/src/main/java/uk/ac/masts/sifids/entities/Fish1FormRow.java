package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.text.format.DateFormat;
/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "fish_1_form_row",
        foreignKeys = {
        @ForeignKey(
                entity = Fish1Form.class,
                parentColumns = "id",
                childColumns = "form_id"
        ),
        @ForeignKey(
                entity = Gear.class,
                parentColumns = "id",
                childColumns = "gear_id"
        ),
        @ForeignKey(
                entity = CatchSpecies.class,
                parentColumns = "id",
                childColumns = "species_id"
        ),
        @ForeignKey(
                entity = CatchState.class,
                parentColumns = "id",
                childColumns = "state_id"
        ),
        @ForeignKey(
                entity = CatchPresentation.class,
                parentColumns = "id",
                childColumns = "presentation_id"
        )
},
        indices = {
                @Index(value = "form_id", name = "form_row_form_id"),
                @Index(value = "gear_id", name = "form_row_gear_id"),
                @Index(value = "species_id", name = "form_row_species_id"),
                @Index(value = "state_id", name = "form_row_state_id"),
                @Index(value = "presentation_id", name = "form_row_presentation_id"),
        }
)
public class Fish1FormRow {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "form_id")
    public int formId;

    @ColumnInfo(name = "fishing_activity_date")
    public Date fishingActivityDate;

    public double latitude;

    public double longitude;

    @ColumnInfo(name = "ices_area")
    public String icesArea;

    @ColumnInfo(name = "gear_id")
    public int gearId;

    @ColumnInfo(name = "mesh_size")
    public int meshSize;

    @ColumnInfo(name = "species_id")
    public int speciesId;

    @ColumnInfo(name = "state_id")
    public int stateId;

    @ColumnInfo(name = "presentation_id")
    public int presentationId;

    public double weight;

    public boolean dis;

    public boolean bms;

    @ColumnInfo(name = "number_of_pots_hauled")
    public int numberOfPotsHauled;

    @ColumnInfo(name = "landing_or_discard_date")
    public Date landingOrDiscardDate;

    @ColumnInfo(name = "transporter_reg_etc")
    public String transporterRegEtc;

    @ColumnInfo(name = "created_at")
    public Date createdAt;

    @ColumnInfo(name = "modified_at")
    public Date modifiedAt;

    public Fish1FormRow () {
        this.updateDates();
    }

    public Fish1FormRow (Fish1Form form, CatchLocation point) {
        this.formId = form.getId();
        this.fishingActivityDate = point.getTimestamp();
        this.latitude = point.getLatitude();
        this.longitude = point.getLongitude();
        this.icesArea = point.getIcesRectangle();
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

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        if (formId != this.getFormId()) {
            this.formId = formId;
            this.updateDates();
        }
    }

    public Date getFishingActivityDate() {
        return fishingActivityDate;
    }

    public void setFishingActivityDate(Date fishingActivityDate) {
        if (fishingActivityDate == null || !fishingActivityDate.equals(this.getFishingActivityDate())) {
            this.fishingActivityDate = fishingActivityDate;
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

    public String getIcesArea() {
        return icesArea;
    }

    public void setIcesArea(String icesArea) {
        if (!icesArea.equals(this.getIcesArea())) {
            this.icesArea = icesArea;
            this.updateDates();
        }
    }

    public int getGearId() {
        return gearId;
    }

    public void setGearId(int gearId) {
        if (gearId != this.getGearId()) {
            this.gearId = gearId;
            this.updateDates();
        }
    }

    public int getMeshSize() {
        return meshSize;
    }

    public void setMeshSize(int meshSize) {
        if (meshSize != this.getMeshSize()) {
            this.meshSize = meshSize;
            this.updateDates();
        }
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(int speciesId) {
        if (speciesId != this.getSpeciesId()) {
            this.speciesId = speciesId;
            this.updateDates();
        }
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        if (stateId != this.getStateId()) {
            this.stateId = stateId;
            this.updateDates();
        }
    }

    public int getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(int presentationId) {
        if (presentationId != this.getPresentationId()) {
            this.presentationId = presentationId;
            this.updateDates();
        }
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (weight != this.getWeight()) {
            this.weight = weight;
            this.updateDates();
        }
    }

    public boolean isDis() {
        return dis;
    }

    public void setDis(boolean dis) {
        if (dis != this.isDis()) {
            this.dis = dis;
            this.updateDates();
        }
    }

    public boolean isBms() {
        return bms;
    }

    public void setBms(boolean bms) {
        if (bms != this.isBms()) {
            this.bms = bms;
            this.updateDates();
        }
    }

    public int getNumberOfPotsHauled() {
        return numberOfPotsHauled;
    }

    public void setNumberOfPotsHauled(int numberOfPotsHauled) {
        if (numberOfPotsHauled != this.getNumberOfPotsHauled()) {
            this.numberOfPotsHauled = numberOfPotsHauled;
            this.updateDates();
        }
    }

    public Date getLandingOrDiscardDate() {
        return landingOrDiscardDate;
    }

    public void setLandingOrDiscardDate(Date landingOrDiscardDate) {
        if (landingOrDiscardDate == null || !landingOrDiscardDate.equals(this.getLandingOrDiscardDate())) {
            this.landingOrDiscardDate = landingOrDiscardDate;
            this.updateDates();
        }
    }

    public String getTransporterRegEtc() {
        return transporterRegEtc;
    }

    public void setTransporterRegEtc(String transporterRegEtc) {
        if (!transporterRegEtc.equals(this.getTransporterRegEtc())) {
            this.transporterRegEtc = transporterRegEtc;
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

    public String toString() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getFishingActivityDate());
        return new SimpleDateFormat("dd MMM yyyy").format(cal.getTime()) + " " + this.getIcesArea();
    }

    public static Collection<Fish1FormRow> createRowsFromTrackForForm(Fish1Form form, Collection<CatchLocation> points) {
        Map<String,Fish1FormRow> rows = new HashMap();
        int counter = 0;
        for (CatchLocation point : points) {
            String dateString = (String) DateFormat.format("yD", point.getTimestamp());
            if (!rows.containsKey(dateString)) {
                counter = 1;
                Fish1FormRow row = new Fish1FormRow(form, point);
                rows.put(dateString + counter,row);
            }
            else if (rows.get(dateString).getIcesArea() != point.getIcesRectangle()) {
                counter++;
                Fish1FormRow row = new Fish1FormRow(form, point);
                rows.put(dateString + counter,row);
            }
        }
        return rows.values();
    }
}
