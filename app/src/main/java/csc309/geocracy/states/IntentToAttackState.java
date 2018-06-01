package csc309.geocracy.states;

import csc309.geocracy.EventBus;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.UIEvent;
import csc309.geocracy.world.Territory;

public class IntentToAttackState implements  GameState {

    private Game game;
    private Territory originTerritory;

    public IntentToAttackState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("INTENT TO ATTACK STATE: -> ALREADY CURRENT STATE");
        this.originTerritory = territory;
    }

    public void selectTargetTerritory(Territory targetTerritory) {
        System.out.println("INTENT TO ATTACK STATE: ANOTHER TERRITORY SELECTED -> GO TO SELECTED ATTACK TARGET STATE");
        if (originTerritory.getAdjacentTerritories().contains(targetTerritory)) {
            game.setState(game.SelectedAttackTargetTerritoryState);
            game.getState().selectOriginTerritory(this.originTerritory);
            game.getState().selectTargetTerritory(targetTerritory);
            game.getState().initState();
        } else {
            cancelAction();
        }

    }

    public void enableAttackMode() {
        System.out.println("INTENT TO ATTACK STATE: -> Disable Attack Mode");
        game.setState(game.SelectedTerritoryState);
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().initState();
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        System.out.println("INTENT TO ATTACK STATE: CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        System.out.println("INTENT TO ATTACK STATE: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        System.out.println("INTENT TO ATTACK STATE: INVALID STATE ACCESSED");
    }

    public void cancelAction() {
        System.out.println("USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.DefaultState);
        game.getState().initState();
    }

    public void initState() {
        System.out.println("INIT INTENT TO ATTACK STATE");
        System.out.println("TERRITORY SELECTED, ATTACK MODE ENABLED: -> DISPLAY ADJACENT TERRITORIES AVAILABLE TO ATTACK");
        game.getWorld().highlightTerritories(originTerritory.getAdjacentTerritories());
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_ACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_CANCEL_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);

    }

}