package csc_cccix.geocracy;

import android.os.Handler;
import android.os.Looper;

import csc_cccix.geocracy.backend.Message;

public final class BackToFront {

    public static void send(Message msg) {
        handler.post(msg);
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

}
