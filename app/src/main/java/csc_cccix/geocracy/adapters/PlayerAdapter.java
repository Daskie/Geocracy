package csc_cccix.geocracy.adapters;



import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import csc_cccix.R;
import csc_cccix.geocracy.game.Player;
import glm_.vec3.Vec3;


public class PlayerAdapter extends ArrayAdapter<Player> {

    private Context mContext;
    private List<Player> playerList = new ArrayList<>();

    public PlayerAdapter(@NonNull Context context, ArrayList<Player> list) {
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

        ImageView image = (ImageView)listItem.findViewById(R.id.playerIcon);
        image.setImageResource(R.drawable.account);

        // TODO: set player color icon accordingly
        Vec3 color = currentPlayer.getColor();
//        image.setBackgroundColor(Color.rgb((int) color.x, (int) color.y, (int) color.z));
        
        TextView details = (TextView) listItem.findViewById(R.id.playerDetails);
        details.setText(currentPlayer.getName());

        return listItem;
    }
}