
package csc_cccix.geocracy.states;

import android.util.Log;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import csc_cccix.geocracy.fragments.DiceRollFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class DiceRollState implements  GameState {

    private static final String TAG = "DICE_ROLL_STATE";

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;
    private int attackerArmiesLost = 0;
    private int defenderArmiesLost = 0;
    private String attackerString = "";
    private String defenderString = "";

    private DiceRollDetails attackerDetails;
    private DiceRollDetails defenderDetails;


    public DiceRollState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "SETTING ORIGIN TERRITORY");
        this.originTerritory = territory;
    }
    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "SETTING TARGET TERRITORY");
        this.targetTerritory = territory;
    }

    public void fortifyAction() { Log.i(TAG, "CANNOT ENABLE FORTIFY MODE"); }

    public void enableAttackMode() {
        Log.i(TAG, "INVALID ACTION: -> CANNOT ENABLE ATTACK MODE");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "PERFORMING DICE ROLL");
        this.attackerDetails = attackerDetails;
        this.defenderDetails = defenderDetails;
        roll(attackerDetails.unitCount, defenderDetails.unitCount);

        Player attacker = this.originTerritory.getOwner();
        Player defender = this.targetTerritory.getOwner();

        attacker.sortDie();
        defender.sortDie();

        for(int i = 2; i > 0; i--) {
            boolean end = checkLosers(attacker, defender, i);
            if(end)
                break;
        }

        resultsToString();
    }

    private void roll(int attackerNumDie, int defenderNumDie){
        Random rGen = new Random();
        for(int i = 0; i < attackerNumDie; i++)
            this.originTerritory.getOwner().setDie(i, (rGen.nextInt()*6) + 1);

        for(int i = 0; i < defenderNumDie; i++)
            this.targetTerritory.getOwner().setDie(i, (rGen.nextInt()*6) + 1);

    }

    private boolean checkLosers(Player attacker, Player defender, int index){
        int attackerDie = attacker.getDie()[index];
        int defenderDie = defender.getDie()[index];
        if(attackerDie==-1 || defenderDie==-1)
            return true;
        else if(attackerDie<=defenderDie)
            attackerArmiesLost++;
        else
            defenderArmiesLost++;
        return false;
    }

    private void resultsToString(){

        int[] attackerDie = this.originTerritory.getOwner().getDie();
        int[] defenderDie = this.targetTerritory.getOwner().getDie();
        StringBuilder attackerStringBuilder = new StringBuilder();
        StringBuilder defenderStringBuilder = new StringBuilder();


        for(int i = attackerDie.length-1; i > -1; i--){
            if(attackerDie[i]!=-1) {
                this.attackerString += attackerDie[i];
                attackerStringBuilder.append(attackerDie[i]);
                if (i!=0)
                    if(attackerDie[i-1] != -1)
                        attackerStringBuilder.append(", ");
            }
        }

        for(int j = defenderDie.length-1; j > -1; j--){
            if(defenderDie[j]!=-1) {
                this.defenderString += defenderDie[j];
                defenderStringBuilder.append(defenderDie[j]);
                if (j!=0)
                    if(defenderDie[j-1] != -1)
                        defenderStringBuilder.append(", ");
            }
        }
        this.attackerString = attackerStringBuilder.toString();
        this.defenderString = defenderStringBuilder.toString();
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "BATTLE COMPLETED -> ENTER BATTLE RESULTS STATE");
        game.setState(new BattleResultsState(game));
        this.originTerritory.getOwner().resetDie();
        this.targetTerritory.getOwner().resetDie();
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().selectTargetTerritory(this.targetTerritory);
        game.getState().performDiceRoll(this.attackerDetails, this.defenderDetails);
        game.getState().battleCompleted(battleResultDetails);
        game.getState().initState();
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "INVALID ACTION: CANNOT UPDATE UNIT COUNT");
    }

    public void confirmAction() {
        Log.i(TAG, "USER CONFIRM ACTION -> N/A");
    }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> N/A"); }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(new DefaultState(game));
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.getActivity().showBottomPaneFragment(DiceRollFragment.newInstance(this.originTerritory, this.targetTerritory, this.attackerString, this.defenderString));
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.targetTerritory);
        game.getCameraController().targetTerritory(this.targetTerritory);
        game.getActivity().runOnUiThread(() -> game.getActivity().hideAllGameInteractionButtons());

        Completable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::goToBattleResults);

    }


    private void goToBattleResults() {
        this.battleCompleted(new BattleResultDetails(this.attackerArmiesLost,this.defenderArmiesLost));
    }

}