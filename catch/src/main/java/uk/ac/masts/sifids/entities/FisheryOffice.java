package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "fishery_office")
public class FisheryOffice extends EntityWithId {

    private static final String[][] OFFICES = {
            {"Anstruther", "28 Cunzie Street, ANSTRUTHER KY10 3DF, Tel: 0300 244 9100", "fo.anstruther@gov.scot"},
            {"Ayr", "Russell House, King Street, AYR KA8 0BE, Tel: 01292 291380", "fo.ayr@gov.scot"},
            {"Buckie", "Suites 3 -5, Douglas Centre, March Road, BUCKIE AB56 1ER", "fo.buckie@gov.scot"},
            {"Campbeltown", "40 Hall Street, CAMPBELTOWN PA28 6BU", "fo.campbeltown@gov.scot"},
            {"Eyemouth", "Gunsgreen, Fish Market Buildings, EYEMOUTH TD14 5SD", "fo.eyemouth@gov.scot"},
            {"Fraserburgh", "121 Shore Street, FRASERBURGH AB43 9BR, Tel: 0300 244 9424", "fo.fraserburgh@gov.scot"},
            {"Kinlochbervie", "Bervie Pier, Kinlochbervie, LAIRG IV27 4RR, Tel: 01971 521375", "fo.kinlochbervie.gov.scot"},
            {"Kirkwall", "Terminal Buildings, Kirkwall Passenger Terminal, East Pier, KIRKWALL KW15 1HU", "fo.kirkwall@gov.scot"},
            {"Lerwick", "Alexandra Buildings, Lerwick, SHETLAND ZE1 0LL, Tel: 01595 692007", "fo.lerwick@gov.scot"},
            {"Lochinver", "Culag Pier, Lochinver, LAIRG IV27 4LE, Tel: 01571 844486", "fo.lochinver@gov.scot"},
            {"Mallaig", "Marine Office, Harbour Offices, MALLAIG PH41 4QB, Tel: 01687 462155", "fo.mallaig@gov.scot"},
            {"Oban", "Marine Office, Cameron House, Albany Street, OBAN PA34 4AE, Tel: 0300 244 9400", "fo.oban@gov.scot"},
            {"Peterhead", "Keith House, Seagate, PETERHEAD AB42 1JP, Tel: 0300 244 9200", "fo.peterhead@gov.scot"},
            {"Portree", "Marine Office, Estates Office, Scorrybreac, PORTREE, Isle of Skye, IV51 9DH, Tel: 01478 612038", "fo.portree@gov.scot"},
            {"Scrabster", "St Ola House, SCRABSTER KW14 7UJ, Tel: 01847 895074", "fo.scrabster@gov.scot"},
            {"Stornoway", "Marine Office, Quay Street, STORNOWAY, Isle of Lewis, HS1 2XX, Tel: 01851 703291", "fo.stornoway@gov.scot"},
            {"Ullapool", "West Shore Street, ULLAPOOL IV26 2UR, Tel: 01854 612704", "fo.ullapool@gov.scot"}
    };

    public String name;
    public String address;
    public String email;

    public FisheryOffice() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static List<FisheryOffice> createFisheryOffices() {
        List<FisheryOffice> fisheryOfficeObjects = new ArrayList();
        for(String[] fisheryOfficeDetails : OFFICES) fisheryOfficeObjects.add(new FisheryOffice(fisheryOfficeDetails));
        return fisheryOfficeObjects;
    }

    @Ignore
    public FisheryOffice(String[] fisheryOfficeDetails) {
        this.setName(fisheryOfficeDetails[0]);
        this.setAddress(fisheryOfficeDetails[1]);
        this.setEmail(fisheryOfficeDetails[2]);
    }

    public String toString() {
        return this.getName();
    }
}
