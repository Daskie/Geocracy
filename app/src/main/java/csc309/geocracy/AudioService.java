package csc309.geocracy;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class AudioService extends Service {

    private static String TAG = "AUDIO_SERVICE";

    private MediaPlayer player;
    private boolean isEnabled = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        player = MediaPlayer.create(getApplicationContext(), R.raw.music);
        player.setLooping(true);
        player.seekTo(0);
        player.setVolume(1f, 1f);
        player.start();

        EventBus.subscribe("SET_MUSIC_ENABLED_EVENT", this, e -> enableMusic());
        EventBus.subscribe("SET_MUSIC_DISABLED_EVENT", this, e -> disableMusic());

        return START_STICKY;
    }

    private void enableMusic() {
        Log.d(TAG, "ENABLING MUSIC");
        player.start();
    }

    private void disableMusic() {
        Log.d(TAG, "DISABLING MUSIC");
        player.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
