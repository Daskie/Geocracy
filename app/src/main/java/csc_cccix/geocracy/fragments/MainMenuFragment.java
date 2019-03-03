package csc_cccix.geocracy.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import csc_cccix.R;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.backend.game.Game;
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

        TextView header = view.findViewById(R.id.mainMenuHeader);
        Button startButton;
        Button tutorialButton;
        Button settingsButton;
        Button exitButton;

        RxView.touches(header).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) showGameDevelopers();
        });

        EventBus.subscribe("GAME_NAME_TAP_EVENT", this, e -> showGameDevelopers());

        continueButton = view.findViewById(R.id.continueButton);

        if (Game.isSavedGame()) {
            continueButton.setEnabled(true);
        }

        continueButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Toasty.warning(this.getContext(), "Your game save is being loaded... hang tight!",  Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(this.getContext(), GameActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.putExtra("GAME_LOAD", true);
                this.startActivity(mainIntent);
            }
            return true;
        });

        startButton = view.findViewById(R.id.startButton);

        startButton.setOnTouchListener((v, event) -> {
            ((MenuActivity)getActivity()).navigateToPage(MenuActivity.Pages.GAME_SETUP);
            return false;
        });

        tutorialButton = view.findViewById(R.id.tutorialButton);

        tutorialButton.setOnTouchListener((v, event) -> {
            ((MenuActivity)getActivity()).navigateToPage(MenuActivity.Pages.TUTORIAL);
            return false;
        });

        settingsButton = view.findViewById(R.id.settingsButton);

        settingsButton.setOnTouchListener((v, event) -> {
            ((MenuActivity)getActivity()).navigateToPage(MenuActivity.Pages.SETTINGS);
            return false;
        });

        exitButton = view.findViewById(R.id.exitButton);

        exitButton.setOnTouchListener((v, event) -> {
            Toasty.warning(getContext(), "Need to Exit Application!", Toast.LENGTH_SHORT, true).show();
            return false;
        });

        return view;
    }

    private void showGameDevelopers() {
        Toasty.info(getContext(), "OUR DEV TEAM:\n\nAustin Quick\nAndrew Exton\nGuraik Clair\nSydney Baroya\nSamantha Koski\nRyan\n\nThanks for playing!", Toast.LENGTH_LONG).show();
    }

    public void enableContinueGameButton() {
        continueButton.setEnabled(true);
    }

}


