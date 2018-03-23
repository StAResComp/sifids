package uk.ac.masts.sifids.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.util.SparseArray;

import java.util.List;

import uk.ac.masts.sifids.entities.CatchPresentation;
import uk.ac.masts.sifids.entities.CatchSpecies;
import uk.ac.masts.sifids.entities.CatchState;
import uk.ac.masts.sifids.entities.Fish1Form;
import uk.ac.masts.sifids.entities.Fish1FormRow;
import uk.ac.masts.sifids.entities.Gear;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Dao
public interface CatchDao {

    @Insert
    public void insertSpecies(List<CatchSpecies> species);

    @Insert
    public void insertStates(List<CatchState> states);

    @Insert
    public void insertPresentations(List<CatchPresentation> presentations);

    @Insert
    public void insertFish1Forms(Fish1Form... forms);

    @Query("SELECT * FROM catch_species")
    public List<CatchSpecies> getSpecies();

    @Query("SELECT * FROM fish_1_form")
    public List<Fish1Form> getForms();

    @Query("SELECT * FROM fish_1_form WHERE id = :id")
    public Fish1Form getForm(int id);

    @Query("SELECT * FROM fish_1_form_row WHERE form_id = :formId")
    public List<Fish1FormRow> getRowsForForm(int formId);

    @Query("SELECT * FROM fish_1_form_row WHERE id = :id")
    public Fish1FormRow getFormRow(int id);

    @Query("SELECT * FROM gear")
    public List<Gear> getGear();

    @Query("SELECT * FROM catch_state")
    public List<CatchState> getStates();

    @Query("SELECT * FROM catch_presentation")
    public List<CatchPresentation> getPresentations();

    @Update
    public void updateFish1Forms(Fish1Form... forms);

    @Update
    public void updateFish1FormRows(Fish1FormRow... forms);

}
