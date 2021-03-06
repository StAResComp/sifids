package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "catch_presentation")
public class CatchPresentation extends EntityWithId {

    private static final String[] PRESENTATIONS = {"Whole", "Gutted", "Parts", "Tails"};

    public String name;

    public static List<CatchPresentation> createPresentations() {
        List<CatchPresentation> presentationObjects = new ArrayList();
        for(String name : PRESENTATIONS) presentationObjects.add(new CatchPresentation(name));
        return presentationObjects;
    }

    @Ignore
    public CatchPresentation(String name) {
        this.setName(name);
    }

    public CatchPresentation(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.getName();
    }
}
