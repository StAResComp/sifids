package uk.ac.masts.sifids;

import android.app.Application;

/**
 * This class is extends android.app.Application to provide a flag which can be used to indicate
 * whether or not the user is currently fishing and which can be set and inspected from anywhere in
 * the app.
 */
public class CatchApplication extends Application {

    private boolean fishing = false;

    /**
     * Indicates whether or not the user is currently fishing.
     *
     * @return whether or not the user is currently fishing
     */
    public boolean isFishing() {
        return fishing;
    }

    /**
     * Set whether or not the user is currently fishing.
     *
     * @param fishing whether or not the user is currently fishing
     */
    public void setFishing(boolean fishing) {
        this.fishing = fishing;
    }
}
