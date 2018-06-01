package csc309.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSeekBar;

import csc309.geocracy.EventBus;
import csc309.geocracy.R;

public class SettingsFragment extends Fragment {

    boolean isMusicEnabled = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        SeekBar masterVolume;
        SeekBar musicVolume;
        CheckBox musicEnabledCheckbox;

        masterVolume = view.findViewById(R.id.masterVolume);
        musicVolume = view.findViewById(R.id.musicVolume);
        musicVolume.setProgress(100);

        RxSeekBar.changeEvents(musicVolume).subscribe(e -> EventBus.publish("SET_MUSIC_VOLUME_LEVEL_EVENT", e.view().getProgress()));

        musicEnabledCheckbox = view.findViewById(R.id.musicEnabled);
        musicEnabledCheckbox.setChecked(isMusicEnabled);

        RxView.touches(musicEnabledCheckbox).subscribe(e -> {
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
