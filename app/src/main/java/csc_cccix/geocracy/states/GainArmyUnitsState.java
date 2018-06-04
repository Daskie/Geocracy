package csc_cccix.geocracy.states;

import android.util.Log;
import android.widget.Toast;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.fragments.DistributeTroopsDetailFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.GameActivity;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class GainArmyUnitsState implements GameState {

    private Game game;

    private Territory territory;

    private static GameActivity parent;

    private int unitsToDistribute;
    private final int MAX_UNITS = 10; // whats the max?
    private final int MIN_UNITS = 1; // whats the max?


    public GainArmyUnitsState(Game game, GameActivity parent) {
        this.game = game;
        this.parent = parent;
    }

    public void selectOriginTerritory(Territory territory) {

    }

    public void selectTargetTerritory(Territory territory) {
        Log.i("", "GAIN ARMIES STATE: SELECTING TARGET TERRITORY TO ADD/REMOVE UNITS");

        if (territory != null) {
            //illegal territory selection for assigning units
            if(territory.getOwner() != game.players[game.currentPlayer]){
                this.parent.runOnUiThread(() -> {
                    Toasty.info(parent.getBaseContext(), "Cannot assign units to another players territory!.", Toast.LENGTH_LONG).show();
                });
                return;
            }

            this.territory = territory;

            game.cameraController.targetTerritory(territory);
            EventBus.publish("UI_EVENT", UIEvent.SHOW_UPDATE_UNITS_MODE_BUTTONS);
            parent.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(this.territory));
        }
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails){

    }
    public void battleCompleted(BattleResultDetails battleResultDetails){

    }

    public void enableAttackMode() {
        Log.i("", "GAIN ARMIES STATE: CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        if (this.territory != null) {
            Player currentPlayer = game.players[game.currentPlayer];

            currentPlayer.addOrRemoveNArmies(-amount);

            if(currentPlayer.getNArmies()<0) {
                this.parent.runOnUiThread(() -> {
                    Toasty.info(parent.getBaseContext(), "You have no more units to add to this territory.", Toast.LENGTH_LONG).show();
                });
                currentPlayer.addOrRemoveNArmies(amount);
                return;
            }

            Log.i("", "GAIN ARMIES STATE: UPDATING UNITS IN TERRITORY BY " + amount);
            int clampedNArmies = Util.clamp(territory.getNArmies() + amount, MIN_UNITS, MAX_UNITS);
            this.territory.setNArmies(clampedNArmies);
            Log.i("", "PLAYER" + game.currentPlayer + " UPDATED UNITS AT " + territory.getTerritoryName());
        } else {
            Log.i("", "GAIN ARMIES STATE: CANNOT UPDATE UNIT COUNT, NO TERRITORY SELECTED");
        }
    }

    public void confirmAction() {
        Log.i("", "SETUP INITIAL TERRITORIES STATE: USER CANCELED ACTION -> ENTER DEFAULT STATE FOR PLAYER");
        game.setState(game.defaultState);
    }

    public void cancelAction() {
        Log.i("", "GAIN ARMIES STATE: USER CANCELED ACTION -> DESELECT TERRITORY IF SELECTED");
        this.territory = null;
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }


    public void initState() {
        Log.i("", "INIT GAIN ARMIES STATE:");
        game.activity.removeActiveBottomPaneFragment();
        Player currentPlayer = game.players[game.currentPlayer];
        this.unitsToDistribute = currentPlayer.getBonus();
        game.getWorld().unhighlightTerritories();
        game.getWorld().unselectTerritory();
        game.getWorld().highlightTerritories(currentPlayer.getTerritories());
        Log.i("", "" + currentPlayer.getId());
        Log.i("", "HAS " + this.unitsToDistribute + " UNITS TO DISTRIBUTE");
        currentPlayer.addOrRemoveNArmies(this.unitsToDistribute);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }
}
