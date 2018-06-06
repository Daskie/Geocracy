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
import android.widget.Toast;

import csc_cccix.R;
import csc_cccix.geocracy.GameSaves;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.GameActivity;
import csc_cccix.geocracy.main_menu.MenuActivity;
import es.dmoral.toasty.Toasty;

public class MainMenuFragment extends Fragment {

    Button continueButton;
    Game loadedGame;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);

        Button startButton;
        Button tutorialButton;
        Button settingsButton;
        Button exitButton;

        continueButton = view.findViewById(R.id.continueButton);

        GameSaves gameSaves = new GameSaves(getContext());
        Game loadedGame = gameSaves.loadGameFromLocalStorage();
        if (loadedGame != null) {
            Log.i("LOADED", loadedGame.toString());
            continueButton.setEnabled(true);
        }

        continueButton.setOnTouchListener((v, event) -> {
            Toasty.warning(this.getContext(), "Your game save is being loaded... hang tight!",  Toast.LENGTH_LONG).show();

            Intent mainIntent = new Intent(this.getContext(), GameActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mainIntent.putExtra("GAME_LOAD", loadedGame.getGameData());
            this.startActivity(mainIntent);
            return false;
        });

        startButton = view.findViewById(R.id.startButton);

        startButton.setOnTouchListener((v, event) -> {
            ((MenuActivity)getActivity()).navigateToPage(MenuActivity.Pages.GameSetup);
            return false;
        });

        tutorialButton = view.findViewById(R.id.tutorialButton);

        tutorialButton.setOnTouchListener((v, event) -> {
            ((MenuActivity)getActivity()).navigateToPage(MenuActivity.Pages.Tutorial);
            return false;
        });

        settingsButton = view.findViewById(R.id.settingsButton);

        settingsButton.setOnTouchListener((v, event) -> {
            ((MenuActivity)getActivity()).navigateToPage(MenuActivity.Pages.Settings);
            return false;
        });

        exitButton = view.findViewById(R.id.exitButton);

        exitButton.setOnTouchListener((v, event) -> {
            Toasty.warning(getContext(), "Need to Exit Application!", Toast.LENGTH_SHORT, true).show();
            return false;
        });

        return view;
    }

    public void enableContinueGameButton() {
        continueButton.setEnabled(true);
    }

}


