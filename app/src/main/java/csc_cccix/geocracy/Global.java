package csc_cccix.geocracy;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

// Simple singleton to allow global access to application context while playing nice with Android
// A way to store global state
public class Global extends Application {

    static Application application;
    public static Application getApplication() { return application; }

    public static Context getContext() { return application.getApplicationContext(); }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        if (LeakCanary.isInAnalyzerProcess(this)) return;
        LeakCanary.install(this);

    }

}
