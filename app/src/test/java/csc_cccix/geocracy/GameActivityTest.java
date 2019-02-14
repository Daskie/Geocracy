package csc_cccix.geocracy;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Random;

import csc_cccix.geocracy.game.GameActivity;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(RobolectricTestRunner.class)
public class GameActivityTest {

    @Test
    public void gameActivity_initializesProperlyWithIntentExtras() {
        Intent gameIntent = new Intent(getApplicationContext(), GameActivity.class);

        final int PLAYER_COUNT = 8;

        gameIntent.putExtra("PLAYER_NAME", "Test Player");
        gameIntent.putExtra("NUM_PLAYERS", PLAYER_COUNT);
        gameIntent.putExtra("MAIN_PLAYER_COLOR", 0);
        gameIntent.putExtra("SEED", new Random().nextLong());

        GameActivity activity = Robolectric.buildActivity(GameActivity.class, gameIntent).create().start().get();
        assertNotNull(activity.disposables);

        assertNotNull(activity.game);
        assertNotNull(activity.game.UI);
        assertNotNull(activity.game.Notifications);

        assertEquals(activity.game.getPlayers().length, PLAYER_COUNT);

    }
}