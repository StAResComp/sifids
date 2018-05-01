package uk.ac.masts.sifids.preferences;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import uk.ac.masts.sifids.database.CatchDatabase;
import uk.ac.masts.sifids.entities.CatchSpecies;
import uk.ac.masts.sifids.entities.Port;

public class PortPreference extends MultiSelectListPreference {

    List<Port> ports;
    private static final String TAG = "PortPreference";

    public PortPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        List<String> entries;
        List<String> entryValues;

        final CatchDatabase db = CatchDatabase.getInstance(this.getContext());

        entries = new ArrayList();
        entryValues = new ArrayList();

        Runnable r = new Runnable(){
            @Override
            public void run() {
                ports = db.catchDao().getPorts();
            }
        };

        Thread newThread= new Thread(r);
        newThread.start();
        try {
            newThread.join();
        }
        catch (InterruptedException ie) {

        }

        for (Port item : ports) {
            entries.add(item.getName());
            entryValues.add(Integer.toString(item.getId()));
        }

        setEntries(entries.toArray(new String[0]));
        setEntryValues(entryValues.toArray(new String[0]));
    }

    public PortPreference(Context context) {
        this(context, null);
    }
}
