package csc309.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.jakewharton.rxbinding2.view.RxView;

import csc309.geocracy.EventBus;
import csc309.geocracy.main_menu.MenuActivity;
import csc309.geocracy.R;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SETTINGS_FRAGMENT";

    private Button backButton;

    private SeekBar masterVolume;
    private SeekBar musicVolume;
    private CheckBox musicEnabledCheckbox;

    private boolean isMusicEnabled = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        masterVolume = (SeekBar) view.findViewById(R.id.masterVolume);
        musicVolume = (SeekBar) view.findViewById(R.id.musicVolume);
        musicEnabledCheckbox = (CheckBox) view.findViewById(R.id.musicEnabled);
        musicEnabledCheckbox.setChecked(isMusicEnabled);

        RxView.touches(musicEnabledCheckbox).subscribe(e -> {
            Log.d(TAG, e.toString());
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                isMusicEnabled = !isMusicEnabled;
                musicEnabledCheckbox.setChecked(isMusicEnabled);
                if (isMusicEnabled) EventBus.publish("SET_MUSIC_ENABLED_EVENT", e);
                else EventBus.publish("SET_MUSIC_DISABLED_EVENT", e);
            }
        });

        return view;
    }
}
