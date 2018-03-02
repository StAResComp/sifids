package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity(tableName = "catch_state")
public class CatchState {

    private static final String[] STATES = {"Fresh", "Frozen", "Alive"};

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public static List<CatchState> createStates() {
        List<CatchState> stateObjects = new ArrayList();
        for(String name : STATES) stateObjects.add(new CatchState(name));
        return stateObjects;
    }

    @Ignore
    public CatchState(String name) {
        this.setName(name);
    }

    public CatchState() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
