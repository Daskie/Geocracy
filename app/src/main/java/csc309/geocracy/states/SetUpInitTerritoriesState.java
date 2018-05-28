package csc309.geocracy.states;

import android.widget.Toast;

import csc309.geocracy.EventBus;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.GameActivity;
import csc309.geocracy.game.Player;
import csc309.geocracy.game.UIEvent;
import csc309.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class SetUpInitTerritoriesState implements GameState {

    private Game game;
    private Territory territory;
    private static GameActivity parent;


    public SetUpInitTerritoriesState(Game game, GameActivity parent) {
        this.game = game;
        this.parent = parent;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("SETUP TERRITORY STATE: ANOTHER TERRITORY SELECTED");
        this.territory = territory;

        //illegal territory selection for setting up territories
        if(this.territory.getOwner()!=null){
            this.parent.runOnUiThread(new Runnable() {
                public void run() {
                    Toasty.info(parent.getBaseContext(), "This territory is already taken! Choose another territory.", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

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
        System.out.println("SETUP TERRITORY STATE: USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.DefaultState);
        game.getState().initState();
    }


    public void initState() {
        System.out.println("INIT SETUP TERR STATE:");

        game.activity.removeActiveBottomPaneFragment();
        game.getWorld().selectTerritory(this.territory);
        game.getWorld().unhighlightTerritories();
        game.cameraController.targetTerritory(this.territory);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_CANCEL_BUTTON);

        if(game.getWorld().allTerritoriesOccupied())
            game.setState(game.GainArmyUnitsState);

    }

}
