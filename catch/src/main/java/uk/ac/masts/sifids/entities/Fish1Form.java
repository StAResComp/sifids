package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "fish_1_form")
public class Fish1Form extends ChangeLoggingEntity {

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

    public Fish1Form() {
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

    public String getFisheryOffice() {
        return fisheryOffice;
    }

    public boolean setFisheryOffice(String fisheryOffice) {
        if (!fisheryOffice.equals(this.getFisheryOffice())) {
            this.fisheryOffice = fisheryOffice;
            this.updateDates();
            return true;
        }
        return false;
    }

    public String getEmail() {
        return email;
    }

    public boolean setEmail(String email) {
        if (!email.equals(this.getEmail())) {
            this.email = email;
            this.updateDates();
            return true;
        }
        return false;
    }

    public String getPortOfDeparture() {
        return portOfDeparture;
    }

    public boolean setPortOfDeparture(String portOfDeparture) {
        if (portOfDeparture != null && !portOfDeparture.equals(this.getPortOfDeparture())) {
            this.portOfDeparture = portOfDeparture;
            this.updateDates();
            return true;
        }
        return false;
    }

    public String getPortOfLanding() {
        return portOfLanding;
    }

    public boolean setPortOfLanding(String portOfLanding) {
        if (portOfLanding != null && !portOfLanding.equals(this.getPortOfLanding())) {
            this.portOfLanding = portOfLanding;
            this.updateDates();
            return true;
        }
        return false;
    }

    public String getPln() {
        return pln;
    }

    public boolean setPln(String pln) {
        if (!pln.equals(this.getPln())) {
            this.pln = pln;
            this.updateDates();
            return true;
        }
        return false;
    }

    public String getVesselName() {
        return vesselName;
    }

    public boolean setVesselName(String vesselName) {
        if (!vesselName.equals(this.getVesselName())) {
            this.vesselName = vesselName;
            this.updateDates();
            return true;
        }
        return false;
    }

    public String getOwnerMaster() {
        return ownerMaster;
    }

    public boolean setOwnerMaster(String ownerMaster) {
        if (!ownerMaster.equals(this.getOwnerMaster())) {
            this.ownerMaster = ownerMaster;
            this.updateDates();
            return true;
        }
        return false;
    }

    public String getAddress() {
        return address;
    }

    public boolean setAddress(String address) {
        if (!address.equals(this.getAddress())) {
            this.address = address;
            this.updateDates();
            return true;
        }
        return false;
    }

    public int getTotalPotsFishing() {
        return totalPotsFishing;
    }

    public boolean setTotalPotsFishing(int totalPotsFishing) {
        if (totalPotsFishing != this.getTotalPotsFishing()) {
            this.totalPotsFishing = totalPotsFishing;
            this.updateDates();
            return true;
        }
        return false;
    }

    public String getCommentsAndBuyersInformation() {
        return commentsAndBuyersInformation;
    }

    public boolean setCommentsAndBuyersInformation(String commentsAndBuyersInformation) {
        if (!commentsAndBuyersInformation.equals(this.getCommentsAndBuyersInformation())) {
            this.commentsAndBuyersInformation = commentsAndBuyersInformation;
            this.updateDates();
            return true;
        }
        return false;
    }

    public String toString() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getCreatedAt());
        return this.getPln() + " " + new SimpleDateFormat("dd MMM yyyy").format(cal.getTime());
    }
}
