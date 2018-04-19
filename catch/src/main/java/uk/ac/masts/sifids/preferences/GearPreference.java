package uk.ac.masts.sifids.preferences;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.Gear;

public class GearPreference extends ListPreference {

    List<Gear> gear;
    List<String> entries;
    List<String> entryValues;
    private static final String TAG = "GearPreference";

    public GearPreference (Context context, AttributeSet attrs) {
        super(context, attrs);

        final CatchDatabase db = CatchDatabase.getInstance(this.getContext());

        entries = new ArrayList();
        entryValues = new ArrayList();

        Runnable r = new Runnable(){
            @Override
            public void run() {
                gear = db.catchDao().getGear();
            }
        };

        Thread newThread= new Thread(r);
        newThread.start();
        try {
            newThread.join();
        }
        catch (InterruptedException ie) {

        }

        for (Gear item : gear) {
            entries.add(item.getName());
            entryValues.add(Integer.toString(item.getId()));
        }

        setEntries(entries.toArray(new String[0]));
        setEntryValues(entryValues.toArray(new String[0]));
    }

    public GearPreference (Context context) {
        this(context, null);
    }
}
