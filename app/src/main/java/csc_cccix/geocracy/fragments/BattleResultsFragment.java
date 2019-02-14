package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.ui_states.BattleResult;
import csc_cccix.geocracy.world.Territory;
import glm_.vec3.Vec3;

public class BattleResultsFragment extends Fragment {

    public static BattleResultsFragment newInstance(BattleResult battleResult) {
        BattleResultsFragment newFragment = new BattleResultsFragment();

        Bundle args = new Bundle();
        args.putSerializable("battleResult", battleResult);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.battle_results, container, false);

        BattleResult battleResult = (BattleResult) getArguments().get("battleResult");

        Territory attackingTerritory = battleResult.attackingTerritory;
        Territory defendingTerritory = battleResult.defendingTerritory;
        int attackerArmiesLost = battleResult.attackingUnitLoss;
        int defenderArmiesLost = battleResult.defendingUnitLoss;

        TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
        attackingPlayer.setText("ATTACKER: " + attackingTerritory.getTerritoryName() + " lost " + attackerArmiesLost + " units.");

        ImageView attackerIcon = view.findViewById(R.id.attackingPlayerIcon);
        attackerIcon.setImageResource(R.drawable.account);

        Vec3 color = attackingTerritory.getOwner().getColor();
        attackerIcon.setBackgroundColor(Util.colorToInt(color));

        TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
        defendingPlayer.setText("DEFENDER: " + defendingTerritory.getTerritoryName() + " lost " + defenderArmiesLost + " units.");

        ImageView defenderIcon = view.findViewById(R.id.defendingPlayerIcon);
        defenderIcon.setImageResource(R.drawable.account);

        Vec3 color2 = defendingTerritory.getOwner().getColor();
        defenderIcon.setBackgroundColor(Util.colorToInt(color2));


        return view;
    }
}
