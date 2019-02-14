package csc_cccix.geocracy;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import csc_cccix.R;

public class AudioService extends Service {

    public static final boolean ENABLED_BY_DEFAULT = false;

    private MediaPlayer player;

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
        if (ENABLED_BY_DEFAULT) player.start();

        EventBus.subscribe("SET_MUSIC_ENABLED_EVENT", this, e -> enableMusic());
        EventBus.subscribe("SET_MUSIC_DISABLED_EVENT", this, e -> disableMusic());
        EventBus.subscribe("SET_MUSIC_VOLUME_LEVEL_EVENT", this, eventProgressVal -> setVolume((int) eventProgressVal));

        return START_STICKY;
    }

    private void setVolume(int volume) {
        float playerVolume = (float) volume / 100;
        player.setVolume(playerVolume, playerVolume);
    }

    private void enableMusic() {
        if (!player.isPlaying()) {
            Log.d("", "ENABLING MUSIC");
            player.start();
        }
    }

    private void disableMusic() {
        if (player.isPlaying()) {
            Log.d("", "DISABLING MUSIC");
            player.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
