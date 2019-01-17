package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.IState;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.states.GameEvent;
import csc_cccix.geocracy.world.Territory;

public class SelectedTerritoryState extends IState {

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
        SM.Game.showBottomPaneFragment(TerritoryDetailFragment.newInstance(this.selectedTerritory));
        SM.Game.getWorld().selectTerritory(this.selectedTerritory);
        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getCameraController().targetTerritory(this.selectedTerritory);

        // TODO: need to fix race condition causing crash (actually might be due to not assigning territories to players as of yet)

        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.getActivity().hideAllGameInteractionButtons();
            SM.Game.getActivity().getEndTurnButton().show();
            SM.Game.getActivity().getCancelBtn().show();

//            if (this.selectedTerritory.getOwner().getId() == SM.Game.getCurrentPlayer().getId()) {
//                if (this.selectedTerritory.getNArmies() >= 2) {
//                    SM.Game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, true);
//                    if (this.selectedTerritory.getAdjacentFriendlyTerritories() != null) {
//                        SM.Game.getActivity().setFortifyButtonVisibilityAndActiveState(true, true);
//                    } else {
//                        SM.Game.getActivity().setFortifyButtonVisibilityAndActiveState(true, false);
//                    }
//
//                } else {
//                    Log.i(TAG, "ENABLE ATTACK MODE: INVALID TERRITORY -> DISABLE ATTACK BUTTON");
//                    SM.Game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, false);
//                    SM.Game.getActivity().setFortifyButtonVisibilityAndActiveState(true, false);
//                }
//            }
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

        switch (event.action) {

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
                SM.Advance(new DefaultState(SM));

            case TERRITORY_SELECTED:
                Log.d(TAG, "TERRITORY SELECTED");
                if (((Territory) event.payload) != null) SM.Advance(new SelectedTerritoryState(SM, (Territory) event.payload));

                break;

            default:
                Log.d(TAG, "UNREGISTERED ACTION TRIGGERED (DEFAULT)");
                break;
        }

        return false;
    }
}
