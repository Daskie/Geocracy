package csc_cccix.geocracy.fragments;

/*import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.game.GameActivity;

public class FortifyTerritoryFragment extends Fragment {

    public static FortifyTerritoryFragment newInstance(int originTerritory, int targetTerritory) {
        FortifyTerritoryFragment newFragment = new FortifyTerritoryFragment();
        Bundle args = new Bundle();
        args.putInt("originTerritoryId", originTerritory);
        args.putInt("targetTerritoryId", targetTerritory);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fortify_territory, container, false);

        GameActivity gameActivity = (GameActivity) getActivity();
        Game game = gameActivity.game;

        Game.Territory originTerritory = game.territories.get(getArguments().getInt("originTerritoryId"));
        Game.Territory targetTerritory = game.territories.get(getArguments().getInt("targetTerritoryId"));

        TextView originTerritoryText = view.findViewById(R.id.originTerritoryID);
        originTerritoryText.setText("MOVE UNITS FROM: " + originTerritory.name);

        TextView targetTerritoryText = view.findViewById(R.id.targetTerritoryID);
        targetTerritoryText.setText("TO TERRITORY: " + targetTerritory.name);

        return view;
    }

}*/
