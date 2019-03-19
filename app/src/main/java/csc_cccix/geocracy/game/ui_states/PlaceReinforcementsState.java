package csc_cccix.geocracy.game.ui_states;

/*import android.util.Log;

import csc_cccix.geocracy.fragments.DistributeTroopsDetailFragment;
import csc_cccix.geocracy.backend.HumanPlayer;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.backend.Player;
import csc_cccix.geocracy.backend.world.Territory;

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
        Player currentPlayer = SM.game.getGameData().getCurrentPlayer();

        // TODO: rename get game status (or make enum)
        //  Out of game Setup
        if(SM.game.getGameData().getGameStatus())
            currentPlayer.addOrRemoveNArmiesToPool(currentPlayer.getBonus());
        else {
            for(Player player : SM.game.getGameData().getPlayers())
                player.addOrRemoveNArmiesToPool((int)Math.floor(3.0 * (float)SM.game.getWorld().getNTerritories() / (float)SM.game.getGameData().getPlayers().length));
        }

        Log.i(TAG, "" + currentPlayer.getArmyPool());

        SM.game.getWorld().unhighlightTerritories();
        SM.game.getWorld().unselectTerritory();
        SM.game.getWorld().highlightTerritories(currentPlayer.getOwnedTerritories());
        SM.game.UI.hideAllGameInteractionButtons();

        if (currentPlayer.getClass() == HumanPlayer.class) {
            SM.game.getActivity().runOnUiThread(() -> {
                SM.game.UI.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(null, currentPlayer));
                SM.game.UI.setUpdateUnitCountButtonsVisibility(false, false);
            });

        }

        Log.i(TAG, "" + currentPlayer.getId());
        Log.i(TAG, "HAS " + currentPlayer.getArmyPool() + " UNITS TO DISTRIBUTE");

    }

    @Override
    public void DeinitializeState() {
        Log.d(TAG, "DEINIT STATE");

        SM.game.setFirstPlayer();

        SM.game.UI.removeActiveBottomPaneFragment();
        SM.game.getActivity().runOnUiThread(() -> SM.game.UI.hideAllGameInteractionButtons());
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case TERRITORY_SELECTED:

                if (event.payload != null) {
                    selectedTerritory = (Territory) event.payload;

                    // Show changes only for human player
                    if (SM.game.getGameData().getCurrentPlayer().getClass() == HumanPlayer.class) {

                        SM.game.getWorld().selectTerritory(selectedTerritory);
                        SM.game.getWorld().targetTerritory(selectedTerritory);
                        SM.game.getCameraController().targetTerritory(selectedTerritory);

                        // If current player owns the selected territory
                        if (selectedTerritory.getOwner() == SM.game.getGameData().getCurrentPlayer()){
                            SM.game.getActivity().runOnUiThread(() -> SM.game.UI.setUpdateUnitCountButtonsVisibility(true, true));
                            SM.game.UI.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(selectedTerritory, SM.game.getGameData().getCurrentPlayer()));
                        } else {
                            SM.game.getActivity().runOnUiThread(() -> {
                                SM.game.UI.hideAllGameInteractionButtons();
                                SM.game.Notifications.showCannotAssignUnitsToAnothersTerritoryNotification();
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
                if (SM.game.getGameData().getCurrentPlayer().getArmyPool() <= 0) {
                    SM.game.UI.removeActiveBottomPaneFragment();

                    // Loop through next players
                    SM.game.nextPlayer();
                    SM.game.UI.hideAllGameInteractionButtons();

                    // If all players have placed reinforcements (is human players turn again)
                    if(SM.game.getGameData().getCurrentPlayer().getClass() == HumanPlayer.class) {
                        SM.Advance(new DefaultState(SM));
                    }
                }

                break;

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
                SM.game.getWorld().unselectTerritory();
                selectedTerritory = null;
                break;

        }

        return false;
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Player currentPlayer = SM.game.getGameData().getCurrentPlayer();

        if (selectedTerritory != null && selectedTerritory.getOwner() == currentPlayer) {

            if (currentPlayer.getArmyPool() - amount < 0) {
                if (currentPlayer.getClass() == HumanPlayer.class) {
                    SM.game.Notifications.showInsufficentUnitPoolNotification();
                }
                return;
            }
            else if (amount < 0 && selectedTerritory.getNArmies() <= 1) {
                if (currentPlayer.getClass() == HumanPlayer.class) {
                    SM.game.Notifications.showInsufficentTerritoryUnitsNotification();
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
            if (SM.game.getGameData().getCurrentPlayer().getArmyPool() <= 0) {
                SM.game.getActivity().runOnUiThread(() -> SM.game.UI.setConfirmButtonVisibilityAndActiveState(true, true));
            } else {
                SM.game.getActivity().runOnUiThread(() -> SM.game.UI.setConfirmButtonVisibilityAndActiveState(true, false));
            }

            SM.game.UI.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(selectedTerritory, currentPlayer));
        }

    }

}*/
