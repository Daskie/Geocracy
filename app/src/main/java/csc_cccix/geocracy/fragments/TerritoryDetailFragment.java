package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.world.Territory;
import glm_.vec3.Vec3;

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

        TextView territoryID = view.findViewById(R.id.territoryDetails);
        territoryID.setText("TERRITORY: " + territory.getTerritoryName() + "\nUNITS: " + territory.getNArmies());


        ImageView ownerIcon = view.findViewById(R.id.playerIcon);

        if (territory.getOwner() != null) {
            ownerIcon.setImageResource(R.drawable.account);

            Vec3 color = territory.getOwner().getColor();
            ownerIcon.setBackgroundColor(Util.colorToInt(color));
        } else {
            ownerIcon.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
