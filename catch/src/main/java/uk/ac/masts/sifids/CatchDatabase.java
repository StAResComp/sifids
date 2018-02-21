package uk.ac.masts.sifids;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;

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
                            }
                        });

                    }
                })
                .build();
    }

}
