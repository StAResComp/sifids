package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "catch_species")
public class CatchSpecies extends EntityWithId {

    //Species from FISH1 Form
    private static final String[][] SPECIES = {
            {"Ballan wrasse", "USB", null},
            {"Brill", "BLL", null},
            {"Brown Crab", "CRE", null},
            {"Cockles", "COC", null},
            {"Cod", "COD", null},
            {"Corkwing Wrasse", "YFM", null},
            {"Crawfish", "CRA", null},
            {"Cuckoo wrasse", "USI", null},
            {"Eel Conger", "COE", null},
            {"Goldsinny Wrasse", "TBR", null},
            {"Green Crab", "CRG", null},
            {"Gurnards", "GUR", null},
            {"Haddock", "HAD", null},
            {"Hake", "HKE", null},
            {"Halibut", "HAL", null},
            {"Herring", "HER", null},
            {"King Scallop", "SCE", null},
            {"Lemon Sole", "LEM", null},
            {"Ling", "LIN", null},
            {"Lobster", "LBE", null},
            {"Mackerel", "MAC", null},
            {"Megrim", "MEG", null},
            {"Monkfish", "MON", null},
            {"Nephrops", "NEP", null},
            {"Plaice", "PLA", null},
            {"Pollock", "POL", null},
            {"Queen Scallop", "QSC", null},
            {"Razorfish (Ensis)", "RAZ", null},
            {"Saithe", "POK", null},
            {"Shrimp", "PRX", null},
            {"Skate Common", "RJB", null},
            {"Skate Cuckoo", "RJN", null},
            {"Skate Spotted", "RJN", null},
            {"Skate Starry Ray", "RJR", null},
            {"Skate Thornback", "RJC", null},
            {"Skate White", "RJA", null},
            {"Sprats", "SPR", null},
            {"Squat Lobster", "LBS", null},
            {"Squid", "SQU", null},
            {"Turbot", "TUR", null},
            {"Velvet Crab", "CRS", null},
            {"Whelks", "WHE", null},
            {"Whiting", "WHG", null},
            {"Witches", "WIT", null}
    };

    @ColumnInfo(name = "species_name")
    public String speciesName;

    @ColumnInfo(name = "species_code")
    public String speciesCode;

    @ColumnInfo(name = "scientific_name")
    public String scientificName;

    public static List<CatchSpecies> createSpecies() {
        List<CatchSpecies> speciesObjects = new ArrayList();
        for(String[] speciesDetails : SPECIES) speciesObjects.add(new CatchSpecies(speciesDetails));
        return speciesObjects;
    }

    @Ignore
    public CatchSpecies(String[] speciesDetails) {
        this.setSpeciesName(speciesDetails[0]);
        this.setSpeciesCode(speciesDetails[1]);
        this.setScientificName(speciesDetails[2]);
    }

    public CatchSpecies(){}

    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getSpeciesCode() {
        return speciesCode;
    }

    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String toString() {
        return this.getSpeciesName() + " (" + this.getSpeciesCode() + ")";
    }
}
