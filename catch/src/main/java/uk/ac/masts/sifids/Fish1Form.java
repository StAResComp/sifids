package uk.ac.masts.sifids;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "fish_1_form")
public class Fish1Form {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "fishery_office")
    public String fisheryOffice;

    public String email;

    @ColumnInfo(name = "port_of_departure")
    public String portOfDeparture;

    @ColumnInfo(name = "port_of_landing")
    public String portOfLanding;

    public String pln;

    @ColumnInfo(name = "vessel_name")
    public String vesselName;

    @ColumnInfo(name = "owner_master")
    public String ownerMaster;

    public String address;

    @ColumnInfo(name = "total_pots_fishing")
    public int totalPotsFishing;

    @ColumnInfo(name = "comments_and_buyers_information")
    public String commentsAndBuyersInformation;

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

    public String getFisheryOffice() {
        return fisheryOffice;
    }

    public void setFisheryOffice(String fisheryOffice) {
        this.fisheryOffice = fisheryOffice;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPortOfDeparture() {
        return portOfDeparture;
    }

    public void setPortOfDeparture(String portOfDeparture) {
        this.portOfDeparture = portOfDeparture;
    }

    public String getPortOfLanding() {
        return portOfLanding;
    }

    public void setPortOfLanding(String portOfLanding) {
        this.portOfLanding = portOfLanding;
    }

    public String getPln() {
        return pln;
    }

    public void setPln(String pln) {
        this.pln = pln;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getOwnerMaster() {
        return ownerMaster;
    }

    public void setOwnerMaster(String ownerMaster) {
        this.ownerMaster = ownerMaster;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotalPotsFishing() {
        return totalPotsFishing;
    }

    public void setTotalPotsFishing(int totalPotsFishing) {
        this.totalPotsFishing = totalPotsFishing;
    }

    public String getCommentsAndBuyersInformation() {
        return commentsAndBuyersInformation;
    }

    public void setCommentsAndBuyersInformation(String commentsAndBuyersInformation) {
        this.commentsAndBuyersInformation = commentsAndBuyersInformation;
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
