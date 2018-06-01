package csc309.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import csc309.geocracy.R;
import csc309.geocracy.game.Player;

public class GameInfoFragment extends Fragment {

    private Player[] players = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_info, container, false);

        this.players = (Player[]) getArguments().get("players");
        System.out.println(this.players);
        ListView playerList = view.findViewById(R.id.playerList);
        ArrayAdapter<Player> playerArrayAdapter = new ArrayAdapter<Player>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                this.players
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
