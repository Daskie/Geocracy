package csc309.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import csc309.geocracy.R;
import csc309.geocracy.world.Territory;

public class TerritoryDetailFragment extends Fragment {

    private static final String TAG = "TROOP_SELECTION_FRAGMENT";
    private Territory territory;

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

        this.territory = (Territory) getArguments().get("territory");
        System.out.println("HELLO? " + this.territory);

        TextView territoryID = view.findViewById(R.id.territoryID);
        territoryID.setText("Territory ID: " + territory.getId());

        // get the bottom sheet view
        LinearLayout llBottomSheet = view.findViewById(R.id.bottom_sheet);

        return view;
    }
}
