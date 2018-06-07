package csc_cccix.geocracy.states;

import android.util.Log;
import android.widget.Toast;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.fragments.DistributeTroopsDetailFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class GainArmyUnitsState implements GameState {

    private static final String TAG = "GAIN_ARMIES_STATE";

    private Game game;

    private Territory territory;

    public GainArmyUnitsState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "CANNOT SELECT ORIGIN TERRITORY (USE TARGET)");
    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "SELECTING TARGET TERRITORY TO ADD/REMOVE UNITS");

        if (territory != null) {
            //illegal territory selection for assigning units
            if(territory.getOwner() != game.getCurrentPlayer()){
                game.getActivity().runOnUiThread(() -> Toasty.info(game.getActivity().getBaseContext(), "Cannot assign units to another players territory!.", Toast.LENGTH_LONG).show());
                return;
            }

            this.territory = territory;

            game.getCameraController().targetTerritory(territory);
            game.getActivity().runOnUiThread(() -> game.getActivity().setUpdateUnitCountButtonsVisibility(true));
            game.getActivity().showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(this.territory, game.getCurrentPlayer()));
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
        Player currentPlayer = game.getCurrentPlayer();

        if (this.territory != null) {

            if (currentPlayer.getArmyPool() - amount < 0) {
                game.getActivity().runOnUiThread(() -> Toasty.info(game.getActivity().getBaseContext(), "You don't have enough units to add to this territory.", Toast.LENGTH_LONG).show());
                return;
            }
            else if (amount < 0 && this.territory.getNArmies() <= 1) {
                game.getActivity().runOnUiThread(() -> Toasty.info(game.getActivity().getBaseContext(), "Cannot remove units from territory.", Toast.LENGTH_LONG).show());
                return;
            }
            else {
                Log.i("", "GAIN ARMIES STATE: UPDATING UNITS IN TERRITORY BY " + amount);
                int clampedNArmies = Util.clamp(territory.getNArmies() + amount, 1, Game.MAX_ARMIES_PER_TERRITORY);
                this.territory.setNArmies(clampedNArmies);
                currentPlayer.addOrRemoveNArmiesToPool(-amount);
                Log.i(TAG, "PLAYER" + currentPlayer + " UPDATED UNITS AT " + territory.getTerritoryName());
            }

        } else {
            Log.i(TAG, "CANNOT UPDATE UNIT COUNT, NO TERRITORY SELECTED");
        }

        if (game.getCurrentPlayer().getArmyPool() == 0) {
            game.getActivity().runOnUiThread(() -> {
                game.getActivity().setConfirmButtonButtonVisibilityAndActiveState(true, true);
            });
        } else {
            game.getActivity().runOnUiThread(() -> {
                game.getActivity().setConfirmButtonButtonVisibilityAndActiveState(true, false);
            });
        }

        game.getActivity().removeActiveBottomPaneFragment();
        game.getActivity().showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(this.territory, currentPlayer));

    }

    public void confirmAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE FOR PLAYER");
        game.setState(new DefaultState(game));
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> DESELECT TERRITORY IF SELECTED");
        this.territory = null;
        Player currentPlayer = game.getCurrentPlayer();
        game.getActivity().runOnUiThread(() -> {
            game.getActivity().showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(null, currentPlayer));
            game.getActivity().setUpdateUnitCountButtonsVisibility(false);
        });
    }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> N/A"); }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.addOrRemoveNArmiesToPool(currentPlayer.getBonus());

        game.getWorld().unhighlightTerritories();
        game.getWorld().unselectTerritory();
        game.getWorld().highlightTerritories(currentPlayer.getTerritories());
        game.getActivity().runOnUiThread(() -> {
            game.getActivity().removeActiveBottomPaneFragment();
            game.getActivity().showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(null, currentPlayer));
            game.getActivity().setUpdateUnitCountButtonsVisibility(false);
            game.getActivity().setConfirmButtonButtonVisibilityAndActiveState(true, false);
        });

        Log.i(TAG, "" + currentPlayer.getId());
        Log.i(TAG, "HAS " + currentPlayer.getArmyPool() + " UNITS TO DISTRIBUTE");
    }
}
