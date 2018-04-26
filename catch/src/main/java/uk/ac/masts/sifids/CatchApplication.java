package uk.ac.masts.sifids;

import android.app.Application;

public class CatchApplication extends Application {

    private boolean fishing = false;

    public boolean isFishing() {
        return fishing;
    }

    public void setFishing(boolean fishing) {
        this.fishing = fishing;
    }
}
