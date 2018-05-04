package csc309.geocracy;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jakewharton.rxbinding2.widget.SeekBarChangeEvent;

public class AudioService extends Service {

    private static String TAG = "AUDIO_SERVICE";

    private MediaPlayer player;
    private boolean isEnabled = true;
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
        player.start();

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
