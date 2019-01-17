package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.world.Territory;


public abstract class IGameState {

    protected Game game;
    protected Territory territory;
    protected String TAG = "TAG UNAVAILABLE (Please set for clarity)";

    // ALL AVAILABLE ACTIONS

    // TODO: you should always be able to cancel -> it should lead to the same default state (pretty sure...)
    public void cancelAction() {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM CANCEL ACTION IN CURRENT STATE");
    }

    public void confirmAction() {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM CONFIRM ACTION IN CURRENT STATE");
    }

    public void fortifyAction() {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM FORTIFY ACTION IN CURRENT STATE");
    }

    public void selectPrimaryTerritory(Territory territory) {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM SELECT PRIMARY TERRITORY ACTION IN CURRENT STATE");
    }

    public void selectSecondaryTerritory(Territory territory) {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM SELECT SECONDARY TERRITORY ACTION IN CURRENT STATE");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM DICE ROLL ACTION IN CURRENT STATE");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM BATTLE COMPLETED ACTION IN CURRENT STATE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM MODIFY TERRITORY UNIT COUNT ACTION IN CURRENT STATE");
    }

    public void endTurn() {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM END TURN ACTION IN CURRENT STATE");
    }

    public void enableAttackMode() {
        Log.w(TAG, "INVALID ACTION: CANNOT PERFORM ENABLE ATTACK MODE ACTION IN CURRENT STATE");
    }

    public void initState() {}
}