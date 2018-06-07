package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final Map<String, String[]> SPECIES = createSpeciesMap();

    private static Map<String, String[]> createSpeciesMap() {
        Map<String, String[]> speciesMap = new HashMap<>();
        speciesMap.put("Dolphin", new String[] {
                "Bottlenose dolphin",
                "White-beaked dolphin",
                "Risso's dolphin",
                "Common dolphin"
        });
        speciesMap.put("Porpoise", new String[] {"Harbour porpoise"});
        speciesMap.put("Whale", new String[] {"Minke whale", "Orca (killer whale)"});
        speciesMap.put("Seal", new String[] {"Harbour (common) seal", "Grey seal"});
        return speciesMap;
    }

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

    public static List<ObservationSpecies> createObservationSpecies(List<ObservationClass> classes) {
        List<ObservationSpecies> observationSpeciesObjects = new ArrayList();
        for (ObservationClass obsClass : classes) {
            if (SPECIES.containsKey(obsClass.getName())) {
                for (String speciesName: SPECIES.get(obsClass.getName())) {
                    observationSpeciesObjects.add(
                            new ObservationSpecies(speciesName, obsClass.getId()));
                }
            }
        }
        return observationSpeciesObjects;
    }
}
