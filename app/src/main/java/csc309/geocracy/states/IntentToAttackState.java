package csc309.geocracy.states;

import android.os.Bundle;

import csc309.geocracy.fragments.TerritoryDetailFragment;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.GameActivity;
import csc309.geocracy.world.Territory;

public class IntentToAttackState implements  GameState {

    private Game game;
    private Territory originTerritory;

    public IntentToAttackState(Game game) {
        this.game = game;
    }

    public void selectTerritory(Territory territory) {
        System.out.println("SELECTED TERRITORY STATE: ANOTHER TERRITORY SELECTED, SWITCH TO OTHER TERRITORY TO DISPLAY DETAILS");
        this.originTerritory = territory;
    }

    public void enableAttackMode() {
        System.out.println("ATTACK MODE ALREADY ENABLED");
    }

    public void cancelAction() {
        System.out.println("USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.DefaultState);
        game.getState().initState();
    }

    public void initState() {
        System.out.println("INIT SELECT TERRITORY STATE");
        System.out.println("TERRITORY SELECTED, ATTACK MODE ENABLED: -> DISPLAY ADJACENT TERRITORIES AVAILABLE TO ATTACK");
        game.world.highlightTerritories(originTerritory.getAdjacentTerritories());
    }

}