package uk.ac.masts.sifids.entities;

import android.arch.persistence.room.PrimaryKey;

public abstract class EntityWithId {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
