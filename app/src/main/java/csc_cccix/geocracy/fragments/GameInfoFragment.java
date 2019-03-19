package csc_cccix.geocracy.fragments;

/*import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import androidx.lifecycle.ViewModelProviders;
import csc_cccix.R;
import csc_cccix.geocracy.adapters.PlayerAdapter;
import csc_cccix.geocracy.game.view_models.GameInfoViewModel;

public class GameInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_info, container, false);

        GameInfoViewModel viewModel = ViewModelProviders.of(getActivity()).get(GameInfoViewModel.class);

        // SETUP WORLD SEED
        TextView worldSeedView = view.findViewById(R.id.worldSeed);
        viewModel.getWorldSeed().observe(this, seed -> {
            worldSeedView.setText("World Seed: " + Long.toHexString(seed));
        });

        RxView.touches(worldSeedView).subscribe(seedView -> {
            if (seedView.getAction() == MotionEvent.ACTION_UP) viewModel.copyWorldSeedToClipboard();
        });

        // SETUP PLAYERS LIST
        ListView playerList = view.findViewById(R.id.playerList);

        viewModel.getPlayers().observe(this, players -> {
            PlayerAdapter playerArrayAdapter = new PlayerAdapter(
                    getContext(),
                    players
            );

            playerList.setAdapter(playerArrayAdapter);
        });

        return view;
    }

    public static GameInfoFragment newInstance() {
        return new GameInfoFragment();
    }

}*/
