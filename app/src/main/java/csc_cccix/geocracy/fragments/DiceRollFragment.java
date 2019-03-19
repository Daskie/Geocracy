package csc_cccix.geocracy.fragments;

/*import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.view_models.DiceRollViewModel;
import glm_.vec3.Vec3;

public class DiceRollFragment extends Fragment {

    public static DiceRollFragment newInstance() {
        return new DiceRollFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dice_roll, container, false);

        DiceRollViewModel viewModel = ViewModelProviders.of(getActivity()).get(DiceRollViewModel.class);

        viewModel.getAttackerDiceRoll().observe(this, aRoll -> {
            Territory attackingTerritory = aRoll.territory;

            TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
            attackingPlayer.setText("ATTACKER " + attackingTerritory.getOwner().getName() + " ROLLS:");

            ImageView attackerIcon = view.findViewById(R.id.attackingPlayerIcon);
            attackerIcon.setImageResource(R.drawable.account);

            Vec3 color = attackingTerritory.getOwner().getColor();
            attackerIcon.setBackgroundColor(Util.colorToInt(color));

            LinearLayout attackerDiceRolls = view.findViewById(R.id.attackingDiceRollFaces);
            attackerDiceRolls.removeAllViews();

            for (Integer faceValue: aRoll.getRolledDiceValues()) {
                if (faceValue > 0) {
                    DiceFaceImageView diceFace = new DiceFaceImageView(getContext(), faceValue);
                    attackerDiceRolls.addView(diceFace);
                }
            }
        });

        viewModel.getDefenderDiceRoll().observe(this, dRoll -> {
            Territory defendingTerritory = dRoll.territory;

            TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
            defendingPlayer.setText("DEFENDER " + defendingTerritory.getOwner().getName() + " ROLLS:");

            ImageView defenderIcon = view.findViewById(R.id.defendingPlayerIcon);
            defenderIcon.setImageResource(R.drawable.account);

            Vec3 color2 = defendingTerritory.getOwner().getColor();
            defenderIcon.setBackgroundColor(Util.colorToInt(color2));

            LinearLayout defenderDiceRolls = view.findViewById(R.id.defendingDiceRollFaces);
            defenderDiceRolls.removeAllViews();

            for (Integer faceValue: dRoll.getRolledDiceValues()) {
                if (faceValue > 0) {
                    DiceFaceImageView diceFace = new DiceFaceImageView(getContext(), faceValue);
                    defenderDiceRolls.addView(diceFace);
                }
            }
        });

        return view;
    }
}*/
