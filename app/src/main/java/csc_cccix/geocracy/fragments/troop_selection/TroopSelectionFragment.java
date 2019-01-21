package csc_cccix.geocracy.fragments.troop_selection;

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
import csc_cccix.geocracy.world.Territory;

public abstract class TroopSelectionFragment extends Fragment {

    View view;
    RadioGroup radioGroup;
    RadioButton radioButton;

    Territory attackingTerritory;
    Territory defendingTerritory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.troop_selection, container, false);

        attackingTerritory = (Territory) getArguments().get("attackingTerritory");
        defendingTerritory = (Territory) getArguments().get("defendingTerritory");

        radioGroup = view.findViewById(R.id.troopSelection);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);

        return view;
    }

    public int getSelectedNumberOfUnits() {
        if (radioGroup != null) {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            radioButton = view.findViewById(selectedId);

            if (radioButton != null) {
                return Integer.parseInt(radioButton.getText().toString());
            }
        }

        return -1;
    }

}
