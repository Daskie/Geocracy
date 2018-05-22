package csc309.geocracy.states;

import android.os.Bundle;

import csc309.geocracy.fragments.TerritoryDetailFragment;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.GameActivity;
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
    }

    public void cancelAction() {
        System.out.println("USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.DefaultState);
        game.getState().initState();
    }

    public void initState() {
        System.out.println("INIT SELECT TERRITORY STATE");
        Bundle args = new Bundle();
        args.putSerializable("territory", this.territory);
        GameActivity.showBottomPaneFragment(TerritoryDetailFragment.newInstance(this.territory));
        this.game.world.selectTerritory(this.territory);
        this.game.world.unhighlightTerritories();
        this.game.cameraController.targetTerritory(this.territory);
    }

}