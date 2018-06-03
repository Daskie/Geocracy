package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import csc_cccix.R;
import csc_cccix.geocracy.game.Player;

public class GameInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_info, container, false);

        Player[] players = (Player[]) getArguments().get("players");
        ListView playerList = view.findViewById(R.id.playerList);
        ArrayAdapter<Player> playerArrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                players
        );

        playerList.setAdapter(playerArrayAdapter);
        return view;
    }

    public static GameInfoFragment newInstance(Player[] players) {
        GameInfoFragment newFragment = new GameInfoFragment();

        Bundle args = new Bundle();
        args.putSerializable("players", players);
        newFragment.setArguments(args);

        return newFragment;
    }
}
