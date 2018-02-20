package uk.ac.masts.sifids;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "catch_species_allowed_state",
        primaryKeys = {
        "species_id",
        "state_id"
},
        foreignKeys = {
        @ForeignKey(
                entity = CatchSpecies.class,
                parentColumns = "id",
                childColumns = "species_id"
        ),
        @ForeignKey(
                entity = CatchState.class,
                parentColumns = "id",
                childColumns = "state_id"
        )
},
        indices = {
        @Index(value = "species_id", name = "allowed_state_species_id"),
        @Index(value = "state_id", name = "allowed_state_state_id"),
})
public class CatchSpeciesAllowedState {
    
    public int species_id;
    public int state_id;

    public int getSpecies_id() {
        return species_id;
    }

    public void setSpecies_id(int species_id) {
        this.species_id = species_id;
    }

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }
}
