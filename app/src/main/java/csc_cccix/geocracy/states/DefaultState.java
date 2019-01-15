package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.world.Territory;

public class DefaultState extends GameState {

    public DefaultState(Game game) {
        TAG = "DEFAULT_STATE";
        this.game = game;
    }

    public void selectPrimaryTerritory(Territory territory) {
        Log.i(TAG, "TERRITORY SELECTED ACTION -> DISPLAY TERRITORY DETAILS");
        if(!game.getWorld().allTerritoriesOccupied())
            game.setState(new SetUpInitTerritoriesState(game));
        else
            game.setState(new SelectedTerritoryState(game));
        game.getState().selectPrimaryTerritory(territory);
        game.getState().initState();
    }

    public void selectSecondaryTerritory(Territory territory) {
        Log.i(TAG, "CANNOT SELECT TARGET TERRITORY, NO ORIGIN TERRITORY");
    }

    public void enableAttackMode() {
        Log.i(TAG, "CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID STATE ACCESSED");
    }

    public void fortifyAction() { Log.i(TAG, "CANNOT ENABLE FORTIFY MODE, SELECT A TERRITORY FIRST"); }

    public void confirmAction() {
        Log.i(TAG, "INVALID ACTION: CONFRIM NOT AVAILIBLE");
    }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> END PLAYER TURN"); }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> NULL ACTION");
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.getActivity().removeActiveBottomPaneFragment();

        game.getWorld().unselectTerritory();
        game.getWorld().untargetTerritory();
        game.getWorld().unhighlightTerritories();
        game.getWorld().highlightTerritories(game.getCurrentPlayer().getTerritories());
        game.getActivity().runOnUiThread(() -> {
            game.getActivity().updateCurrentPlayerFragment();
            game.getActivity().removeActiveOverlayFragment();
            game.getActivity().hideAllGameInteractionButtons();
            game.getActivity().getEndTurnButton().show();
        });
    }
}