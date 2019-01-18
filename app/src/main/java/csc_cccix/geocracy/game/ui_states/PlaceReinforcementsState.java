package csc_cccix.geocracy.game.ui_states;

import android.util.Log;
import android.widget.Toast;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.fragments.DistributeTroopsDetailFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.old_states.GameEvent;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

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
        Player currentPlayer = SM.Game.getCurrentPlayer();


        // TODO: rename get game status (or make enum)
        //  Out of Game Setup
        if(SM.Game.getGameStatus())
            currentPlayer.addOrRemoveNArmiesToPool(currentPlayer.getBonus());
        else {
            for(Player player : SM.Game.getPlayers())
                player.addOrRemoveNArmiesToPool((int)Math.floor(3.0 * (float)SM.Game.getWorld().getNTerritories() / (float)SM.Game.getPlayers().length));
        }

        Log.i(TAG, "" + currentPlayer.getArmyPool());

        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().unselectTerritory();
        SM.Game.getWorld().highlightTerritories(currentPlayer.getTerritories());
        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.getActivity().hideAllGameInteractionButtons();
            SM.Game.getActivity().removeActiveBottomPaneFragment();
            SM.Game.getActivity().showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(null, currentPlayer));
            SM.Game.getActivity().setUpdateUnitCountButtonsVisibility(false);
            SM.Game.getActivity().setConfirmButtonVisibilityAndActiveState(true, false);
        });

        Log.i(TAG, "" + currentPlayer.getId());
        Log.i(TAG, "HAS " + currentPlayer.getArmyPool() + " UNITS TO DISTRIBUTE");

    }

    @Override
    public void DeinitializeState() {
        Log.d(TAG, "DEINIT STATE");
        SM.Game.removeActiveBottomPaneFragment();

    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case TERRITORY_SELECTED:

                if (event.payload != null) {
                    selectedTerritory = (Territory) event.payload;
                    SM.Game.getWorld().selectTerritory(selectedTerritory);
                    SM.Game.getWorld().targetTerritory(selectedTerritory);
                    SM.Game.getCameraController().targetTerritory(selectedTerritory);

                    // If current player owns the selected territory
                    if (selectedTerritory.getOwner() == SM.Game.getCurrentPlayer()){
                        SM.Game.getCameraController().targetTerritory(selectedTerritory);
                        SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().setUpdateUnitCountButtonsVisibility(true));
                        SM.Game.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(selectedTerritory, SM.Game.getCurrentPlayer()));
                    } else {
                        SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "Cannot assign units to another players territory!.", Toast.LENGTH_LONG).show());
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
                SM.Advance(new DefaultState(SM));
                break;

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
                SM.Game.getWorld().unselectTerritory();
                selectedTerritory = null;
//                SM.Advance(new DefaultState(SM));
                break;

        }

        return false;
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Player currentPlayer = SM.Game.getCurrentPlayer();

        if (selectedTerritory != null) {

            if (currentPlayer.getArmyPool() - amount < 0) {
                SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "You don't have enough units to add to this territory.", Toast.LENGTH_LONG).show());
                return;

            } else if (amount < 0 && selectedTerritory.getNArmies() <= 1) {
                SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "Cannot remove units from territory.", Toast.LENGTH_LONG).show());
                return;
            }
            else {
                Log.i(TAG, "GAIN ARMIES STATE: UPDATING UNITS IN TERRITORY BY " + amount);
                int clampedNArmies = Util.clamp(selectedTerritory.getNArmies() + amount, 1, Game.MAX_ARMIES_PER_TERRITORY);
                selectedTerritory.setNArmies(clampedNArmies);
                currentPlayer.addOrRemoveNArmiesToPool(-amount);
                Log.i(TAG, "PLAYER" + currentPlayer + " UPDATED UNITS AT " + selectedTerritory.getTerritoryName());
            }

        } else {
            Log.i(TAG, "CANNOT UPDATE UNIT COUNT, NO TERRITORY SELECTED");
        }

        if (SM.Game.getCurrentPlayer().getArmyPool() == 0) {
            SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().setConfirmButtonVisibilityAndActiveState(true, true));
        } else {
            SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().setConfirmButtonVisibilityAndActiveState(true, false));
        }

        SM.Game.getActivity().removeActiveBottomPaneFragment();
        SM.Game.getActivity().showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(selectedTerritory, currentPlayer));

    }

}
