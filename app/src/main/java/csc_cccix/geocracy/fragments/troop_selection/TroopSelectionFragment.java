package csc_cccix.geocracy.fragments.troop_selection;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import csc_cccix.R;
import csc_cccix.geocracy.game.view_models.TroopSelectionViewModel;
import csc_cccix.geocracy.backend.world.Territory;

public abstract class TroopSelectionFragment extends Fragment {

    View view;
    TroopSelectionViewModel viewModel;
    RadioGroup radioGroup;
    RadioButton radioButton;

    LiveData<Territory> attackingTerritory;
    LiveData<Territory> defendingTerritory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.troop_selection, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(TroopSelectionViewModel.class);
        attackingTerritory = viewModel.getAttackingTerritory();
        defendingTerritory = viewModel.getDefendingTerritory();

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
