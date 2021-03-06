package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.DistributeTroopsDetailFragment;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;

public class PlaceReinforcementsState extends IGameplayState {

    private final String TAG = "PLACE_REINFORCEMENTS_STATE";

    private Territory selectedTerritory = null;

    public PlaceReinforcementsState(IStateMachine SM) {
        super(SM);
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.d(TAG, "INIT STATE");
        Player currentPlayer = SM.Game.getGameData().getCurrentPlayer();

        // TODO: rename get game status (or make enum)
        //  Out of Game Setup
        if(SM.Game.getGameData().getGameStatus())
            currentPlayer.addOrRemoveNArmiesToPool(currentPlayer.getBonus());
        else {
            for(Player player : SM.Game.getGameData().getPlayers())
                player.addOrRemoveNArmiesToPool((int)Math.floor(3.0 * (float)SM.Game.getWorld().getNTerritories() / (float)SM.Game.getGameData().getPlayers().length));
        }

        Log.i(TAG, "" + currentPlayer.getArmyPool());

        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().unselectTerritory();
        SM.Game.getWorld().highlightTerritories(currentPlayer.getOwnedTerritories());
        SM.Game.UI.hideAllGameInteractionButtons();

        if (currentPlayer.getClass() == HumanPlayer.class) {
            SM.Game.getActivity().runOnUiThread(() -> {
                SM.Game.UI.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(null, currentPlayer));
                SM.Game.UI.setUpdateUnitCountButtonsVisibility(false, false);
            });

        }

        Log.i(TAG, "" + currentPlayer.getId());
        Log.i(TAG, "HAS " + currentPlayer.getArmyPool() + " UNITS TO DISTRIBUTE");

    }

    @Override
    public void DeinitializeState() {
        Log.d(TAG, "DEINIT STATE");

        SM.Game.setFirstPlayer();

        SM.Game.UI.removeActiveBottomPaneFragment();
        SM.Game.getActivity().runOnUiThread(() -> SM.Game.UI.hideAllGameInteractionButtons());
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case TERRITORY_SELECTED:

                if (event.payload != null) {
                    selectedTerritory = (Territory) event.payload;

                    // Show changes only for human player
                    if (SM.Game.getGameData().getCurrentPlayer().getClass() == HumanPlayer.class) {

                        SM.Game.getWorld().selectTerritory(selectedTerritory);
                        SM.Game.getWorld().targetTerritory(selectedTerritory);
                        SM.Game.getCameraController().targetTerritory(selectedTerritory);

                        // If current player owns the selected territory
                        if (selectedTerritory.getOwner() == SM.Game.getGameData().getCurrentPlayer()){
                            SM.Game.getActivity().runOnUiThread(() -> SM.Game.UI.setUpdateUnitCountButtonsVisibility(true, true));
                            SM.Game.UI.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(selectedTerritory, SM.Game.getGameData().getCurrentPlayer()));
                        } else {
                            SM.Game.getActivity().runOnUiThread(() -> {
                                SM.Game.UI.hideAllGameInteractionButtons();
                                SM.Game.Notifications.showCannotAssignUnitsToAnothersTerritoryNotification();
                            });
                        }

                    }

                }

                break;

            case ADD_UNIT_TAPPED:
                addToSelectedTerritoryUnitCount(1);
                break;

            case REMOVE_UNIT_TAPPED:
                addToSelectedTerritoryUnitCount(-1);
                break;

            case CONFIRM_TAPPED:

                // Verify player has placed all their reinforcements
                if (SM.Game.getGameData().getCurrentPlayer().getArmyPool() <= 0) {
                    SM.Game.UI.removeActiveBottomPaneFragment();

                    // Loop through next players
                    SM.Game.nextPlayer();
                    SM.Game.UI.hideAllGameInteractionButtons();

                    // If all players have placed reinforcements (is human players turn again)
                    if(SM.Game.getGameData().getCurrentPlayer().getClass() == HumanPlayer.class) {
                        SM.Advance(new DefaultState(SM));
                    }
                }

                break;

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
                SM.Game.getWorld().unselectTerritory();
                selectedTerritory = null;
                break;

        }

        return false;
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Player currentPlayer = SM.Game.getGameData().getCurrentPlayer();

        if (selectedTerritory != null && selectedTerritory.getOwner() == currentPlayer) {

            if (currentPlayer.getArmyPool() - amount < 0) {
                if (currentPlayer.getClass() == HumanPlayer.class) {
                    SM.Game.Notifications.showInsufficentUnitPoolNotification();
                }
                return;
            }
            else if (amount < 0 && selectedTerritory.getNArmies() <= 1) {
                if (currentPlayer.getClass() == HumanPlayer.class) {
                    SM.Game.Notifications.showInsufficentTerritoryUnitsNotification();
                }
                return;
            }
            else {
                Log.d(TAG, "GAIN ARMIES STATE: UPDATING UNITS IN TERRITORY BY " + amount);
                if (currentPlayer.placeUnitsInOwnedTerritory(selectedTerritory, amount)) {
                    Log.d(TAG, "PLAYER " + currentPlayer.getId() + " UPDATED UNITS AT " + selectedTerritory.getTerritoryName());
                }
            }

        } else {
            Log.d(TAG, "CANNOT UPDATE UNIT COUNT, NO TERRITORY SELECTED OR CURRENT PLAYER IS NOT THE TERRITORIES OWNER!");
        }

        if (currentPlayer.getClass() == HumanPlayer.class) {
            if (SM.Game.getGameData().getCurrentPlayer().getArmyPool() <= 0) {
                SM.Game.getActivity().runOnUiThread(() -> SM.Game.UI.setConfirmButtonVisibilityAndActiveState(true, true));
            } else {
                SM.Game.getActivity().runOnUiThread(() -> SM.Game.UI.setConfirmButtonVisibilityAndActiveState(true, false));
            }

            SM.Game.UI.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(selectedTerritory, currentPlayer));
        }

    }

}
