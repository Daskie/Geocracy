package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.world.Territory;

public class IntentToAttackState extends  GameState {

    private Territory originTerritory;

    public IntentToAttackState(Game game) {
        TAG = "INTENT_TO_ATTACK_STATE";
        this.game = game;
    }

    public IntentToAttackState(Game game, Territory originTerritory) {
        this(game);
        this.originTerritory = originTerritory;
    }

    // Used to select the adjacent enemy territory the player would like to attack
    public void selectSecondaryTerritory(Territory targetTerritory) {
        Log.i(TAG, "ANOTHER TERRITORY SELECTED -> GO TO SELECTED ATTACK TARGET STATE");
        if (originTerritory.getAdjacentEnemyTerritories().contains(targetTerritory)) {
            game.setState(new SelectedAttackTargetTerritoryState(game));
            game.getState().selectPrimaryTerritory(this.originTerritory);
            game.getState().selectSecondaryTerritory(targetTerritory);
            game.getState().initState();
        } else {
            Log.d(TAG, "CANNOT ATTACK FRIENDLY TERRITORIES!");
            cancelAction();
        }
    }

    public void enableAttackMode() {

        // TODO: Not sure how to handle because there's the cancel button. I think this button should just be interaction disabled, but colored to show active attack state to user?

//        Log.i(TAG, "-> Disable Attack Mode");
//        game.setState(new SelectedTerritoryState(game, this.originTerritory));
//        game.getState().initState();
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.getActivity().runOnUiThread(() -> game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, false));
        game.setState(new SelectedTerritoryState(game, this.originTerritory));
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        Log.i(TAG, "TERRITORY SELECTED, ATTACK MODE ENABLED: -> DISPLAY ADJACENT TERRITORIES AVAILABLE TO ATTACK");

        game.getWorld().highlightTerritories(originTerritory.getAdjacentEnemyTerritories());

        game.getActivity().runOnUiThread(() -> {
            game.getActivity().hideAllGameInteractionButtons();
            game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, true);
            game.getActivity().getCancelBtn().show();

        });
    }

}