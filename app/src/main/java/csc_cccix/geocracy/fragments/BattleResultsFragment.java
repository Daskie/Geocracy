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
import csc_cccix.geocracy.world.Territory;

public class BattleResultsFragment extends Fragment {

    public static BattleResultsFragment newInstance(Territory originTerritory, Territory targetTerritory) {
        BattleResultsFragment newFragment = new BattleResultsFragment();

        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("targetTerritory", targetTerritory);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.battle_results, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");
        Territory targetTerritory = (Territory) getArguments().get("targetTerritory");

        TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
        attackingPlayer.setText("ATTACKER: " + originTerritory.getTerritoryName());

        TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
        defendingPlayer.setText("DEFENDER: " + targetTerritory.getTerritoryName());

        TextView battleResult = view.findViewById(R.id.battleResult);
        battleResult.setText("RESULT: ");

        return view;
    }
}
