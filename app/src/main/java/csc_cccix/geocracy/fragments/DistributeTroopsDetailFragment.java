package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.game.Player;
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
        if (originTerritory != null) originTerritoryID.setText("UPDATE UNITS AT: " + originTerritory.getTerritoryName());
        else originTerritoryID.setText("SELECT A TERRITORY TO ADD/REMOVE UNITS");

        TextView unitPoolCount = view.findViewById(R.id.unitPoolCount);
        unitPoolCount.setText("Remaining Number of Units: " + currentPlayer.getArmyPool());

        return view;
    }
}
