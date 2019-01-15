package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.world.Territory;


public abstract class GameState {

    protected Game game;
    protected Territory territory;
    protected static String TAG = "TAG UNAVAILABLE (Please set for clarity)";

    // ALL AVAILABLE ACTIONS
    public void cancelAction() {

    }

    public void confirmAction() {

    }

    public void fortifyAction() {

    }

    public void selectPrimaryTerritory(Territory territory) {

    }

    public void selectSecondaryTerritory(Territory territory) {
        Log.i(this.TAG, "SECONDARY TERRITORY ACTION UNAVAILABLE");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {

    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {

    }

    public void addToSelectedTerritoryUnitCount(int amount) {

    }

    public void endTurn() {

    }

    public void enableAttackMode() {

    }

    public void initState() {

    }
}