package csc_cccix.geocracy.game.ui_states;

import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;

import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

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

        if (SM.Game.getCurrentPlayer() instanceof HumanPlayer) {
            SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "Please select a territory to acquire!", Toast.LENGTH_LONG).show());
        }

    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");
        SM.Game.setFirstPlayer();
        SM.Game.UI.removeActiveBottomPaneFragment();
        SM.Game.getActivity().runOnUiThread(() ->  SM.Game.UI.getConfirmButton().hide());
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case CONFIRM_TAPPED:

                if (selectedTerritory != null) {

                    //illegal territory selection for setting up territories
                    if(selectedTerritory.getOwner() != null){

                        if(SM.Game.getCurrentPlayer() instanceof HumanPlayer) {
                            SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "This territory is already taken! Choose another territory.", Toast.LENGTH_LONG).show());
                        }

                    } else {
                        addToSelectedTerritoryUnitCount(1);
                    }

                }

                break;

            case TERRITORY_SELECTED:

                if (event.payload != null) {
                    selectedTerritory = (Territory) event.payload;

                    SM.Game.getWorld().selectTerritory(selectedTerritory);
                    SM.Game.getWorld().unhighlightTerritories();
                    SM.Game.getCameraController().targetTerritory(selectedTerritory);
                    if (selectedTerritory.getOwner() == null && SM.Game.getCurrentPlayer() instanceof HumanPlayer) {
                        SM.Game.getActivity().runOnUiThread(() -> SM.Game.UI.getConfirmButton().show());
                    } else {
                        SM.Game.getActivity().runOnUiThread(() -> SM.Game.UI.getConfirmButton().hide());
                    }
                    SM.Game.getActivity().runOnUiThread(() -> {
                        SM.Game.UI.showBottomPaneFragment(TerritoryDetailFragment.newInstance(selectedTerritory));
                    });

                }

                break;

            case CANCEL_TAPPED:

                selectedTerritory = null;
                SM.Game.getWorld().unselectTerritory();
                SM.Game.getActivity().runOnUiThread(() -> {
                    SM.Game.UI.removeActiveBottomPaneFragment();
                    SM.Game.UI.getConfirmButton().hide();
                });

                highlightUnoccupiedTerritories();

                break;

        }

        return false;
    }

    public void addToSelectedTerritoryUnitCount(int amount) {

        Log.i(TAG, "ADDING TERRITORY TO PLAYERS INITIAL TERRITORIES");
        Player currentPlayer = SM.Game.getCurrentPlayer();
        selectedTerritory.setOwner(currentPlayer);
        selectedTerritory.setNArmies(amount);
        currentPlayer.addOrRemoveNArmies(1);

        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.UI.showBottomPaneFragment(TerritoryDetailFragment.newInstance(selectedTerritory));
        });

        Log.i(TAG, currentPlayer.getName() + " ADDED " + selectedTerritory.getTerritoryName());

        SM.Game.nextPlayer();

        // If all territories occupied, exit state
        if(SM.Game.getWorld().allTerritoriesOccupied()) {
            SM.Advance(new PlaceReinforcementsState(SM));

        } else {

            if (SM.Game.getCurrentPlayer() instanceof HumanPlayer) {
                SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "Please select a territory to acquire!", Toast.LENGTH_LONG).show());
            }

            SM.Game.getWorld().unhighlightTerritories();
            SM.Game.getWorld().highlightTerritories(new HashSet<>(SM.Game.getWorld().getUnoccupiedTerritories()));
        }

    }

    private void highlightUnoccupiedTerritories() {
        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().highlightTerritories(new HashSet<>(SM.Game.getWorld().getUnoccupiedTerritories()));
    }


}
