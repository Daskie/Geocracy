package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;

public class IntentToAttackState implements  GameState {

    private static final String TAG = "INTENT_TO_ATTACK_STATE";

    private Game game;
    private Territory originTerritory;

    public IntentToAttackState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "ALREADY CURRENT STATE");
        this.originTerritory = territory;
    }

    public void selectTargetTerritory(Territory targetTerritory) {
        Log.i(TAG, "ANOTHER TERRITORY SELECTED -> GO TO SELECTED ATTACK TARGET STATE");
        if (originTerritory.getAdjacentTerritories().contains(targetTerritory)) {
            game.setState(game.selectedAttackTargetTerritoryState);
            game.getState().selectOriginTerritory(this.originTerritory);
            game.getState().selectTargetTerritory(targetTerritory);
            game.getState().initState();
        } else {
            cancelAction();
        }

    }

    public void enableAttackMode() {
        Log.i(TAG, "-> Disable Attack Mode");
        game.setState(game.selectedTerritoryState);
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().initState();
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "INVALID ACTION: CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "INVALID ACTION: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID ACTION: BATTLE COMPLETED INVALID STATE ACCESSED");
    }

    public void confirmAction() {
        Log.i(TAG, "INVALID ACTION: USER CONFIRMED ACTION");
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.defaultState);
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        Log.i(TAG, "TERRITORY SELECTED, ATTACK MODE ENABLED: -> DISPLAY ADJACENT TERRITORIES AVAILABLE TO ATTACK");
        game.getWorld().highlightTerritories(originTerritory.getAdjacentTerritories());

        String ui_tag = "UI_EVENT";

        EventBus.publish(ui_tag, UIEvent.SET_ATTACK_MODE_ACTIVE);
        EventBus.publish(ui_tag, UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish(ui_tag, UIEvent.SHOW_CANCEL_BUTTON);
        EventBus.publish(ui_tag, UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);

    }

}