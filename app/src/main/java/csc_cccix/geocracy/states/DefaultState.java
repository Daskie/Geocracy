package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;

public class DefaultState implements GameState {

    private static final String TAG = "DEFAULT_STATE";
    private Game game;

    public DefaultState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("DEFAULT STATE: TERRITORY SELECTED ACTION -> DISPLAY TERRITORY DETAILS");
        if(!game.getWorld().allTerritoriesOccupied())
            game.setState(game.setUpInitTerritoriesState);
        else
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

    public void confirmAction() {
        Log.i("", "SETUP INITIAL TERRITORIES STATE: USER CANCELED ACTION -> N/A");
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
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }
}