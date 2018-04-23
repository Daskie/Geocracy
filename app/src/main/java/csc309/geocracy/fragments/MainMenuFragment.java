package csc309.geocracy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import csc309.geocracy.game.GameActivity;
import csc309.geocracy.main_menu.MenuActivity;
import csc309.geocracy.R;
import es.dmoral.toasty.Toasty;

public class MainMenuFragment extends Fragment {

    private static final String TAG = "MAIN_MENU_FRAGMENT";

    private Button continueButton;
    private Button startButton;
    private Button tutorialButton;
    private Button settingsButton;
    private Button exitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu, container, false);

        continueButton = (Button) view.findViewById(R.id.continueButton);

        continueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(getContext(), GameActivity.class));
                return false;
            }
        });

        startButton = (Button) view.findViewById(R.id.startButton);

        startButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(getContext(), GameActivity.class));
                return false;
            }
        });

        tutorialButton = (Button) view.findViewById(R.id.tutorialButton);

        tutorialButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((MenuActivity)getActivity()).setViewPager(1);
                return false;
            }
        });

        settingsButton = (Button) view.findViewById(R.id.settingsButton);

        settingsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((MenuActivity)getActivity()).setViewPager(2);
                return false;
            }
        });

        exitButton = (Button) view.findViewById(R.id.exitButton);

        exitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toasty.warning(getContext(), "Need to Exit Application!", Toast.LENGTH_SHORT, true).show();
                return false;
            }
        });

        return view;
    }

}


