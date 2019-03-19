package csc_cccix.geocracy.fragments.troop_selection;

/*import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Arrays;

import csc_cccix.R;

public class DefendingTroopSelectionFragment extends TroopSelectionFragment {

    private static final int[] DEFEND_OPTIONS = new int[]{1,2};

    public static DefendingTroopSelectionFragment newInstance() {
        return new DefendingTroopSelectionFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

//        viewModel.getAttackingTerritory().observe(this, aT -> {
            TextView attackingTerritoryTextView = view.findViewById(R.id.originTerritoryID);
            attackingTerritoryTextView.setText("SELECT NUMBER OF UNITS");
//        });

        viewModel.getDefendingTerritory().observe(this, dT -> {
            TextView defendingTerritoryTextView = view.findViewById(R.id.targetTerritoryID);
            defendingTerritoryTextView.setText("TO DEFEND TERRITORY: " + dT.getTerritoryName());
            addRadioButtons(DEFEND_OPTIONS, dT.getNArmies());
        });

        return view;
    }

    private void addRadioButtons(int[] values, int defenderArmies) {
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        boolean first = true;

        for (int i = 0; i < values.length && i  < defenderArmies; i++) {
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
        if (radioGroup != null) radioGroup.check(Arrays.binarySearch(DEFEND_OPTIONS, unitCount));
    }

}*/
