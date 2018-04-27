package uk.ac.masts.sifids.preferences;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchSpecies;
import uk.ac.masts.sifids.entities.Gear;

public class SpeciesPreference extends MultiSelectListPreference {

    List<CatchSpecies> species;
    private static final String TAG = "SpeciesPreference";

    public SpeciesPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        List<String> entries;
        List<String> entryValues;

        final CatchDatabase db = CatchDatabase.getInstance(this.getContext());

        entries = new ArrayList();
        entryValues = new ArrayList();

        Runnable r = new Runnable(){
            @Override
            public void run() {
                species = db.catchDao().getSpecies();
            }
        };

        Thread newThread= new Thread(r);
        newThread.start();
        try {
            newThread.join();
        }
        catch (InterruptedException ie) {

        }

        for (CatchSpecies item : species) {
            entries.add(item.toString());
            entryValues.add(Integer.toString(item.getId()));
        }

        setEntries(entries.toArray(new String[0]));
        setEntryValues(entryValues.toArray(new String[0]));
    }

    public SpeciesPreference(Context context) {
        this(context, null);
    }
}
