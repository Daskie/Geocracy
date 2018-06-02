package csc_cccix.geocracy.states;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;

public class SelectedTerritoryState implements  GameState {

    private Game game;
    private Territory territory;

    public SelectedTerritoryState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        if (this.territory == null) {
            System.out.println("SELECTED TERRITORY STATE: TERRITORY SELECTED, DISPLAY DETAILS");
        } else {
            System.out.println("SELECTED TERRITORY STATE: ANOTHER TERRITORY SELECTED, SWITCH TO OTHER TERRITORY TO DISPLAY DETAILS");
        }
        this.territory = territory;

    }

    public void selectTargetTerritory(Territory territory) {
        System.out.println("SELECTED TERRITORY STATE: TARGET TERRITORY ACTION UNAVAILABLE");
    }

    public void enableAttackMode() {
        game.setState(game.intentToAttackState);
        game.getState().selectOriginTerritory(territory);
        game.getState().initState();
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        System.out.println("SELECTED TERRITORY STATE: CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        System.out.println("SELECTED TERRITORY STATE: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        System.out.println("SELECTED TERRITORY STATE: INVALID STATE ACCESSED");
    }

    public void cancelAction() {
        System.out.println("USER CANCELED ACTION -> ENTER DEFAULT STATE");
        this.territory = null;
        game.setState(game.defaultState);
        game.getState().initState();
    }

    public void initState() {
        System.out.println("INIT SELECT TERRITORY STATE");
        game.activity.showBottomPaneFragment(TerritoryDetailFragment.newInstance(this.territory));
        game.getWorld().selectTerritory(this.territory);
        game.getWorld().unhighlightTerritories();
        game.cameraController.targetTerritory(this.territory);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_CANCEL_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);

    }

}