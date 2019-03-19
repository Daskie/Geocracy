package csc_cccix.geocracy.fragments.troop_selection;

/*import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Arrays;

import csc_cccix.R;

public class AttackingTroopSelectionFragment extends TroopSelectionFragment {

    private static final int[] ATTACK_OPTIONS = new int[]{2,3,4};

    public static AttackingTroopSelectionFragment newInstance() {
        return new AttackingTroopSelectionFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        viewModel.getAttackingTerritory().observe(this, aT -> {
            TextView attackingTerritoryTextView = view.findViewById(R.id.originTerritoryID);
            attackingTerritoryTextView.setText("SELECT NUMBER OF UNITS FROM: " + aT.getTerritoryName());
            addRadioButtons(ATTACK_OPTIONS, aT.getNArmies());
        });

        viewModel.getDefendingTerritory().observe(this, dT -> {
            TextView defendingTerritoryTextView = view.findViewById(R.id.targetTerritoryID);
            defendingTerritoryTextView.setText("TO ATTACK TERRITORY: " + dT.getTerritoryName());
        });

        return view;
    }

    private void addRadioButtons(int[] values, int attackerArmies) {
        boolean first = true;

        for (int i = 0; i < values.length && i + 1 < attackerArmies - 1; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            if (first) {
                radioButton.setChecked(true);
                first = false;
            }

            radioButton.setId(i);
            radioButton.setText("" + values[i]);
            radioGroup.addView(radioButton);
        }
    }

    public void selectUnitCount(int unitCount) {
        radioGroup.check(Arrays.binarySearch(ATTACK_OPTIONS, unitCount));
    }

}*/
