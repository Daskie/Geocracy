

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
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;

public class DiceRollFragment extends Fragment {

    public static DiceRollFragment newInstance(Territory originTerritory, Territory targetTerritory, int[] attackerDie, int[] defenderDie, Player[] winners) {
        DiceRollFragment newFragment = new DiceRollFragment();

        Bundle args = new Bundle();
        args.putSerializable("originTerritory", originTerritory);
        args.putSerializable("targetTerritory", targetTerritory);
        args.putSerializable("attackerDie", attackerDie);
        args.putSerializable("defenderDie", defenderDie);
        args.putSerializable("winners", winners);
        newFragment.setArguments(args);

        return newFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dice_roll, container, false);

        Territory originTerritory = (Territory) getArguments().get("originTerritory");
        Territory targetTerritory = (Territory) getArguments().get("targetTerritory");
        int[] attackerDie = (int[]) getArguments().get("attackerDie");
        int[] defenderDie  = (int[]) getArguments().get("defenderDie");
        Player[] winners  = (Player[]) getArguments().get("winners");


        String attackerString = "";
        String defenderString = "";

        String winnerString = "";


        for(int i = attackerDie.length-1; i > -1; i--){
            if(attackerDie[i]!=-1) {
                attackerString += attackerDie[i];
                if (i!=0)
                    if(attackerDie[i-1] != -1)
                        attackerString += ", ";
            }
        }

        for(int j = defenderDie.length-1; j > -1; j--){
            if(defenderDie[j]!=-1) {
                defenderString += defenderDie[j];
                if (j!=0)
                    if(defenderDie[j-1] != -1)
                        defenderString += ", ";
            }
        }

        for(int k = 0; k<winners.length; k++){
            if(winners[k]!=null) {
                winnerString = winnerString + "ROUND " + (k+1) + " " + winners[k].getName() + " WINS";
                if(k!=winners.length-1)
                    winnerString += "\n";
            }
            else
                break;
        }

        TextView attackingPlayer = view.findViewById(R.id.attackingPlayer);
        attackingPlayer.setText("Attacker ( " + originTerritory.getTerritoryName() + " ) Rolls a: " + attackerString);

        TextView defendingPlayer = view.findViewById(R.id.defendingPlayer);
        defendingPlayer.setText("Defender ( " + targetTerritory.getTerritoryName() + " ) Rolls a: " + defenderString);

        TextView battleResult = view.findViewById(R.id.battleResult);
        battleResult.setText("RESULT: " + winnerString);

        return view;
    }
}
