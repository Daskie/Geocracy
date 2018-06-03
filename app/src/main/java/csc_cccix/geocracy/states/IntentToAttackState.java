package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;

public class IntentToAttackState implements  GameState {

    private Game game;
    private Territory originTerritory;

    public IntentToAttackState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i("", "INTENT TO ATTACK STATE: -> ALREADY CURRENT STATE");
        this.originTerritory = territory;
    }

    public void selectTargetTerritory(Territory targetTerritory) {
        Log.i("", "INTENT TO ATTACK STATE: ANOTHER TERRITORY SELECTED -> GO TO SELECTED ATTACK TARGET STATE");
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
        Log.i("", "INTENT TO ATTACK STATE: -> Disable Attack Mode");
        game.setState(game.selectedTerritoryState);
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().initState();
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i("", "INTENT TO ATTACK STATE: CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i("", "INTENT TO ATTACK STATE: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i("intentToAttackState", "INTENT TO ATTACK STATE: INVALID STATE ACCESSED");
    }

    public void confirmAction() {
        Log.i("", "SETUP INITIAL TERRITORIES STATE: USER CANCELED ACTION -> N/A");
    }

    public void cancelAction() {
        Log.i("", "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.defaultState);
        game.getState().initState();
    }

    public void initState() {
        Log.i("", "INIT INTENT TO ATTACK STATE");
        Log.i("", "TERRITORY SELECTED, ATTACK MODE ENABLED: -> DISPLAY ADJACENT TERRITORIES AVAILABLE TO ATTACK");
        game.getWorld().highlightTerritories(originTerritory.getAdjacentTerritories());
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_ACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_CANCEL_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);

    }

}