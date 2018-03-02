package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "catch_species_allowed_presentation",
        primaryKeys = {
        "species_id",
        "presentation_id"
},
        foreignKeys = {
        @ForeignKey(
                entity = CatchSpecies.class,
                parentColumns = "id",
                childColumns = "species_id"
        ),
        @ForeignKey(
                entity = CatchPresentation.class,
                parentColumns = "id",
                childColumns = "presentation_id"
        )
},
        indices = {
                @Index(value = "species_id", name = "allowed_presentation_species_id"),
                @Index(value = "presentation_id", name = "allowed_presentation_presentation_id"),
        })
public class CatchSpeciesAllowedPresentation {

    public int species_id;
    public int presentation_id;

    public int getSpecies_id() {
        return species_id;
    }

    public void setSpecies_id(int species_id) {
        this.species_id = species_id;
    }

    public int getPresentation_id() {
        return presentation_id;
    }

    public void setPresentation_id(int presentation_id) {
        this.presentation_id = presentation_id;
    }
}
