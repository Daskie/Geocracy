package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.UIEvent;
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
            Log.i(TAG, "TERRITORY SELECTED, DISPLAY DETAILS");
        } else {
            Log.i(TAG, "ANOTHER TERRITORY SELECTED, SWITCH TO OTHER TERRITORY TO DISPLAY DETAILS");
        }
        this.territory = territory;
    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "TARGET TERRITORY ACTION UNAVAILABLE");
    }

    public void enableAttackMode() {
        Log.i(TAG, "ENABLE ATTACK MODE -> ENTER INTENT TO ATTACK STATE");
        game.setState(new IntentToAttackState(game));
        game.getState().selectOriginTerritory(territory);
        game.getState().initState();
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
        String ui_tag = "UI_EVENT";
        EventBus.publish(ui_tag, UIEvent.SET_ATTACK_MODE_INACTIVE);

        EventBus.publish(ui_tag, UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish(ui_tag, UIEvent.SHOW_CANCEL_BUTTON);

        EventBus.publish(ui_tag, UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }

}