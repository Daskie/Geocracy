package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.world.Territory;
import glm_.vec3.Vec3;

public class BattleResultsFragment extends Fragment {

    public static BattleResultsFragment newInstance(Territory originTerritory, Territory targetTerritory, int attackerArmiesLost, int defenderArmiesLost) {
        BattleResultsFragment newFragment = new BattleResultsFragment();

        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("targetTerritory", targetTerritory);
        args.putSerializable("attackerArmiesLost", attackerArmiesLost);
        args.putSerializable("defenderArmiesLost", defenderArmiesLost);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.battle_results, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");
        Territory targetTerritory = (Territory) getArguments().get("targetTerritory");
        int attackerArmiesLost = (int) getArguments().get("attackerArmiesLost");
        int defenderArmiesLost = (int) getArguments().get("defenderArmiesLost");

        //CountryName: OriginalNumber - TroopsLost = NewAmountTroops

        int attackerNewArmyNum = originTerritory.getNArmies() - attackerArmiesLost;
        int defenderNewArmyNum = targetTerritory.getNArmies() - defenderArmiesLost;

        TextView example = view.findViewById(R.id.example);
        example.setText("(country): (original number) - (troops lost) = (new number)");

        TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
        attackingPlayer.setText("ATTACKER: " + originTerritory.getTerritoryName() + "  :   " + originTerritory.getNArmies() + "   -   " + attackerArmiesLost + "   =   " + attackerNewArmyNum);

        ImageView attackerIcon = view.findViewById(R.id.attackingPlayerIcon);
        attackerIcon.setImageResource(R.drawable.account);

        Vec3 color = originTerritory.getOwner().getColor();
        attackerIcon.setBackgroundColor(Util.colorToInt(color));

        TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
        defendingPlayer.setText("DEFENDER: " + targetTerritory.getTerritoryName() + "  :   " + targetTerritory.getNArmies() + "   -   " + defenderArmiesLost + "   =   " + defenderNewArmyNum);

        ImageView defenderIcon = view.findViewById(R.id.defendingPlayerIcon);
        defenderIcon.setImageResource(R.drawable.account);

        Vec3 color2 = targetTerritory.getOwner().getColor();
        defenderIcon.setBackgroundColor(Util.colorToInt(color2));


        return view;
    }
}
