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
        Log.i(TAG, "TERRITORY SELECTED");
        this.territory = territory;

        //illegal territory selection for setting up territories
        if(this.territory.getOwner()!=null){
            if(game.getGameData().players[game.getGameData().currentPlayer] instanceof HumanPlayer) {
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
        Log.i(TAG, "CANNOT SELECT TARGET TERRITORY");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails){
        Log.i(TAG, "INVALID ACTION: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails){
        Log.i(TAG, "INVALID ACTION: CANNOT PERFORM BATTLE COMPLETED");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "ADDING TERRITORY TO PLAYERS INITIAL TERRITORIES");
        Player currentPlayer = game.getGameData().players[game.getGameData().currentPlayer];
        territory.setOwner(currentPlayer);
        territory.setNArmies(amount);
        currentPlayer.addOrRemoveNArmies(1);

        Log.i(TAG, game.getGameData().players[game.getGameData().currentPlayer].getName() + " ADDED " + territory.getTerritoryName());

        game.getGameData().currentPlayer++;
        if(game.getGameData().currentPlayer==game.getGameData().players.length)
            game.getGameData().currentPlayer=0;

        // If all territories occupied, exit state
        if(game.getWorld().allTerritoriesOccupied()) {
            game.getGameData().currentPlayer = 0; // HUMAN PLAYER
            currentPlayer.setArmyPool(currentPlayer.getBonus()); // WILL NEED TO MOVE
            game.setState(new GainArmyUnitsState(game, parent));
            game.getState().initState();
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
            game.getActivity().removeActiveBottomPaneFragment();
            game.getWorld().selectTerritory(this.territory);
            game.getWorld().unhighlightTerritories();
            game.getCameraController().targetTerritory(this.territory);
        } else {
            this.parent.runOnUiThread(() -> {
                Toasty.info(parent.getBaseContext(), "Please select a territory to acquire!.", Toast.LENGTH_LONG).show();
            });
        }

        String tag = "UI_EVENT";

        EventBus.publish(tag, UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish(tag, UIEvent.HIDE_ATTACK_MODE_BUTTON);
        EventBus.publish(tag, UIEvent.HIDE_CANCEL_BUTTON);
        EventBus.publish(tag, UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);

    }

}
