package csc_cccix.geocracy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSeekBar;

import csc_cccix.R;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.LoadingScreenActivity;

public class GameSetupFragment extends Fragment {

    private int playerCount = 4;
    private TextView playerCountView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_setup, container, false);

        playerCountView = view.findViewById(R.id.playerCount);

        SeekBar numberofPlayers = view.findViewById(R.id.numberofPlayers);
        numberofPlayers.setProgress(playerCount);

        RxSeekBar.changeEvents(numberofPlayers).subscribe(e -> playerCountView.setText(Integer.toString(e.view().getProgress() + 4)));

        Button confirmGameSettings = view.findViewById(R.id.confirmGameSettingsBtn);
        RxView.touches(confirmGameSettings).subscribe(e -> startActivity(new Intent(getContext(), LoadingScreenActivity.class)));

        return view;
    }

}
