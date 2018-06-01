package cscCCCIX.geocracy.states;

import cscCCCIX.geocracy.EventBus;
import cscCCCIX.geocracy.game.Game;
import cscCCCIX.geocracy.game.UIEvent;
import cscCCCIX.geocracy.world.Territory;

public class DefaultState implements GameState {

    private Game game;

    public DefaultState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("DEFAULT STATE: TERRITORY SELECTED ACTION -> DISPLAY TERRITORY DETAILS");
        game.setState(game.selectedTerritoryState);
        game.getState().selectOriginTerritory(territory);
        game.getState().initState();
    }

    public void selectTargetTerritory(Territory territory) {
        System.out.println("DEFAULT STATE: CANNOT SELECT TARGET TERRITORY, NO ORIGIN TERRITORY");
    }

    public void enableAttackMode() {
        System.out.println("DEFAULT STATE: CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        System.out.println("DEFAULT STATE: CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        System.out.println("DEFAULT STATE: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        System.out.println("DEFAULT STATE: INVALID STATE ACCESSED");
    }

    public void cancelAction() {
        System.out.println("DEFAULT STATE: USER CANCELED ACTION -> NULL ACTION");
    }

    public void initState() {
        game.activity.removeActiveBottomPaneFragment();
        game.getWorld().unselectTerritory();
        game.getWorld().unhighlightTerritories();
        EventBus.publish("UI_EVENT", UIEvent.HIDE_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_CANCEL_BUTTON);
    }
}