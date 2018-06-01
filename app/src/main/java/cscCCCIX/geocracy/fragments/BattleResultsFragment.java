package cscCCCIX.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cscCCCIX.R;
import cscCCCIX.geocracy.world.Territory;

public class BattleResultsFragment extends Fragment {

    private static final String TAG = "BATTLE_RESULTS_FRAGMENT";


    private Territory targetTerritory;
    private Territory originTerritory;


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

        this.originTerritory = (Territory) getArguments().get("originTerritory");
        this.targetTerritory = (Territory) getArguments().get("targetTerritory");

        TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
        attackingPlayer.setText("Attacker: " + originTerritory.getTerritoryName());

        TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
        defendingPlayer.setText("Defender: " + targetTerritory.getTerritoryName());

        TextView battleResult = view.findViewById(R.id.battleResult);
        battleResult.setText("RESULT: ");

        // get the bottom sheet view
        LinearLayout llBottomSheet = view.findViewById(R.id.bottom_sheet);

        return view;
    }
}
