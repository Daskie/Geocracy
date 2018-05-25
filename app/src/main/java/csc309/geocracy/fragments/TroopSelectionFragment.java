package csc309.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import csc309.geocracy.EventBus;
import csc309.geocracy.R;
import csc309.geocracy.states.GameAction;
import csc309.geocracy.states.GameEvent;
import csc309.geocracy.world.Territory;

import static android.view.MotionEvent.ACTION_UP;

public class TroopSelectionFragment extends Fragment {

    private static final String TAG = "TROOP_SELECTION_FRAGMENT";


    private Territory targetTerritory;
    private Territory originTerritory;


    public static TroopSelectionFragment newInstance(Territory originTerritory, Territory targetTerritory) {
        TroopSelectionFragment newFragment = new TroopSelectionFragment();

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

        this.originTerritory = (Territory) getArguments().get("originTerritory");
        this.targetTerritory = (Territory) getArguments().get("targetTerritory");

        TextView originTerritoryID = view.findViewById(R.id.originTerritoryID);
        originTerritoryID.setText("Select Number of Units From: " + originTerritory.getTerritoryName());

        TextView targetTerritoryID = view.findViewById(R.id.targetTerritoryID);
        targetTerritoryID.setText("To Attack Territory: " + targetTerritory.getTerritoryName());

        Button confirmButton = view.findViewById(R.id.confirmButton);
        RxView.touches(confirmButton).subscribe((event) -> {
            if (event.getActionMasked() == ACTION_UP) EventBus.publish("USER_ACTION", new GameEvent(GameAction.CONFIRM_UNITS_TAPPED, null));
        });

        // get the bottom sheet view
        LinearLayout llBottomSheet = view.findViewById(R.id.bottom_sheet);

        return view;
    }
}
