package csc_cccix.geocracy.states;

import android.util.Log;
import android.widget.Toast;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.GameActivity;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class SetUpInitTerritoriesState implements GameState {

    private static final String TAG = "INIT_TERRITORIES_STATE";

    private Game game;
    private Territory territory;
    private GameActivity parent;


    public SetUpInitTerritoriesState(Game game, GameActivity parent) {
        this.game = game;
        this.parent = parent;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "SETUP INITIAL TERRITORIES STATE: ANOTHER TERRITORY SELECTED");
        this.territory = territory;

        //illegal territory selection for setting up territories
        if(this.territory.getOwner()!=null){
            if(game.gameData.players[game.gameData.currentPlayer] instanceof HumanPlayer) {
                this.parent.runOnUiThread(new Runnable() {
                    public void run() {
                        Toasty.info(parent.getBaseContext(), "This territory is already taken! Choose another territory.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return;
        }

        addToSelectedTerritoryUnitCount(1);

    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "SETUP INITIAL TERRITORIES STATE: CANNOT SELECT TARGET TERRITORY");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails){
        Log.i(TAG, "INVALID ACTION: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails){
        Log.i(TAG, "INVALID ACTION: CANNOT PERFORM BATTLE COMPLETED");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "ADDING TERRITORY TO PLAYERS INITIAL TERRITORIES");
        territory.setOwner(game.gameData.players[game.gameData.currentPlayer]);
        territory.setNArmies(amount);

        Log.i("", game.gameData.players[game.gameData.currentPlayer].getName() + " ADDED " + territory.getTerritoryName());

        game.gameData.currentPlayer++;
        if(game.gameData.currentPlayer==game.gameData.players.length)
            game.gameData.currentPlayer=0;

        if(game.getWorld().allTerritoriesOccupied()) {
            game.gameData.currentPlayer = 0; // HUMAN PLAYER
            Player currentPlayer = game.gameData.players[game.gameData.currentPlayer];
            currentPlayer.setArmyPool(currentPlayer.getBonus()); // WILL NEED TO MOVE
            game.setState(game.gainArmyUnitsState);
        }

    }

    public void enableAttackMode() {
        Log.i(TAG, "CANNOT ENABLE ATTACK MODE");
    }

    public void confirmAction() {
        Log.i(TAG, "USER CANCELED ACTION -> N/A");
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        this.territory = null;
    }


    public void initState() {
        Log.i(TAG, "INIT STATE");

        if (this.territory != null) {
            game.activity.removeActiveBottomPaneFragment();
            game.getWorld().selectTerritory(this.territory);
            game.getWorld().unhighlightTerritories();
            game.cameraController.targetTerritory(this.territory);
        }

        String tag = "UI_EVENT";

        EventBus.publish(tag, UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish(tag, UIEvent.HIDE_ATTACK_MODE_BUTTON);
        EventBus.publish(tag, UIEvent.HIDE_CANCEL_BUTTON);
        EventBus.publish(tag, UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);

    }

}