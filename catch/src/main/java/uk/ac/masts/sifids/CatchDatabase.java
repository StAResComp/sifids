package uk.ac.masts.sifids;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

/**
 * Created by pgm5 on 19/02/2018.
 */

@Database(
        entities = {
                Fish1Form.class,
                Fish1FormRow.class,
                CatchSpecies.class,
                CatchState.class,
                CatchPresentation.class,
                Gear.class,
                CatchSpeciesAllowedState.class,
                CatchSpeciesAllowedPresentation.class
    },
        version = 1
)
@TypeConverters({DateTypeConverter.class})
public abstract class CatchDatabase extends RoomDatabase{
    public abstract CatchDao catchDao();
}
