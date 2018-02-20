package uk.ac.masts.sifids;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

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

    public int weight;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public Date getFishingActivityDate() {
        return fishingActivityDate;
    }

    public void setFishingActivityDate(Date fishingActivityDate) {
        this.fishingActivityDate = fishingActivityDate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIcesArea() {
        return icesArea;
    }

    public void setIcesArea(String icesArea) {
        this.icesArea = icesArea;
    }

    public int getGearId() {
        return gearId;
    }

    public void setGearId(int gearId) {
        this.gearId = gearId;
    }

    public int getMeshSize() {
        return meshSize;
    }

    public void setMeshSize(int meshSize) {
        this.meshSize = meshSize;
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public int getPresentationId() {
        return presentationId;
    }

    public void setPresentationId(int presentationId) {
        this.presentationId = presentationId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isDis() {
        return dis;
    }

    public void setDis(boolean dis) {
        this.dis = dis;
    }

    public boolean isBms() {
        return bms;
    }

    public void setBms(boolean bms) {
        this.bms = bms;
    }

    public int getNumberOfPotsHauled() {
        return numberOfPotsHauled;
    }

    public void setNumberOfPotsHauled(int numberOfPotsHauled) {
        this.numberOfPotsHauled = numberOfPotsHauled;
    }

    public Date getLandingOrDiscardDate() {
        return landingOrDiscardDate;
    }

    public void setLandingOrDiscardDate(Date landingOrDiscardDate) {
        this.landingOrDiscardDate = landingOrDiscardDate;
    }

    public String getTransporterRegEtc() {
        return transporterRegEtc;
    }

    public void setTransporterRegEtc(String transporterRegEtc) {
        this.transporterRegEtc = transporterRegEtc;
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
}
