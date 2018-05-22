package csc309.geocracy.states;

import android.os.Bundle;

import csc309.geocracy.fragments.TroopSelectionFragment;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.GameActivity;
import csc309.geocracy.world.Territory;

public class SelectedAttackTargetTerritoryState implements  GameState {

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;

    public SelectedAttackTargetTerritoryState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("SELECTED ATTACK TARGET TERRITORY STATE: SETTING ORIGIN TERRITORY");
        this.originTerritory = territory;
    }
    public void selectTargetTerritory(Territory territory) {
        System.out.println("SELECTED ATTACK TARGET TERRITORY STATE: SETTING TARGET TERRITORY");
        this.targetTerritory = territory;
    }

    public void enableAttackMode() {
        System.out.println("SELECTED ATTACK TARGET TERRITORY STATE: -> CANNOT ENABLE ATTACK MODE");
    }

    public void cancelAction() {
        System.out.println("USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.DefaultState);
        game.getState().initState();
    }

    public void initState() {
        System.out.println("INIT SELECTED ATTACK TARGET TERRITORY STATE:");
        Bundle args = new Bundle();
        args.putSerializable("territory", this.targetTerritory);
        GameActivity.showBottomPaneFragment(TroopSelectionFragment.newInstance(this.targetTerritory));
        this.game.getWorld().selectTerritory(this.targetTerritory);
        this.game.getWorld().unhighlightTerritories();
        this.game.cameraController.targetTerritory(this.targetTerritory);
    }

}