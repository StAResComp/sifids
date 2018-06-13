package uk.ac.masts.sifids.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import uk.ac.masts.sifids.tasks.PostDataTask;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new PostDataTask(context).execute();
    }
}
