package csc309.geocracy.states;

import android.os.Bundle;

import csc309.geocracy.EventBus;
import csc309.geocracy.fragments.TerritoryDetailFragment;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.GameActivity;
import csc309.geocracy.game.UIEvent;
import csc309.geocracy.world.Territory;

public class SelectedTerritoryState implements  GameState {

    private Game game;
    private Territory territory;

    public SelectedTerritoryState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("SELECTED TERRITORY STATE: ANOTHER TERRITORY SELECTED, SWITCH TO OTHER TERRITORY TO DISPLAY DETAILS");
        this.territory = territory;
    }

    public void selectTargetTerritory(Territory territory) {
        System.out.println("SELECTED TERRITORY STATE: TARGET TERRITORY ACTION UNAVAILABLE");
    }

    public void enableAttackMode() {
        game.setState(game.IntentToAttackState);
        game.getState().selectOriginTerritory(territory);
        game.getState().initState();
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        System.out.println("SELECTED TERRITORY STATE: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        System.out.println("SELECTED TERRITORY STATE: INVALID STATE ACCESSED");
    }

    public void cancelAction() {
        System.out.println("USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.DefaultState);
        game.getState().initState();
    }

    public void initState() {
        System.out.println("INIT SELECT TERRITORY STATE");
        game.activity.showBottomPaneFragment(TerritoryDetailFragment.newInstance(this.territory));
        game.getWorld().selectTerritory(this.territory);
        game.getWorld().unhighlightTerritories();
        game.cameraController.targetTerritory(this.territory);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_CANCEL_BUTTON);
    }

}