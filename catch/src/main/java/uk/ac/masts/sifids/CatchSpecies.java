package uk.ac.masts.sifids;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity
public class CatchSpecies {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "species_name")
    public String speciesName;

    @ColumnInfo(name = "species_code")
    public String speciesCode;

    @ColumnInfo(name = "scientific_name")
    public String scientificName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
}
