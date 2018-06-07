

package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;

public class DiceRollFragment extends Fragment {

    public static DiceRollFragment newInstance(Territory originTerritory, Territory targetTerritory, String attackerString, String defenderString, String winnerString) {
        DiceRollFragment newFragment = new DiceRollFragment();

        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("targetTerritory", targetTerritory);
        args.putSerializable("attackerString", attackerString);
        args.putSerializable("defenderString", defenderString);
        args.putSerializable("winnerString", winnerString);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dice_roll, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");
        Territory targetTerritory = (Territory) getArguments().get("targetTerritory");
        String attackerString = (String) getArguments().get("attackerString");
        String defenderString  = (String) getArguments().get("defenderString");
        String winnerString  = (String) getArguments().get("winners");




        TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
        attackingPlayer.setText("ATTACKER: " + originTerritory.getTerritoryName() + " rolls -> " + attackerString);

        TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
        defendingPlayer.setText("DEFENDER: " + targetTerritory.getTerritoryName() + " rolls -> " + defenderString);

        TextView battleResult = view.findViewById(R.id.battleResult);
        battleResult.setText("RESULT: " + winnerString);

        return view;
    }
}
