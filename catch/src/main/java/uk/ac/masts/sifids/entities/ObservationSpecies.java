package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "observation_species",
        foreignKeys = {
        @ForeignKey(
                entity = ObservationClass.class,
                parentColumns = "id",
                childColumns = "observation_class_id",
                onDelete = CASCADE
        )
})
public class ObservationSpecies extends EntityWithId {

    public String name;

    @ColumnInfo(name = "observation_class_id")
    public int observationClassId;

    public ObservationSpecies() {}

    @Ignore
    public ObservationSpecies(String name, int observationClassId) {
        this.setName(name);
        this.setObservationClassId(observationClassId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getObservationClassId() {
        return observationClassId;
    }

    public void setObservationClassId(int observationClassId) {
        this.observationClassId = observationClassId;
    }
}
