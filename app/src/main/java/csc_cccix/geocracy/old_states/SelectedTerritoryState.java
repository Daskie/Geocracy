package csc_cccix.geocracy.old_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.world.Territory;

public class SelectedTerritoryState extends IGameState {

    public SelectedTerritoryState(Game game) {
        TAG = "SELECT_TERRITORY_STATE";
        this.game = game;
    }

    public SelectedTerritoryState(Game game, Territory selectedTerritory) {
        this(game);

        Log.d(TAG, "A TERRITORY WAS SELECTED, DISPLAY ITS DETAILS");
        this.territory = selectedTerritory;
    }

    public void selectPrimaryTerritory(Territory territory) {
        Log.d(TAG, "ANOTHER TERRITORY WAS SELECTED, SWITCH TO OTHER TERRITORY TO DISPLAY DETAILS");

        // if a different territory was selected, switch to that territory for selection state
        if (territory != this.territory) {
            game.setState(new SelectedTerritoryState(game, territory));
            game.getState().initState();
        }

    }

    public void enableAttackMode() {
        // If current player is the owner and the player has enough units to perform an attack
        if (this.territory.getOwner() == game.getCurrentPlayer() && this.territory.getNArmies() >= 2) {
            Log.d(TAG, "ENABLE ATTACK MODE ACTION: ENTERING INTENT TO ATTACK STATE");
            game.setState(new IntentToAttackState(game, territory));
            game.getState().initState();
        } else {
            Log.d(TAG, "ENABLE ATTACK MODE ACTION: INVALID TERRITORY TO ENABLE ATTACK MODE ON");
        }
    }

    public void fortifyAction() {
        Log.i(TAG, "FORTIFY ACTION");
        if (this.territory.getOwner() == game.getCurrentPlayer() && this.territory.getNArmies() >= 2) {
            Log.i(TAG, "ENABLE FORTIFY MODE -> ENTER FORTIFY STATE");

            // TODO: trying new state
            game.setState(new MoveUnitsState(game, this.territory));

//            game.setState(new FortifyTerritoryState(game));
//            game.getState().selectPrimaryTerritory(territory);
            game.getState().initState();
        } else {
            Log.i(TAG, "ENABLE FORTIFY MODE: INVALID TERRITORY TO ENABLE FORTIFY MODE ON");
        }
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        this.territory = null;
        game.setState(new DefaultState(game));
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.getActivity().showBottomPaneFragment(TerritoryDetailFragment.newInstance(this.territory));
        game.getWorld().selectTerritory(this.territory);
        game.getWorld().unhighlightTerritories();
        game.getCameraController().targetTerritory(this.territory);
        game.getActivity().runOnUiThread(() -> {
            game.getActivity().hideAllGameInteractionButtons();
            game.getActivity().getEndTurnButton().show();
            if (this.territory.getOwner().getId() == game.getCurrentPlayer().getId()) {
                if (this.territory.getNArmies() >= 2) {
                    game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, true);
                    if (this.territory.getAdjacentFriendlyTerritories() != null) {
                        game.getActivity().setFortifyButtonVisibilityAndActiveState(true, true);
                    } else {
                        game.getActivity().setFortifyButtonVisibilityAndActiveState(true, false);
                    }

                } else {
                    Log.i(TAG, "ENABLE ATTACK MODE: INVALID TERRITORY -> DISABLE ATTACK BUTTON");
                    game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, false);
                    game.getActivity().setFortifyButtonVisibilityAndActiveState(true, false);
                }
            }
            game.getActivity().getCancelBtn().show();
        });

    }

}