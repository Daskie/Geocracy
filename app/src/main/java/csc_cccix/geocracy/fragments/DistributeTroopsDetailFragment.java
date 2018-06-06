package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import csc_cccix.R;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.states.GameAction;
import csc_cccix.geocracy.states.GameEvent;
import csc_cccix.geocracy.world.Territory;

public class DistributeTroopsDetailFragment extends Fragment {

    public static DistributeTroopsDetailFragment newInstance(Territory originTerritory, Player currentPlayer) {
        DistributeTroopsDetailFragment newFragment = new DistributeTroopsDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("currentPlayer", currentPlayer);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.distribute_troops_detail, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");
        Player currentPlayer = (Player) getArguments().get("currentPlayer");

        TextView originTerritoryID = view.findViewById(R.id.originTerritoryID);
        if (originTerritory != null) originTerritoryID.setText("Update Unit Count At: " + originTerritory.getTerritoryName());
        else originTerritoryID.setText("SELECT A TERRITORY");


        TextView unitPoolCount = view.findViewById(R.id.unitPoolCount);
        unitPoolCount.setText("Remaining Number of Units: " + currentPlayer.getArmyPool());

        Button confirmButton = view.findViewById(R.id.confirmButton);

        RxView.touches(confirmButton).subscribe(event -> {
            Log.i("CONFIRM_BTN", "CONFIRM TAPPED!");
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) EventBus.publish("USER_ACTION", new GameEvent(GameAction.CONFIRM_ACTION, null));
        });

        return view;
    }
}
