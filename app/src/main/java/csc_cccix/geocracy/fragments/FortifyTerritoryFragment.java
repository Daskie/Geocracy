package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.world.Territory;

public class FortifyTerritoryFragment extends Fragment {

    public static FortifyTerritoryFragment newInstance(Territory originTerritory, Territory targetTerritory) {
        FortifyTerritoryFragment newFragment = new FortifyTerritoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("targetTerritory", targetTerritory);
        newFragment.setArguments(args);
        return newFragment;
    }

    private static final int[] ATTACK_OPTIONS = new int[]{1,2,3};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fortify_territory, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");
        Territory targetTerritory = (Territory) getArguments().get("targetTerritory");

        TextView originTerritoryID = view.findViewById(R.id.originTerritoryID);
        originTerritoryID.setText("MOVE UNITS FROM: " + originTerritory.getTerritoryName());

        TextView targetTerritoryID = view.findViewById(R.id.targetTerritoryID);

        if (targetTerritory != null) {
            targetTerritoryID.setText("TO TERRITORY: " + targetTerritory.getTerritoryName());
        } else {
            targetTerritoryID.setText("SELECT A TARGET TERRITORY!");

        }

        return view;
    }

}
