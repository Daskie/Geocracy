package csc309.geocracy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        startButton = (Button) view.findViewById(R.id.startButton);
        tutorialButton = (Button) view.findViewById(R.id.tutorialButton);
        settingsButton = (Button) view.findViewById(R.id.settingsButton);

        settingsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((MenuActivity)getActivity()).setViewPager(1);
                return false;
            }
        });

        exitButton = (Button) view.findViewById(R.id.exitButton);
        return view;
    }

}


