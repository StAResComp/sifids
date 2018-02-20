package uk.ac.masts.sifids;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Dao
public interface CatchDao {

    @Insert
    public void insertSpecies(List<CatchSpecies> species);

    @Query("SELECT * FROM catch_species")
    public List<CatchSpecies> getSpecies();

}
