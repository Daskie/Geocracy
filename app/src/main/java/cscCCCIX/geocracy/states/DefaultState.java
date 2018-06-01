package cscCCCIX.geocracy.states;

import android.util.Log;

import cscCCCIX.geocracy.EventBus;
import cscCCCIX.geocracy.game.Game;
import cscCCCIX.geocracy.game.UIEvent;
import cscCCCIX.geocracy.world.Territory;

public class DefaultState implements GameState {

    private static final String TAG = "DEFAULT_STATE";
    private Game game;

    public DefaultState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "TERRITORY SELECTED ACTION -> DISPLAY TERRITORY DETAILS");
        game.setState(game.selectedTerritoryState);
        game.getState().selectOriginTerritory(territory);
        game.getState().initState();
    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "CANNOT SELECT TARGET TERRITORY, NO ORIGIN TERRITORY");
    }

    public void enableAttackMode() {
        Log.i(TAG, "CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID STATE ACCESSED");
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> NULL ACTION");
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.activity.removeActiveBottomPaneFragment();
        game.getWorld().unselectTerritory();
        game.getWorld().unhighlightTerritories();
        EventBus.publish("UI_EVENT", UIEvent.HIDE_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_CANCEL_BUTTON);
    }
}