package cscCCCIX.geocracy;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import cscCCCIX.R;

public class AudioService extends Service {

    private static String tag = "AUDIO_SERVICE";

    private MediaPlayer player;
    private boolean isEnabled = false;
    private float playerVolume = 1.0f;

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
        if (isEnabled) {
            player.start();
        }

        EventBus.subscribe("SET_MUSIC_ENABLED_EVENT", this, e -> enableMusic());
        EventBus.subscribe("SET_MUSIC_DISABLED_EVENT", this, e -> disableMusic());
        EventBus.subscribe("SET_MUSIC_VOLUME_LEVEL_EVENT", this, eventProgressVal -> setVolume((int) eventProgressVal));

        return START_STICKY;
    }

    private void setVolume(int volume) {
        playerVolume = (float) volume / 100;
        player.setVolume(playerVolume, playerVolume);
    }

    private void enableMusic() {
        Log.d(tag, "ENABLING MUSIC");
        player.start();
    }

    private void disableMusic() {
        Log.d(tag, "DISABLING MUSIC");
        player.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
