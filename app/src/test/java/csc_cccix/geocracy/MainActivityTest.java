package csc_cccix.geocracy;

import android.app.Application;
import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import csc_cccix.geocracy.main_menu.MenuActivity;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    @Test
    public void mainActivity_startsWithMenuActivityAndAudioService() {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);

        // Check Menu Activity Starts
        Intent expectedActivityIntent = new Intent(activity, MenuActivity.class);
        Intent actualActivity = shadowOf((Application) getApplicationContext()).getNextStartedActivity();
        assertEquals(expectedActivityIntent.getComponent(), actualActivity.getComponent());

        // Check Audio Service Starts
        Intent expectedServiceIntent = new Intent(activity, AudioService.class);
        Intent actualService = shadowOf((Application) getApplicationContext()).getNextStartedService();
        assertEquals(expectedServiceIntent.getComponent(), actualService.getComponent());

    }
}