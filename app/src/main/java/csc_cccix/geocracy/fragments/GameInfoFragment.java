package csc_cccix.geocracy.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;

import csc_cccix.R;
import csc_cccix.geocracy.adapters.PlayerAdapter;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.Player;
import es.dmoral.toasty.Toasty;

public class GameInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_info, container, false);

        Long worldSeed = getArguments().getLong("worldSeed");
        TextView worldSeedView = view.findViewById(R.id.worldSeed);
        worldSeedView.setText("World Seed: " + Long.toHexString(worldSeed));
        RxView.touches(worldSeedView).subscribe(seedView -> {
            if (seedView.getAction() == MotionEvent.ACTION_UP) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("GEOCRACY_WORLD_SEED", "" + worldSeed);
                clipboard.setPrimaryClip(clip);
                Toasty.info(getContext(), "Copied world seed").show();
            }
        });

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

    public static GameInfoFragment newInstance(Game game) {
        GameInfoFragment newFragment = new GameInfoFragment();

        Bundle args = new Bundle();
        args.putSerializable("worldSeed", game.getWorld().getSeed());
        args.putSerializable("players", game.getPlayers());
        newFragment.setArguments(args);

        return newFragment;
    }
}
