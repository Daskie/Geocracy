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
        View view = inflater.inflate(R.layout.troop_selection, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");
        Territory targetTerritory = (Territory) getArguments().get("targetTerritory");


        TextView originTerritoryID = view.findViewById(R.id.originTerritoryID);
        originTerritoryID.setText("Select Number of Units From: " + originTerritory.getTerritoryName());

        TextView targetTerritoryID = view.findViewById(R.id.targetTerritoryID);
        targetTerritoryID.setText("To Attack Territory: " + targetTerritory.getTerritoryName());

        radioGroup = view.findViewById(R.id.radio);

        Button confirmButton = view.findViewById(R.id.confirmButton);
        RxView.touches(confirmButton).subscribe(event -> {
            if (event.getActionMasked() == ACTION_UP){

                int selectedId = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioButton = view.findViewById(selectedId);

                int numArmiesSelected = Integer.parseInt(radioButton.getText().toString());
                if(numArmiesSelected<=originTerritory.getNArmies()) {
                    player.setNumArmiesAttacking(numArmiesSelected);

                    EventBus.publish("USER_ACTION", new GameEvent(GameAction.CONFIRM_UNITS_TAPPED, null));
                }
                else
                    Toasty.warning(this.getContext(), "You do not have enough armies in this territory to attack with the number you selected! ",  Toast.LENGTH_LONG).show();

            }
        });

        return view;
    }
}
