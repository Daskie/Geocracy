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

    private static final String TAG = "GAIN_ARMIES_STATE";

    private Game game;

    private Territory territory;

    private GameActivity parent;

    private static final int max_units = 10; // whats the max?
    private static final int min_units = 1; // whats the max?


    public GainArmyUnitsState(Game game, GameActivity parent) {
        this.game = game;
        this.parent = parent;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "CANNOT SELECT ORIGIN TERRITORY (USE TARGET)");
    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "SELECTING TARGET TERRITORY TO ADD/REMOVE UNITS");

        if (territory != null) {
            //illegal territory selection for assigning units
            if(territory.getOwner() != game.getGameData().players[game.getGameData().currentPlayer]){
                this.parent.runOnUiThread(() -> {
                    Toasty.info(parent.getBaseContext(), "Cannot assign units to another players territory!.", Toast.LENGTH_LONG).show();
                });
                return;
            }

            this.territory = territory;

            game.getCameraController().targetTerritory(territory);
            EventBus.publish("UI_EVENT", UIEvent.SHOW_UPDATE_UNITS_MODE_BUTTONS);
            Player currentPlayer = game.getGameData().players[game.getGameData().currentPlayer];
            parent.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(this.territory, currentPlayer));
        }
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails){
        Log.i(TAG, "GAIN ARMY UNITS STATE: CANNOT PERFORM DICE ROLL");

    }
    public void battleCompleted(BattleResultDetails battleResultDetails){
        Log.i(TAG, "GAIN ARMY UNITS STATE: CANNOT BATTLE");
    }

    public void enableAttackMode() {
        Log.i(TAG, "CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Player currentPlayer = game.getGameData().players[game.getGameData().currentPlayer];

        if (this.territory != null) {

            if(currentPlayer.getArmyPool()-amount<0) {
                this.parent.runOnUiThread(() -> {
                    Toasty.info(parent.getBaseContext(), "You don't have enough units to add to this territory.", Toast.LENGTH_LONG).show();
                });
                return;
            }
            else if (amount<0 && this.territory.getNArmies() <= 1) {
                this.parent.runOnUiThread(() -> {
                    Toasty.info(parent.getBaseContext(), "Cannot remove units from territory.", Toast.LENGTH_LONG).show();
                });
                return;
            }
            else {
                Log.i("", "GAIN ARMIES STATE: UPDATING UNITS IN TERRITORY BY " + amount);
                int clampedNArmies = Util.clamp(territory.getNArmies() + amount, min_units, max_units);
                this.territory.setNArmies(clampedNArmies);
                currentPlayer.addOrRemoveNArmiesToPool(-amount);
                Log.i(TAG, "PLAYER" + game.getGameData().currentPlayer + " UPDATED UNITS AT " + territory.getTerritoryName());
            }


        } else {
            Log.i(TAG, "CANNOT UPDATE UNIT COUNT, NO TERRITORY SELECTED");
        }

        game.getActivity().removeActiveBottomPaneFragment();
        parent.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(this.territory, currentPlayer));

    }

    public void confirmAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE FOR PLAYER");
        game.setState(new DefaultState(game));
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> DESELECT TERRITORY IF SELECTED");
        this.territory = null;
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> N/A"); }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.getActivity().removeActiveBottomPaneFragment();
        Player currentPlayer = game.getGameData().players[game.getGameData().currentPlayer];
        currentPlayer.addOrRemoveNArmiesToPool(currentPlayer.getBonus());
        game.getWorld().unhighlightTerritories();
        game.getWorld().unselectTerritory();
        game.getWorld().highlightTerritories(currentPlayer.getTerritories());
        parent.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(null, currentPlayer));
        Log.i(TAG, "" + currentPlayer.getId());
        Log.i(TAG, "HAS " + currentPlayer.getArmyPool() + " UNITS TO DISTRIBUTE");
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }
}
