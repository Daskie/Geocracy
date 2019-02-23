package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import androidx.lifecycle.ViewModelProviders;
import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.game.view_models.TerritoryDetailViewModel;
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

        ViewModelProviders.of(SM.Game.getActivity()).get(TerritoryDetailViewModel.class).setSelectedTerritory(selectedTerritory);

        SM.Game.getActivity().runOnUiThread(() -> {

            SM.Game.UI.showBottomPaneFragment(TerritoryDetailFragment.newInstance());
            SM.Game.UI.hideAllGameInteractionButtons();

            if (SM.Game.getControllingPlayer() instanceof HumanPlayer) {
                SM.Game.UI.getEndTurnButton().show();
                SM.Game.UI.getCancelBtn().show();

                // If current player is the owner of selected territory
                if (selectedTerritory.getOwner().getId() == SM.Game.getGameData().getCurrentPlayer().getId()) {

                    // If the territory contains enough units to perform an attack
                    if (selectedTerritory.getNArmies() >= 2) {
                        SM.Game.UI.setAttackModeButtonVisibilityAndActiveState(true, true);

                        // If the territory has adjacent friendly territories to fortify from
                        if (selectedTerritory.getAdjacentFriendlyTerritories() != null) {
                            SM.Game.UI.setFortifyButtonVisibilityAndActiveState(true, true);
                        } else {
                            SM.Game.UI.setFortifyButtonVisibilityAndActiveState(true, false);
                        }

                    } else {
                        SM.Game.UI.setAttackModeButtonVisibilityAndActiveState(false, false);
                        SM.Game.UI.setFortifyButtonVisibilityAndActiveState(false, false);
                    }

                }
            }

        });

    }

    @Override
    public void DeinitializeState() {
        SM.Game.UI.removeActiveBottomPaneFragment();
        SM.Game.getWorld().unhighlightTerritories();

        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.UI.hideAllGameInteractionButtons();
        });
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
                SM.Advance(new DefaultState(SM));
                break;

            case TERRITORY_SELECTED:
                Log.d(TAG, "TERRITORY SELECTED");
                if (event.payload != null) SM.Advance(new SelectedTerritoryState(SM, (Territory) event.payload));
                break;

            case ATTACK_TAPPED:
                Log.d(TAG, "ATTACK TAPPED -> PROCEED TO INTENT TO ATTACK STATE");
                if (selectedTerritory != null) {
                    SM.Advance(new IntentToAttackState(SM, selectedTerritory));
                }
                break;

            case FORTIFY_TAPPED:
                Log.d(TAG, "FORTIFY TAPPED -> PROCEED TO FORTIFY TERRITORY STATE");
                if (selectedTerritory != null) {
                    SM.Advance(new FortifyTerritoryState(SM, selectedTerritory));
                }
                break;

            case END_TURN_TAPPED:
                Log.d(TAG, "PLAYER ENDED THEIR TURN");
                SM.Game.nextPlayer();
                SM.Advance(new DefaultState(SM));
                break;

            default:
                Log.d(TAG, "UNREGISTERED ACTION TRIGGERED (DEFAULT)");
                break;
        }

        return false;
    }
}
