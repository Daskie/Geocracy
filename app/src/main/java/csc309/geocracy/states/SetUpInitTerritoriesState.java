package csc309.geocracy.states;

import android.widget.Toast;

import csc309.geocracy.EventBus;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.UIEvent;
import csc309.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class SetUpInitTerritoriesState implements GameState {

    private Game game;
    private Territory territory;

    public SetUpInitTerritoriesState(Game game) {
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

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails){

    }
    public void battleCompleted(BattleResultDetails battleResultDetails){

    }

    public void enableAttackMode() {
        System.out.println("SETUP TERRITORY STATE: CANNOT ENABLE ATTACK MODE");
    }

    public void cancelAction() {
        System.out.println("SETUP STATE: USER CANCELED ACTION -> NULL ACTION");
    }


    public void initState() {
        System.out.println("INIT DICE ROLL STATE:");

        game.activity.removeActiveBottomPaneFragment();
        game.getWorld().selectTerritory(this.territory);
        game.getWorld().unhighlightTerritories();
        game.cameraController.targetTerritory(this.territory);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_CANCEL_BUTTON);
    }
}
