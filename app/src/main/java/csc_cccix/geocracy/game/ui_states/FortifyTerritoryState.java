package csc_cccix.geocracy.game.ui_states;

import android.util.Log;
import android.widget.Toast;

import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.old_states.GameEvent;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

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

        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().selectTerritory(this.originTerritory);
        SM.Game.getWorld().highlightTerritory(this.originTerritory);
        SM.Game.getWorld().highlightTerritories(this.originTerritory.getAdjacentFriendlyTerritories());

        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.getActivity().hideAllGameInteractionButtons();
            SM.Game.getActivity().getConfirmButton().show();
            SM.Game.getActivity().getCancelBtn().show();
        });

    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");

        SM.Game.removeActiveBottomPaneFragment();
        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.getActivity().hideAllGameInteractionButtons();
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
                    if (destinationTerritory.getOwner() == SM.Game.getCurrentPlayer()){
                        SM.Game.getWorld().selectTerritory(destinationTerritory);
                        SM.Game.getWorld().targetTerritory(destinationTerritory);
                        SM.Game.getCameraController().targetTerritory(destinationTerritory);
                        SM.Game.getCameraController().targetTerritory(destinationTerritory);
                        SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().setUpdateUnitCountButtonsVisibility(true));
//                        SM.Game.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(destinationTerritory, SM.Game.getCurrentPlayer()));
                    } else {
                        SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "Cannot move units to another players territory!.", Toast.LENGTH_LONG).show());
                    }

                }

                break;

            case ADD_UNIT_TAPPED:
//                addToSelectedTerritoryUnitCount(1);
                break;

            case REMOVE_UNIT_TAPPED:
//                addToSelectedTerritoryUnitCount(-1);
                break;

            case CONFIRM_TAPPED:
                SM.Advance(new DefaultState(SM));
                break;

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
//                SM.Game.getWorld().unselectTerritory();
//                destinationTerritory = null;
                SM.Advance(new DefaultState(SM));
                break;

        }

        return false;
    }
}
