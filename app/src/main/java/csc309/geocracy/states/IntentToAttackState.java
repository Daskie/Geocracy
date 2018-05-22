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

    public void selectOriginTerritory(Territory territory) {
        System.out.println("INTENT TO ATTACK STATE: -> ALREADY CURRENT STATE");
        this.originTerritory = territory;
        initState();
    }

    public void selectTargetTerritory(Territory targetTerritory) {
        System.out.println("INTENT TO ATTACK STATE: ANOTHER TERRITORY SELECTED -> GO TO SELECTED ATTACK TARGET STATE");
        game.setState(game.SelectedAttackTargetTerritoryState);
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().selectTargetTerritory(targetTerritory);
        game.getState().initState();
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