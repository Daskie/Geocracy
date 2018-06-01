package cscCCCIX.geocracy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import cscCCCIX.R;
import cscCCCIX.geocracy.LoadingScreenActivity;
import cscCCCIX.geocracy.main_menu.MenuActivity;
import es.dmoral.toasty.Toasty;

public class MainMenuFragment extends Fragment {

    private Button continueButton;
    private Button startButton;
    private Button tutorialButton;
    private Button settingsButton;
    private Button exitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);

        continueButton = view.findViewById(R.id.continueButton);

        continueButton.setOnTouchListener((v, event) -> {
            startActivity(new Intent(getContext(), LoadingScreenActivity.class));
            return false;
        });

        startButton = view.findViewById(R.id.startButton);

        startButton.setOnTouchListener((v, event) -> {
            startActivity(new Intent(getContext(), LoadingScreenActivity.class));
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

}


