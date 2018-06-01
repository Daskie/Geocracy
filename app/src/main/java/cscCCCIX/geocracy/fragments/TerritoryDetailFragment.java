package cscCCCIX.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cscCCCIX.R;
import cscCCCIX.geocracy.world.Territory;

public class TerritoryDetailFragment extends Fragment {

    public static TerritoryDetailFragment newInstance(Territory territory) {
        TerritoryDetailFragment newFragment = new TerritoryDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable("territory", territory);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.territory_detail, container, false);

        Territory territory = (Territory) getArguments().get("territory");

        TextView territoryID = view.findViewById(R.id.territoryID);
        territoryID.setText("Territory: " + territory.getTerritoryName());

        TextView territoryOwner = view.findViewById(R.id.territoryOwner);
        territoryOwner.setText("Territory Owned by Player: " + territory.getOwner().getName() + " (" + territory.getOwner().getId() + ")");

        TextView numberOfUnits = view.findViewById(R.id.numberOfUnits);
        numberOfUnits.setText("Number of Units in Territory: " + territory.getNArmies());

        return view;
    }
}
