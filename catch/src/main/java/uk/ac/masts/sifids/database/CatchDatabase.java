package uk.ac.masts.sifids.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

import uk.ac.masts.sifids.entities.CatchLocation;
import uk.ac.masts.sifids.entities.CatchPresentation;
import uk.ac.masts.sifids.entities.CatchSpecies;
import uk.ac.masts.sifids.entities.CatchSpeciesAllowedPresentation;
import uk.ac.masts.sifids.entities.CatchSpeciesAllowedState;
import uk.ac.masts.sifids.entities.CatchState;
import uk.ac.masts.sifids.entities.Fish1Form;
import uk.ac.masts.sifids.entities.Fish1FormRow;
import uk.ac.masts.sifids.entities.Gear;

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
                CatchSpeciesAllowedPresentation.class,
                CatchLocation.class
    },
        version = 5
)
@TypeConverters({DateTypeConverter.class})
public abstract class CatchDatabase extends RoomDatabase{

    private static CatchDatabase INSTANCE;

    public abstract CatchDao catchDao();

    public synchronized static CatchDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static CatchDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context,
                CatchDatabase.class,
                "catch-database")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                getInstance(context).catchDao().insertPresentations(CatchPresentation.createPresentations());
                                getInstance(context).catchDao().insertSpecies(CatchSpecies.createSpecies());
                                getInstance(context).catchDao().insertStates(CatchState.createStates());
                                getInstance(context).catchDao().insertGear(Gear.createGear());
                                //getInstance(context).catchDao().insertLocations(CatchLocation.createTestLocations());
                            }
                        });

                    }
                })
                .fallbackToDestructiveMigration()
                .build();
    }
}
