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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.view_models.GameViewModel;
import glm_.vec3.Vec3;

public class CurrentPlayerFragment extends Fragment {

    public static CurrentPlayerFragment newInstance() {
        return new CurrentPlayerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_player, container, false);

        GameViewModel gameViewModel = ViewModelProviders.of(getActivity()).get(GameViewModel.class);
        LiveData<Player> player = gameViewModel.getCurrentPlayer();

        player.observe(this, p -> {
            TextView currentPlayer = view.findViewById(R.id.currentPlayer);
            currentPlayer.setText(p.getName());

            ImageView ownerIcon = view.findViewById(R.id.playerIcon);
            ownerIcon.setImageResource(R.drawable.account);
            Vec3 color = p.getColor();
            ownerIcon.setBackgroundColor(Util.colorToInt(color));
        });

        return view;
    }
}*/
