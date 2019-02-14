

package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.ui_states.DiceRoll;
import csc_cccix.geocracy.world.Territory;
import glm_.vec3.Vec3;

public class DiceRollFragment extends Fragment {

    public static DiceRollFragment newInstance(DiceRoll attackerDiceRoll, DiceRoll defenderDiceRoll) {
        DiceRollFragment newFragment = new DiceRollFragment();

        Bundle args = new Bundle();
        
        args.putSerializable("attackerDiceRoll", attackerDiceRoll);
        args.putSerializable("defenderDiceRoll", defenderDiceRoll);

        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dice_roll, container, false);
        
        DiceRoll attackerDiceRoll = (DiceRoll) getArguments().get("attackerDiceRoll");
        DiceRoll defenderDiceRoll = (DiceRoll) getArguments().get("defenderDiceRoll"); 

        Territory attackingTerritory = attackerDiceRoll.territory;
        Territory defendingTerritory = defenderDiceRoll.territory;

        TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
        attackingPlayer.setText("ATTACKER " + attackingTerritory.getOwner().getName() + " ROLLS:");

        ImageView attackerIcon = view.findViewById(R.id.attackingPlayerIcon);
        attackerIcon.setImageResource(R.drawable.account);

        Vec3 color = attackingTerritory.getOwner().getColor();
        attackerIcon.setBackgroundColor(Util.colorToInt(color));

        LinearLayout attackerDiceRolls = view.findViewById(R.id.attackingDiceRollFaces);
        attackerDiceRolls.removeAllViews();

        for (Integer faceValue: attackerDiceRoll.getRolledDiceValues()) {
            if (faceValue > 0) {
                DiceFaceImageView diceFace = new DiceFaceImageView(getContext(), faceValue);
                attackerDiceRolls.addView(diceFace);
            }
        }


        TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
        defendingPlayer.setText("DEFENDER " + defendingTerritory.getOwner().getName() + " ROLLS:");

        ImageView defenderIcon = view.findViewById(R.id.defendingPlayerIcon);
        defenderIcon.setImageResource(R.drawable.account);

        Vec3 color2 = defendingTerritory.getOwner().getColor();
        defenderIcon.setBackgroundColor(Util.colorToInt(color2));

        LinearLayout defenderDiceRolls = view.findViewById(R.id.defendingDiceRollFaces);
        defenderDiceRolls.removeAllViews();

        for (Integer faceValue: defenderDiceRoll.getRolledDiceValues()) {
            if (faceValue > 0) {
                DiceFaceImageView diceFace = new DiceFaceImageView(getContext(), faceValue);
                defenderDiceRolls.addView(diceFace);
            }
        }

        return view;
    }
}
