package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "gear")
public class Gear extends EntityWithId {

    private static final String[] GEAR = {
            "Beam trawls",
            "Boat dredges",
            "Cast nets",
            "Danish seines",
            "Diving",
            "Drifting longlines",
            "Driftnets",
            "Encircling gillnets",
            "Fyke nets",
            "Gillnets",
            "Hand dredges",
            "Hooks and lines",
            "Lift nets",
            "Longlines",
            "Mechanized dredges",
            "Midwater trawls",
            "Nephrops trawls",
            "Otter trawls",
            "Otter twin trawls",
            "Pair Seines",
            "Portable lift nets",
            "Pots creels",
            "Pumps",
            "Scottish seines",
            "Seines",
            "Set longlines",
            "Stake net",
            "Shrimp trawls",
            "Stake net",
            "Stow nets",
            "Trammel nets",
            "Traps",
            "Trolling lines"
    };

    public String name;

    public boolean hasMeshSize;

    public static List<Gear> createGear() {
        List<Gear> gearObjects = new ArrayList();
        for(String name : GEAR) gearObjects.add(new Gear(name));
        return gearObjects;
    }

    @Ignore
    public Gear(String name) {
        this.setName(name);
    }

    public Gear() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasMeshSize() {
        return hasMeshSize;
    }

    public void setHasMeshSize(boolean hasMeshSize) {
        this.hasMeshSize = hasMeshSize;
    }

    public String toString() {
        return this.getName();
    }
}
