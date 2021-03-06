package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSeekBar;

import java.io.Serializable;

import csc_cccix.R;
import csc_cccix.geocracy.AudioService;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.game.ui_states.GameAction;
import csc_cccix.geocracy.game.ui_states.GameEvent;

public class SettingsFragment extends Fragment implements Serializable {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        SeekBar musicVolume;
        CheckBox musicEnabledCheckbox;
        Button saveGameButton;

        musicVolume = view.findViewById(R.id.musicVolume);
        musicVolume.setProgress(100);

        RxSeekBar.changeEvents(musicVolume).subscribe(e -> EventBus.publish("SET_MUSIC_VOLUME_LEVEL_EVENT", e.view().getProgress()));

        musicEnabledCheckbox = view.findViewById(R.id.musicEnabled);
        musicEnabledCheckbox.setChecked(AudioService.ENABLED_BY_DEFAULT);

        RxView.touches(musicEnabledCheckbox).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                musicEnabledCheckbox.toggle();
                if (musicEnabledCheckbox.isChecked()) EventBus.publish("SET_MUSIC_ENABLED_EVENT", e);
                else EventBus.publish("SET_MUSIC_DISABLED_EVENT", e);
            }
        });

        saveGameButton = view.findViewById(R.id.saveGameButton);

        RxView.touches(saveGameButton).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                EventBus.publish("SAVE_GAME_EVENT", new GameEvent(GameAction.SAVE_GAME_TAPPED, null));
            }
        });

        return view;
    }
}
