package csc_cccix.geocracy.game.ui_states;

/*import android.util.Log;

import csc_cccix.geocracy.fragments.FortifyTerritoryFragment;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.backend.world.Territory;

public class FortifyTerritoryState extends IGameplayState {

    private final String TAG = "FORTIFY_TERRITORY_STATE";

    private Territory originTerritory;
    private Territory destinationTerritory;

    public FortifyTerritoryState(IStateMachine SM, Territory originTerritory) {
        super(SM);
        this.originTerritory = originTerritory;
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {

        Log.i(TAG, "INIT STATE");

        SM.game.getWorld().unhighlightTerritories();
        SM.game.getWorld().selectTerritory(originTerritory);
        SM.game.getWorld().highlightTerritory(originTerritory);

        if (originTerritory.getAdjacentFriendlyTerritories() != null) {
            SM.game.getWorld().highlightTerritories(originTerritory.getAdjacentFriendlyTerritories());
        }

        SM.game.getActivity().runOnUiThread(() -> {
            SM.gameUI.hideAllGameInteractionButtons();

            if (SM.game.currentPlayerIsHuman()) {
                SM.gameUI.getCancelBtn().show();
            }
        });

    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");

        SM.gameUI.removeActiveBottomPaneFragment();
        SM.game.getActivity().runOnUiThread(() -> {
            SM.gameUI.hideAllGameInteractionButtons();
        });
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case TERRITORY_SELECTED:

                if (event.payload != null) {
                    destinationTerritory = (Territory) event.payload;

                    // If current player owns the selected territory
                    if (destinationTerritory.getOwner() == SM.game.getGameData().getCurrentPlayer()){
                        SM.game.getWorld().selectTerritory(originTerritory);
                        SM.game.getWorld().targetTerritory(destinationTerritory);
                        SM.game.getCameraController().targetTerritory(destinationTerritory);
                        SM.game.getActivity().runOnUiThread(() -> SM.gameUI.setUpdateUnitCountButtonsVisibility(true, true));
                        SM.gameUI.showBottomPaneFragment(FortifyTerritoryFragment.newInstance(originTerritory, destinationTerritory));
                    } else {
                        SM.game.Notifications.showCannotAssignUnitsToAnothersTerritoryNotification();
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
                SM.game.nextPlayer();
                SM.Advance(new DefaultState(SM));
                break;

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
                SM.Advance(new DefaultState(SM));
                break;

        }

        return false;
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "UPDATING UNIT COUNT BY: " + amount);
        if (amount == 0) return;
        else if (amount > 0) {
            amount = Math.abs(amount);
            if (originTerritory.getNArmies() - amount > 0) {
                originTerritory.setNArmies(originTerritory.getNArmies() - amount);
                destinationTerritory.setNArmies(destinationTerritory.getNArmies() + amount);
            }
        }
        else if (amount < 0) {
            amount = Math.abs(amount);
            if (destinationTerritory.getNArmies() - amount > 0) {
                originTerritory.setNArmies(originTerritory.getNArmies() + amount);
                destinationTerritory.setNArmies(destinationTerritory.getNArmies() - amount);
            }
        }

        if (SM.game.currentPlayerIsHuman()) {
            SM.game.getActivity().runOnUiThread(() -> {
                SM.gameUI.getConfirmButton().show();
                SM.gameUI.getCancelBtn().hide();
            });
        }
    }
}*/
