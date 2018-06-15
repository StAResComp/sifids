package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "observation_class")
public class ObservationClass extends EntityWithId {

    public static final String[] ANIMALS = {"Seal", "Whale", "Dolphin", "Porpoise", "John Dory",
            "Basking Shark", "Wrasse", "Triggerfish", "Octopus"};

    public String name;

    public ObservationClass() {}

    @Ignore
    public ObservationClass(String name) {
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<ObservationClass> createObservationClasses() {
        List<ObservationClass> observationClassObjects = new ArrayList();
        for(String name : ANIMALS) observationClassObjects.add(new ObservationClass(name));
        return observationClassObjects;
    }
}
