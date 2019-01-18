

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

import java.util.List;

import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.ui_states.DiceRoll;
import csc_cccix.geocracy.world.Territory;
import glm_.vec3.Vec3;

public class DiceRollFragment extends Fragment {

    public static DiceRollFragment newInstance(Territory originTerritory, Territory targetTerritory, String attackerString, String defenderString) {
        DiceRollFragment newFragment = new DiceRollFragment();

        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("targetTerritory", targetTerritory);
        args.putSerializable("attackerString", attackerString);
        args.putSerializable("defenderString", defenderString);
        newFragment.setArguments(args);

        return newFragment;
    }

    public static DiceRollFragment newInstance(DiceRoll attackerDiceRoll, DiceRoll defenderDiceRoll) {
        DiceRollFragment newFragment = new DiceRollFragment();

        List<Integer> attackerDiceValues = attackerDiceRoll.getRolledDiceValues();
        List<Integer> defenderDiceValues = defenderDiceRoll.getRolledDiceValues();

        String attackerDiceString = "";
        String defenderDiceString = "";

        // Format attacker roll string
        for (int i = 0; i < attackerDiceValues.size(); i++) {
            int diceValue = attackerDiceValues.get(i);

            if (diceValue > 0) {
                attackerDiceString += diceValue;
                if (i < attackerDiceValues.size()-1) attackerDiceString += ", ";
            }
        }

        // Format defender roll string
        for (int i = 0; i < defenderDiceValues.size(); i++) {
            int diceValue = defenderDiceValues.get(i);

            if (diceValue > 0) {
                defenderDiceString += diceValue;
                if (i < defenderDiceValues.size()-1) defenderDiceString += ", ";
            }
        }

        Bundle args = new Bundle();
        args.putSerializable("originTerritory", attackerDiceRoll.territory);
        args.putSerializable("targetTerritory", defenderDiceRoll.territory);
        args.putSerializable("attackerString", attackerDiceString);
        args.putSerializable("defenderString", defenderDiceString);
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

        TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
        attackingPlayer.setText("ATTACKER ROLLS -> " + attackerString);

        TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
        defendingPlayer.setText("DEFENDER ROLLS -> " + defenderString);

        ImageView attackerIcon = view.findViewById(R.id.attackingPlayerIcon);
        attackerIcon.setImageResource(R.drawable.account);

        Vec3 color = originTerritory.getOwner().getColor();
        attackerIcon.setBackgroundColor(Util.colorToInt(color));

        ImageView defenderIcon = view.findViewById(R.id.defendingPlayerIcon);
        defenderIcon.setImageResource(R.drawable.account);

        Vec3 color2 = targetTerritory.getOwner().getColor();
        defenderIcon.setBackgroundColor(Util.colorToInt(color2));

        return view;
    }
}
