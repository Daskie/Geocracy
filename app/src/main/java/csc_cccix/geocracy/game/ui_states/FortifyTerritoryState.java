package csc_cccix.geocracy.game.ui_states;

import android.util.Log;
import android.widget.Toast;

import csc_cccix.geocracy.fragments.FortifyTerritoryFragment;
import csc_cccix.geocracy.game.IStateMachine;
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
        SM.Game.getWorld().selectTerritory(originTerritory);
        SM.Game.getWorld().highlightTerritory(originTerritory);

        if (originTerritory.getAdjacentFriendlyTerritories() != null) {
            SM.Game.getWorld().highlightTerritories(originTerritory.getAdjacentFriendlyTerritories());
        }

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
                        SM.Game.getWorld().selectTerritory(originTerritory);
                        SM.Game.getWorld().targetTerritory(destinationTerritory);
                        SM.Game.getCameraController().targetTerritory(destinationTerritory);
                        SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().setUpdateUnitCountButtonsVisibility(true));
                        SM.Game.showBottomPaneFragment(FortifyTerritoryFragment.newInstance(originTerritory, destinationTerritory));
                    } else {
                        SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "Cannot move units to another players territory!.", Toast.LENGTH_LONG).show());
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

        SM.Game.getActivity().runOnUiThread(() -> {
            SM.Game.getActivity().getConfirmButton().show();
            SM.Game.getActivity().getCancelBtn().hide();
        });
    }
}
