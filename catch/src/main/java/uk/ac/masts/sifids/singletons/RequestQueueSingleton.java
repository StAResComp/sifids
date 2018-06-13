package uk.ac.masts.sifids.singletons;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingleton {

    private static RequestQueueSingleton instance;
    private RequestQueue queue;
    private static Context context;

    private RequestQueueSingleton(Context c) {
        context = c;
        this.queue = getQueue();
    }

    public static synchronized RequestQueueSingleton getInstance(Context c) {
        if (instance == null) {
            instance = new RequestQueueSingleton(c);
        }
        return instance;
    }

    public RequestQueue getQueue() {
        if (this.queue == null) {
            this.queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return queue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        this.getQueue().add(req);
    }

    public void startQueue() {
        this.getQueue().start();
    }

    public void stopQueue() {
        this.getQueue().stop();
    }
}
