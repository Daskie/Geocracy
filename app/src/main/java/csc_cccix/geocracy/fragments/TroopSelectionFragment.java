package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;

public class TroopSelectionFragment extends Fragment {

    private View view;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private static Player player;
    private Territory originTerritory;

    public static TroopSelectionFragment newInstance(Territory originTerritory, Territory targetTerritory, Player attackingPlayer) {
        TroopSelectionFragment newFragment = new TroopSelectionFragment();
        player = attackingPlayer;
        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("targetTerritory", targetTerritory);
        newFragment.setArguments(args);

        return newFragment;
    }

    private static final int[] ATTACK_OPTIONS = new int[]{2,3,4};
    private static final int[] DEFEND_OPTIONS = new int[]{1,2};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.troop_selection, container, false);

        originTerritory = (Territory) getArguments().get("originTerritory");
        Territory targetTerritory = (Territory) getArguments().get("targetTerritory");

        TextView originTerritoryID = view.findViewById(R.id.originTerritoryID);
        originTerritoryID.setText("SELECT NUMBER OF UNITS FROM: " + originTerritory.getTerritoryName());

        TextView targetTerritoryID = view.findViewById(R.id.targetTerritoryID);
        targetTerritoryID.setText("TO ATTACK TERRITORY: " + targetTerritory.getTerritoryName());

        radioGroup = view.findViewById(R.id.troopSelection);
        addRadioButtons(ATTACK_OPTIONS);

        return view;
    }

    private void addRadioButtons(int[] values) {
        RadioGroup ll = radioGroup;
        ll.setOrientation(LinearLayout.HORIZONTAL);
        boolean first = true;

        for (int i = 0; i < values.length && i + 1 < this.originTerritory.getNArmies(); i++) {
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

    public int getSelectedNumberOfUnits() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(selectedId);
        return Integer.parseInt(radioButton.getText().toString());
    }
}
