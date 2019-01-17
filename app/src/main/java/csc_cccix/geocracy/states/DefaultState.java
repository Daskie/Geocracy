package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.world.Territory;

public class DefaultState extends IGameState {

    public DefaultState(Game game) {
        TAG = "DEFAULT_STATE";
        this.game = game;
    }

    public void selectPrimaryTerritory(Territory territory) {
        Log.i(TAG, "TERRITORY SELECTED ACTION -> DISPLAY TERRITORY DETAILS");
        if(!game.getWorld().allTerritoriesOccupied())
            game.setState(new SetUpInitTerritoriesState(game));
        else
            game.setState(new SelectedTerritoryState(game, territory));
        game.getState().initState();
    }

    public void fortifyAction() { Log.i(TAG, "CANNOT ENABLE FORTIFY MODE, SELECT A TERRITORY FIRST"); }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> END PLAYER TURN"); }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.getActivity().removeActiveBottomPaneFragment();

        game.getWorld().unselectTerritory();
        game.getWorld().untargetTerritory();
        game.getWorld().unhighlightTerritories();
        game.getWorld().highlightTerritories(game.getCurrentPlayer().getTerritories());
        game.getActivity().runOnUiThread(() -> {
            game.getActivity().updateCurrentPlayerFragment();
//            game.getActivity().removeActiveOverlayFragment();
            game.getActivity().hideAllGameInteractionButtons();
            game.getActivity().getEndTurnButton().show();
        });
    }
}