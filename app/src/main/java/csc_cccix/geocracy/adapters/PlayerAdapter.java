package csc_cccix.geocracy.adapters;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import csc_cccix.R;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.Player;
import glm_.vec3.Vec3;


public class PlayerAdapter extends ArrayAdapter<Player> {

    private Context mContext;
    private List<Player> playerList;

    public PlayerAdapter(@NonNull Context context, List<Player> list) {
        super(context, 0 , list);
        mContext = context;
        playerList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.player_list_item,parent,false);

        Player currentPlayer = playerList.get(position);

        ImageView image = listItem.findViewById(R.id.playerIcon);
        image.setImageResource(R.drawable.account);

        Vec3 color = currentPlayer.getColor();
        image.setBackgroundColor(Util.colorToInt(color));

        TextView details = listItem.findViewById(R.id.playerName);
        details.setText(currentPlayer.getName());

        TextView unitCount = listItem.findViewById(R.id.playerDetails);
        unitCount.setText("Armies: " + currentPlayer.getDeployedArmyCount() + " | Territories: " + currentPlayer.getNTerritories());

        return listItem;
    }
}