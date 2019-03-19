package csc_cccix.geocracy.fragments;

/*import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.view_models.TerritoryDetailViewModel;
import glm_.vec3.Vec3;

public class TerritoryDetailFragment extends Fragment {

    public static TerritoryDetailFragment newInstance() {
        return new TerritoryDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.territory_detail, container, false);

        TerritoryDetailViewModel viewModel = ViewModelProviders.of(getActivity()).get(TerritoryDetailViewModel.class);

        viewModel.getSelectedTerritory().observe(this, t -> {

            TextView territoryID = view.findViewById(R.id.territoryDetails);
            territoryID.setText("TERRITORY: " + t.getTerritoryName() + "\nUNITS: " + t.getNArmies());

            ImageView ownerIcon = view.findViewById(R.id.playerIcon);

            if (t.getOwner() != null) {
                ownerIcon.setImageResource(R.drawable.account);

                Vec3 color = t.getOwner().getColor();
                ownerIcon.setBackgroundColor(Util.colorToInt(color));
            } else {
                ownerIcon.setVisibility(View.INVISIBLE);
            }

        });

        return view;
    }
}*/
