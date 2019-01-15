package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.world.Territory;

public class IntentToAttackState extends  GameState {

    private Territory originTerritory;
    private boolean originTerritoryLock;

    public IntentToAttackState(Game game) {
        TAG = "INTENT_TO_ATTACK_STATE";
        this.game = game;
    }

    public void selectPrimaryTerritory(Territory territory) {
        Log.i(TAG, "ALREADY CURRENT STATE");
        if (!originTerritoryLock) this.originTerritory = territory;
    }

    public void selectSecondaryTerritory(Territory targetTerritory) {
        Log.i(TAG, "ANOTHER TERRITORY SELECTED -> GO TO SELECTED ATTACK TARGET STATE");
        if (originTerritory.getAdjacentTerritories().contains(targetTerritory)) {
            game.setState(new SelectedAttackTargetTerritoryState(game));
            game.getState().selectPrimaryTerritory(this.originTerritory);
            game.getState().selectSecondaryTerritory(targetTerritory);
            game.getState().initState();
        } else {
            cancelAction();
        }

    }

    public void enableAttackMode() {
        Log.i(TAG, "-> Disable Attack Mode");
        game.setState(new SelectedTerritoryState(game));
        game.getState().selectPrimaryTerritory(this.originTerritory);
        game.getState().initState();
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        originTerritoryLock = false;
        game.getActivity().runOnUiThread(() -> game.getActivity().setAttackModeButtonVisibilityAndActiveState(false, false));
        game.setState(new DefaultState(game));
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        Log.i(TAG, "TERRITORY SELECTED, ATTACK MODE ENABLED: -> DISPLAY ADJACENT TERRITORIES AVAILABLE TO ATTACK");

        game.getWorld().highlightTerritories(originTerritory.getAdjacentEnemyTerritories());

        originTerritoryLock = true;

        game.getActivity().runOnUiThread(() -> {
            game.getActivity().hideAllGameInteractionButtons();
            game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, true);
            game.getActivity().getCancelBtn().show();

        });
    }

}