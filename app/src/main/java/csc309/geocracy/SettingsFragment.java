package csc309.geocracy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SETTINGS_FRAGMENT";

    private SeekBar masterVolume;
    private SeekBar musicVolume;
    private CheckBox musicEnabled;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        masterVolume = (SeekBar) view.findViewById(R.id.masterVolume);
        musicVolume = (SeekBar) view.findViewById(R.id.musicVolume);
        musicEnabled = (CheckBox) view.findViewById(R.id.musicEnabled);

        return view;
    }
}
