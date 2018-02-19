package uk.ac.masts.sifids;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Entity
public class CatchState {

    @PrimaryKey
    public int id;

    public String name;

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
