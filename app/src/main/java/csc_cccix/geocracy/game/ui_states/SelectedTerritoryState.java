package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.old_states.GameEvent;
import csc_cccix.geocracy.world.Territory;

public class SelectedTerritoryState extends IGameplayState {

    private final String TAG = "SELECTED_TERRITORY_STATE";

    private Territory selectedTerritory;

    public SelectedTerritoryState(IStateMachine SM, Territory territory) {
        super(SM);
        this.selectedTerritory = territory;
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {

        Log.i(TAG, "INIT STATE");

        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().selectTerritory(selectedTerritory);
        SM.Game.getCameraController().targetTerritory(selectedTerritory);

        SM.Game.getActivity().runOnUiThread(() -> {

            SM.Game.showBottomPaneFragment(TerritoryDetailFragment.newInstance(selectedTerritory));

            SM.Game.getActivity().hideAllGameInteractionButtons();
            SM.Game.getActivity().getEndTurnButton().show();
            SM.Game.getActivity().getCancelBtn().show();

            // If current player is the owner of selected territory
            if (selectedTerritory.getOwner().getId() == SM.Game.getCurrentPlayer().getId()) {

                // If the territory contains enough units to perform an attack
                if (selectedTerritory.getNArmies() >= 2) {
                    SM.Game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, true);
                } else {
                    SM.Game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, false);
                }

                // If the territory has adjacent friendly territories to fortify from
                if (selectedTerritory.getAdjacentFriendlyTerritories() != null) {
                    SM.Game.getActivity().setFortifyButtonVisibilityAndActiveState(true, true);
                } else {
                    SM.Game.getActivity().setFortifyButtonVisibilityAndActiveState(true, false);
                }

            }
        });

    }

    @Override
    public void DeinitializeState() {
        SM.Game.removeActiveBottomPaneFragment();
        SM.Game.getWorld().unhighlightTerritories();

        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.getActivity().hideAllGameInteractionButtons();
        });
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
                SM.Advance(new DefaultState(SM));

            case TERRITORY_SELECTED:
                Log.d(TAG, "TERRITORY SELECTED");
                if (((Territory) event.payload) != null) SM.Advance(new SelectedTerritoryState(SM, (Territory) event.payload));

                break;

            case ATTACK_TAPPED:
                Log.d(TAG, "ATTACK TAPPED -> PROCEED TO INTENT TO ATTACK STATE");
                if (selectedTerritory != null) {
                    SM.Advance(new IntentToAttackState(SM, selectedTerritory));
                }

            default:
                Log.d(TAG, "UNREGISTERED ACTION TRIGGERED (DEFAULT)");
                break;
        }

        return false;
    }
}
