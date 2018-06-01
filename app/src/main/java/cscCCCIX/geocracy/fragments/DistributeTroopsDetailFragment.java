package cscCCCIX.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import cscCCCIX.R;
import cscCCCIX.geocracy.world.Territory;

public class DistributeTroopsDetailFragment extends Fragment {

    public static DistributeTroopsDetailFragment newInstance(Territory originTerritory) {
        DistributeTroopsDetailFragment newFragment = new DistributeTroopsDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.distribute_troops_detail, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");

        TextView originTerritoryID = view.findViewById(R.id.originTerritoryID);
        originTerritoryID.setText("Update Unit Count At: " + originTerritory.getTerritoryName());

        Button confirmButton = view.findViewById(R.id.confirmButton);
        RxView.touches(confirmButton).subscribe((event) -> {
//            if (event.getActionMasked() == ACTION_UP) EventBus.publish("USER_ACTION", new GameEvent(GameAction.CONFIRM_UNITS_TAPPED, null));
        });

        return view;
    }
}
