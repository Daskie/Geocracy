package csc_cccix.geocracy.game.ui_states;

/*import android.util.Log;

import java.util.HashSet;

import androidx.lifecycle.ViewModelProviders;
import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.backend.Player;
import csc_cccix.geocracy.game.view_models.TerritoryDetailViewModel;
import csc_cccix.geocracy.backend.world.Territory;

public class DistributeTerritoriesState extends IGameplayState {

    private final String TAG = "DISTRIBUTE_TERRITORIES_STATE";

    Territory selectedTerritory = null;

    public DistributeTerritoriesState(IStateMachine SM) {
        super(SM);
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.i(TAG, "INIT STATE");
        highlightUnoccupiedTerritories();

        if (SM.game.currentPlayerIsHuman()) {
            SM.game.Notifications.showSelectTerritoryToAcquireNotification();
        }

    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");
        SM.game.setFirstPlayer();
        SM.game.UI.removeActiveBottomPaneFragment();
        SM.game.getActivity().runOnUiThread(() ->  SM.game.UI.getConfirmButton().hide());
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case CONFIRM_TAPPED:

                if (selectedTerritory != null) {

                    //illegal territory selection for setting up territories
                    if (selectedTerritory.getOwner() != null){

                        if (SM.game.currentPlayerIsHuman()) {
                            SM.game.Notifications.showTerritoryAlreadyAcquiredNotification();
                        }

                    } else {
                        addToSelectedTerritoryUnitCount(1);
                    }

                }

                break;

            case TERRITORY_SELECTED:

                if (event.payload != null) {
                    selectedTerritory = (Territory) event.payload;
                    ViewModelProviders.of(SM.game.getActivity()).get(TerritoryDetailViewModel.class).setSelectedTerritory(selectedTerritory);

                    SM.game.getWorld().selectTerritory(selectedTerritory);
                    SM.game.getWorld().unhighlightTerritories();
                    SM.game.getCameraController().targetTerritory(selectedTerritory);
                    if (selectedTerritory.getOwner() == null && SM.game.currentPlayerIsHuman()) {
                        SM.game.getActivity().runOnUiThread(() -> SM.game.UI.getConfirmButton().show());
                    } else {
                        SM.game.getActivity().runOnUiThread(() -> SM.game.UI.getConfirmButton().hide());
                    }

                    SM.game.getActivity().runOnUiThread(() -> {
                        SM.game.UI.showBottomPaneFragment(TerritoryDetailFragment.newInstance());
                    });

                }

                break;

            case CANCEL_TAPPED:

                selectedTerritory = null;
                SM.game.getWorld().unselectTerritory();
                SM.game.getActivity().runOnUiThread(() -> {
                    SM.game.UI.removeActiveBottomPaneFragment();
                    SM.game.UI.getConfirmButton().hide();
                });

                highlightUnoccupiedTerritories();

                break;

        }

        return false;
    }

    public void addToSelectedTerritoryUnitCount(int amount) {

        Log.i(TAG, "ADDING TERRITORY TO PLAYERS INITIAL TERRITORIES");
        Player currentPlayer = SM.game.getGameData().getCurrentPlayer();
        selectedTerritory.setOwner(currentPlayer);
        selectedTerritory.setNArmies(amount);
        currentPlayer.addOrRemoveNArmies(1);

        SM.game.getActivity().runOnUiThread(() -> {
            SM.game.UI.showBottomPaneFragment(TerritoryDetailFragment.newInstance());
        });

        Log.i(TAG, currentPlayer.getName() + " ADDED " + selectedTerritory.getTerritoryName());

        SM.game.nextPlayer();

        // If all territories occupied, exit state
        if(SM.game.getWorld().allTerritoriesOccupied()) {
            SM.Advance(new PlaceReinforcementsState(SM));

        } else {

            if (SM.game.currentPlayerIsHuman()) {
                SM.game.Notifications.showSelectTerritoryToAcquireNotification();
            }

            SM.game.getWorld().unhighlightTerritories();
            SM.game.getWorld().highlightTerritories(new HashSet<>(SM.game.getWorld().getUnoccupiedTerritories()));
        }

    }

    private void highlightUnoccupiedTerritories() {
        SM.game.getWorld().unhighlightTerritories();
        SM.game.getWorld().highlightTerritories(new HashSet<>(SM.game.getWorld().getUnoccupiedTerritories()));
    }


}*/
