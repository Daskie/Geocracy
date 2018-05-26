package csc309.geocracy.states;

import csc309.geocracy.EventBus;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.UIEvent;
import csc309.geocracy.world.Territory;

public class SetUpInitTerritories implements GameState {

    private Game game;
    private Territory territory;

    public SetUpInitTerritories(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("SETUP TERRITORY STATE: ANOTHER TERRITORY SELECTED");
        this.territory = territory;

        territory.setOwner(game.players[game.currentPlayer]);
        game.players[game.currentPlayer].addTerritory(territory);
        territory.setNArmies(1);

        System.out.println("PLAYER" + game.currentPlayer + " ADDED " + territory.getTerritoryName());


        game.currentPlayer++;
        if(game.currentPlayer==game.players.length)
            game.currentPlayer=0;

    }

    public void selectTargetTerritory(Territory territory) {
        System.out.println("SETUP TERRITORY STATE: CANNOT SELECT TARGET TERRITORY");
    }

    public void enableAttackMode() {
        System.out.println("SETUP TERRITORY STATE: CANNOT ENABLE ATTACK MODE");
    }

    public void cancelAction() {
        System.out.println("SETUP STATE: USER CANCELED ACTION -> NULL ACTION");
    }

    public void initState() {
        game.activity.removeActiveBottomPaneFragment();
        game.getWorld().selectTerritory(this.territory);
        game.getWorld().unhighlightTerritories();
        game.cameraController.targetTerritory(this.territory);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_CANCEL_BUTTON);
    }
}
