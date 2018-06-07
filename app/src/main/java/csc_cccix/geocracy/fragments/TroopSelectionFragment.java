package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import csc_cccix.R;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.states.GameAction;
import csc_cccix.geocracy.states.GameEvent;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

import static android.view.MotionEvent.ACTION_UP;

public class TroopSelectionFragment extends Fragment {

    private View view;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private static Player player;

    public static TroopSelectionFragment newInstance(Territory originTerritory, Territory targetTerritory, Player attackingPlayer) {
        TroopSelectionFragment newFragment = new TroopSelectionFragment();
        player = attackingPlayer;
        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("targetTerritory", targetTerritory);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.troop_selection, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");
        Territory targetTerritory = (Territory) getArguments().get("targetTerritory");

        TextView originTerritoryID = view.findViewById(R.id.originTerritoryID);
        originTerritoryID.setText("SELECT NUMBER OF UNITS FROM: " + originTerritory.getTerritoryName());

        TextView targetTerritoryID = view.findViewById(R.id.targetTerritoryID);
        targetTerritoryID.setText("TO ATTACK TERRITORY: " + targetTerritory.getTerritoryName());

        radioGroup = view.findViewById(R.id.radio);

        return view;
    }

    public int getSelectedNumberOfUnits() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(selectedId);
        return Integer.parseInt(radioButton.getText().toString());
    }
}
