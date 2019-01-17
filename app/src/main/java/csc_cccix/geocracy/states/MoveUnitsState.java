package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.fragments.FortifyTerritoryFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.world.Territory;

public class MoveUnitsState extends IGameState {

    private Territory originTerritory;
    private Territory targetTerritory;

    public MoveUnitsState(Game game) {
        TAG = "MOVE UNITS_STATE";
        this.game = game;
    }

    public MoveUnitsState(Game game, Territory originTerritory) {
        this(game);

        Log.i(TAG, "SETTING ORIGIN TERRITORY");
        this.originTerritory = originTerritory;

        game.getActivity().runOnUiThread(() -> {
            game.getActivity().removeActiveBottomPaneFragment();
            game.getActivity().showBottomPaneFragment(FortifyTerritoryFragment.newInstance(this.originTerritory, this.targetTerritory));
        });

    }

    public void selectSecondaryTerritory(Territory territory) {
        Log.i(TAG, "SETTING TARGET TERRITORY");

        if (game.getCurrentPlayer().getId() == territory.getOwner().getId() && this.originTerritory != territory) {
            this.targetTerritory = territory;
            game.getWorld().unhighlightTerritories();
            game.getWorld().highlightTerritory(this.originTerritory);
            game.getWorld().highlightTerritories(this.originTerritory.getAdjacentFriendlyTerritories());
            game.getWorld().selectTerritory(this.originTerritory);
            game.getWorld().targetTerritory(this.targetTerritory);
            game.getCameraController().targetTerritory(this.targetTerritory);
            game.getActivity().runOnUiThread(() -> {
                game.getActivity().removeActiveBottomPaneFragment();
                game.getActivity().showBottomPaneFragment(FortifyTerritoryFragment.newInstance(this.originTerritory, this.targetTerritory));
                game.getActivity().hideAllGameInteractionButtons();
                game.getActivity().getAddUnitBtn().show();
                game.getActivity().getRemoveUnitBtn().show();
                game.getActivity().getCancelBtn().show();
                game.getActivity().getConfirmButton().show();
            });
        } else {
            Log.i(TAG, "INVALID TARGET TERRITORY TO FORTIFY");
        }
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "UPDATING UNIT COUNT BY: " + amount);
        if (amount == 0) return;
        else if (amount > 0) {
            amount = Math.abs(amount);
            if (this.originTerritory.getNArmies() - amount > 0) {
                this.originTerritory.setNArmies(this.originTerritory.getNArmies() - amount);
                this.targetTerritory.setNArmies(this.targetTerritory.getNArmies() + amount);
            }
        }
        else if (amount < 0) {
            amount = Math.abs(amount);
            if (this.targetTerritory.getNArmies() - amount > 0) {
                this.originTerritory.setNArmies(this.originTerritory.getNArmies() + amount);
                this.targetTerritory.setNArmies(this.targetTerritory.getNArmies() - amount);
            }
        }

        game.getActivity().runOnUiThread(() -> {
            game.getActivity().getConfirmButton().show();
            game.getActivity().getCancelBtn().hide();
        });
    }

    public void confirmAction() {
        Log.i(TAG, "SHOULD END PLAYER TURN");
        game.getActivity().runOnUiThread(() ->game.getActivity().removeActiveBottomPaneFragment());
        game.setState(new DefaultState(game));
        game.getState().initState();
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.getActivity().runOnUiThread(() ->game.getActivity().removeActiveBottomPaneFragment());
            game.setState(new DefaultState(game));
            game.getState().initState();
    }

    // TODO: maybe this should disable fortify?
    public void fortifyAction() { Log.i(TAG, "CANNOT REENABLE FORTIFY MODE"); }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.getActivity().removeActiveBottomPaneFragment();
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.originTerritory);

        // TODO: ideally, player shouldn't access this state unless they have adjacent friendly territories anyways
        if (this.originTerritory.getAdjacentFriendlyTerritories() != null && this.originTerritory.getAdjacentFriendlyTerritories().size() > 0) {
            game.getWorld().highlightTerritories(this.originTerritory.getAdjacentFriendlyTerritories());
        }

        game.getActivity().runOnUiThread(() -> {
            game.getActivity().hideAllGameInteractionButtons();
            game.getActivity().getConfirmButton().show();
            game.getActivity().getCancelBtn().show();
        });
    }

}