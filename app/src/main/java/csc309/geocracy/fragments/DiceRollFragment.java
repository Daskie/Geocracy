

package csc309.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import csc309.geocracy.R;
import csc309.geocracy.world.Territory;

public class DiceRollFragment extends Fragment {

    public static DiceRollFragment newInstance(Territory originTerritory, Territory targetTerritory) {
        DiceRollFragment newFragment = new DiceRollFragment();

        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("targetTerritory", targetTerritory);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dice_roll, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");
        Territory targetTerritory = (Territory) getArguments().get("targetTerritory");

        TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
        attackingPlayer.setText("Attacker ( " + originTerritory.getTerritoryName() + " ) Rolls a: ");

        TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
        defendingPlayer.setText("Defender ( " + targetTerritory.getTerritoryName() + " ) Rolls a: ");

        TextView battleResult = view.findViewById(R.id.battleResult);
        battleResult.setText("RESULT: ");

        return view;
    }
}
