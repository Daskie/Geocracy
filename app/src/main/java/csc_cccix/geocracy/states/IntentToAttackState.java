package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.world.Territory;

public class IntentToAttackState implements  GameState {

    private static final String TAG = "INTENT_TO_ATTACK_STATE";

    private Game game;
    private Territory originTerritory;
    private boolean originTerritoryLock;

    public IntentToAttackState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "ALREADY CURRENT STATE");
        if (!originTerritoryLock) this.originTerritory = territory;
    }

    public void selectTargetTerritory(Territory targetTerritory) {
        Log.i(TAG, "ANOTHER TERRITORY SELECTED -> GO TO SELECTED ATTACK TARGET STATE");
        if (originTerritory.getAdjacentTerritories().contains(targetTerritory)) {
            game.setState(new SelectedAttackTargetTerritoryState(game));
            game.getState().selectOriginTerritory(this.originTerritory);
            game.getState().selectTargetTerritory(targetTerritory);
            game.getState().initState();
        } else {
            cancelAction();
        }

    }

    public void enableAttackMode() {
        Log.i(TAG, "-> Disable Attack Mode");
        game.setState(new SelectedTerritoryState(game));
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().initState();
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "INVALID ACTION: CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "INVALID ACTION: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID ACTION: BATTLE COMPLETED INVALID STATE ACCESSED");
    }

    public void confirmAction() {
        Log.i(TAG, "INVALID ACTION: USER CONFIRMED ACTION");
    }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> N/A"); }

    public void fortifyAction() { Log.i(TAG, "CANNOT ENABLE FORTIFY MODE"); }

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