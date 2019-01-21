package csc_cccix.geocracy.fragments.troop_selection;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.world.Territory;

public class AttackingTroopSelectionFragment extends TroopSelectionFragment {

    public static AttackingTroopSelectionFragment newInstance(Territory attackingTerritory, Territory defendingTerritory) {
        AttackingTroopSelectionFragment newFragment = new AttackingTroopSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable("attackingTerritory", attackingTerritory);
        args.putSerializable("defendingTerritory", defendingTerritory);
        newFragment.setArguments(args);

        return newFragment;
    }

    private static final int[] ATTACK_OPTIONS = new int[]{2,3,4};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        TextView attackingTerritoryTextView = view.findViewById(R.id.originTerritoryID);
        attackingTerritoryTextView.setText("SELECT NUMBER OF UNITS FROM: " + attackingTerritory.getTerritoryName());

        TextView defendingTerritoryTextView = view.findViewById(R.id.targetTerritoryID);
        defendingTerritoryTextView.setText("TO ATTACK TERRITORY: " + defendingTerritory.getTerritoryName());

        radioGroup = view.findViewById(R.id.troopSelection);
        addRadioButtons(ATTACK_OPTIONS);

        return view;
    }

    private void addRadioButtons(int[] values) {
        RadioGroup ll = radioGroup;
        ll.setOrientation(LinearLayout.HORIZONTAL);
        boolean first = true;

        for (int i = 0; i < values.length && i + 1 < this.attackingTerritory.getNArmies() - 1; i++) {
            RadioButton rdbtn = new RadioButton(getContext());
            if (first) {
                rdbtn.setChecked(true);
                first = false;
            }

            rdbtn.setId(i);
            rdbtn.setText("" + values[i]);
            ll.addView(rdbtn);
        }
    }

}
