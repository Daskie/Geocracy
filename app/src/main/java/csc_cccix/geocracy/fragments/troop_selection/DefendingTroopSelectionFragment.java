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

public class DefendingTroopSelectionFragment extends TroopSelectionFragment {

    public static DefendingTroopSelectionFragment newInstance(Territory attackingTerritory, Territory defendingTerritory) {
        DefendingTroopSelectionFragment newFragment = new DefendingTroopSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable("attackingTerritory", attackingTerritory);
        args.putSerializable("defendingTerritory", defendingTerritory);
        newFragment.setArguments(args);

        return newFragment;
    }

    private static final int[] DEFFEND_OPTIONS = new int[]{1,2,};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        TextView attackingTerritoryTextView = view.findViewById(R.id.originTerritoryID);
        attackingTerritoryTextView.setText("SELECT NUMBER OF UNITS FROM: " + defendingTerritory.getTerritoryName());

        TextView defendingTerritoryTextView = view.findViewById(R.id.targetTerritoryID);
        defendingTerritoryTextView.setText("TO DEFEND TERRITORY: " + defendingTerritory.getTerritoryName());

        radioGroup = view.findViewById(R.id.troopSelection);
        addRadioButtons(DEFFEND_OPTIONS);

        return view;
    }

    private void addRadioButtons(int[] values) {
        RadioGroup ll = radioGroup;
        ll.setOrientation(LinearLayout.HORIZONTAL);
        boolean first = true;

        for (int i = 0; i < values.length && i  < this.defendingTerritory.getNArmies(); i++) {
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
