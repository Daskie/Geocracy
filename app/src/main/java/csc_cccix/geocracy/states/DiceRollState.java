
package csc_cccix.geocracy.states;

import android.util.Log;

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
    private int attackerArmies;
    private int defenderArmies;
    private Player[] winners = new Player[3];

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

    public void enableAttackMode() {
        Log.i(TAG, "INVALID ACTION: -> CANNOT ENABLE ATTACK MODE");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "INVALID ACTION: -> ALREADY PERFORMING DICE ROLL");
        this.attackerArmies = attackerDetails.unitCount;
        this.defenderArmies = defenderDetails.unitCount;
        roll(attackerDetails.unitCount, defenderDetails.unitCount);

        Player attacker = this.originTerritory.getOwner();
        Player defender = this.targetTerritory.getOwner();

        attacker.sortDie();
        defender.sortDie();

        for(int i = 2; i > 0; i--){
            this.winners[2-i] = checkWinner(attacker, defender, i);
        }
    }

    private void roll(int attackerNumDie, int defenderNumDie){
        for(int i = 0; i < attackerNumDie; i++)
            this.originTerritory.getOwner().setDie(i, (int)(Math.random()*6) + 1);

        for(int i = 0; i < defenderNumDie; i++)
            this.targetTerritory.getOwner().setDie(i, (int)(Math.random()*6) + 1);

    }

    private Player checkWinner(Player attacker, Player defender, int index){
        int attackerDie = attacker.getDie()[index];
        int defenderDie = defender.getDie()[index];
        if(attackerDie==-1 || defenderDie==-1)
            return null;
        else if(attackerDie>defenderDie)
            return attacker;
        else
            return defender;
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "BATTLE COMPLETED -> ENTER BATTLE RESULTS STATE");
        game.setState(new BattleResultsState(game));
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().selectTargetTerritory(this.targetTerritory);
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
        game.getActivity().showBottomPaneFragment(DiceRollFragment.newInstance(this.originTerritory, this.targetTerritory, this.originTerritory.getOwner().getDie(), this.targetTerritory.getOwner().getDie(), this.winners));
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.targetTerritory);
        game.getCameraController().targetTerritory(this.targetTerritory);
        game.getActivity().runOnUiThread(() -> {
            game.getActivity().hideAllGameInteractionButtons();
        });

        Completable.timer(8, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::goToBattleResults);

    }

    private void goToBattleResults() {
        this.battleCompleted(new BattleResultDetails(this.targetTerritory, attackerArmies, this.originTerritory, defenderArmies));
    }

}