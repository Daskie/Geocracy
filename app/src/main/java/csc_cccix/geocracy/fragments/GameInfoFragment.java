package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import csc_cccix.R;
import csc_cccix.geocracy.adapters.PlayerAdapter;
import csc_cccix.geocracy.game.Player;

public class GameInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_info, container, false);

        Long worldSeed = (Long) getArguments().getLong("worldSeed");
        TextView worldSeedView = view.findViewById(R.id.worldSeed);
        worldSeedView.setText("World Seed: " + worldSeed.toString());

        Player[] players = (Player[]) getArguments().get("players");

        ListView playerList = view.findViewById(R.id.playerList);
        PlayerAdapter playerArrayAdapter = new PlayerAdapter(
                getContext(),
                new ArrayList<>(Arrays.asList(players))
        );

        playerList.setAdapter(playerArrayAdapter);
        return view;
    }

    public static GameInfoFragment newInstance(Player[] players, Long worldSeed) {
        GameInfoFragment newFragment = new GameInfoFragment();

        Bundle args = new Bundle();
        args.putSerializable("worldSeed", worldSeed);
        args.putSerializable("players", players);
        newFragment.setArguments(args);

        return newFragment;
    }
}
