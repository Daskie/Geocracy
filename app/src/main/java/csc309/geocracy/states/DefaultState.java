package csc309.geocracy.states;

import csc309.geocracy.game.Game;
import csc309.geocracy.game.GameActivity;
import csc309.geocracy.world.Territory;

public class DefaultState implements GameState {

    private Game game;

    public DefaultState(Game game) {
        this.game = game;
    }

    public void selectTerritory(Territory territory) {
        System.out.println("DEFAULT STATE: TERRITORY SELECTED ACTION -> DISPLAY TERRITORY DETAILS");
        game.setState(game.SelectedTerritoryState);
        game.getState().selectTerritory(territory);
        game.getState().initState();
    }

    public void enableAttackMode() {
        System.out.println("DEFAULT STATE: CANNOT ENABLE ATTACK MODE");
    }

    public void cancelAction() {
        System.out.println("DEFAULT STATE: USER CANCELED ACTION -> NULL ACTION");
    }

    public void initState() {
        GameActivity.removeActiveBottomPaneFragment();
        this.game.world.unselectTerritory();
        this.game.world.unhighlightTerritories();
    }
}