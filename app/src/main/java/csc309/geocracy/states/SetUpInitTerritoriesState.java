package csc309.geocracy.states;

import android.widget.Toast;

import csc309.geocracy.EventBus;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.GameActivity;
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

        addToSelectedTerritoryUnitCount(1);

    }

    public void selectTargetTerritory(Territory territory) {
        System.out.println("SETUP TERRITORY STATE: CANNOT SELECT TARGET TERRITORY");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails){

    }
    public void battleCompleted(BattleResultDetails battleResultDetails){

    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        System.out.println("SETUP TERRITORY STATE: ADDING TERRITORY TO PLAYERS INITIAL TERRITORIES");
        territory.setOwner(game.players[game.currentPlayer]);
        territory.setNArmies(amount);

        System.out.println("PLAYER" + game.currentPlayer + " ADDED " + territory.getTerritoryName());

        game.currentPlayer++;
        if(game.currentPlayer==game.players.length)
            game.currentPlayer=0;

        if(game.getWorld().allTerritoriesOccupied())
            game.setState(game.GainArmyUnitsState);

    }

    public void enableAttackMode() {
        System.out.println("SETUP TERRITORY STATE: CANNOT ENABLE ATTACK MODE");
    }

    public void cancelAction() {
        System.out.println("SETUP TERRITORY STATE: USER CANCELED ACTION -> ENTER DEFAULT STATE");
        this.territory = null;
    }


    public void initState() {
        System.out.println("INIT SETUP TERR STATE:");

        if (this.territory != null) {
            game.activity.removeActiveBottomPaneFragment();
            game.getWorld().selectTerritory(this.territory);
            game.getWorld().unhighlightTerritories();
            game.cameraController.targetTerritory(this.territory);
        }


        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_CANCEL_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);


    }

}