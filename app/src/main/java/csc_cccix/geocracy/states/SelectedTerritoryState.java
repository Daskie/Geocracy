package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;

public class SelectedTerritoryState implements  GameState {

    private static final String TAG = "SELECT_TERRITORY_STATE";

    private Game game;
    private Territory territory;

    public SelectedTerritoryState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        if (this.territory == null) {
            Log.i(TAG, "A TERRITORY WAS SELECTED, DISPLAY DETAILS");
            this.territory = territory;
            if (this.territory == null) return;
        } else {
            Log.i(TAG, "ANOTHER TERRITORY WAS SELECTED, SWITCH TO OTHER TERRITORY TO DISPLAY DETAILS");
            this.territory = territory;
        }

        if (this.territory.getOwner().getId() == game.getCurrentPlayer().getId()) {
            Log.i(TAG, "ENABLE ATTACK MODE: VALID TERRITORY -> ENABLE ATTACK MODE ON");
            game.getActivity().runOnUiThread(() ->  game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, true));
        } else {
            Log.i(TAG, "ENABLE ATTACK MODE: INVALID TERRITORY TO DISABLE ATTACK BUTTON");
            game.getActivity().runOnUiThread(() ->  game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, false));
        }
    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "TARGET TERRITORY ACTION UNAVAILABLE");
    }

    public void enableAttackMode() {
        if (this.territory.getOwner() == game.getCurrentPlayer()) {
            Log.i(TAG, "ENABLE ATTACK MODE -> ENTER INTENT TO ATTACK STATE");
            game.setState(new IntentToAttackState(game));
            game.getState().selectOriginTerritory(territory);
            game.getState().initState();
        } else {
            Log.i(TAG, "ENABLE ATTACK MODE: INVALID TERRITORY TO ENABLE ATTACK MODE ON");
        }
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "INVALID ACTION: CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "INVALID ACTION: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID ACTION: BATTLE COMPLETED");
    }

    public void confirmAction() {
        Log.i(TAG, "INVALID ACTION: USER CANCELED ACTION");
    }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> N/A"); }

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
            game.getActivity().getAttackBtn().show();
            game.getActivity().getCancelBtn().show();
        });

    }

}