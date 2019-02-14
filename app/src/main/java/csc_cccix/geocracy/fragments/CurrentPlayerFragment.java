package csc_cccix.geocracy.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.Player;
import glm_.vec3.Vec3;

public class CurrentPlayerFragment extends Fragment {

    public static CurrentPlayerFragment newInstance(Player player) {
        CurrentPlayerFragment newFragment = new CurrentPlayerFragment();

        Bundle args = new Bundle();
        args.putSerializable("player", player);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_player, container, false);

        Player player = (Player) getArguments().get("player");

        TextView currentPlayer = view.findViewById(R.id.currentPlayer);
        currentPlayer.setText(player.getName());

        ImageView ownerIcon = view.findViewById(R.id.playerIcon);
        ownerIcon.setImageResource(R.drawable.account);
        Vec3 color = player.getColor();
        ownerIcon.setBackgroundColor(Util.colorToInt(color));

        return view;
    }
}
