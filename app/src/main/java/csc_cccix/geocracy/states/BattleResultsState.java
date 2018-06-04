package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.fragments.BattleResultsFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;

public class BattleResultsState implements  GameState {

    private static final String TAG = "BATTLE_RESULTS_STATE";

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;

    public BattleResultsState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "SETTING ORIGIN TERRITORY");
        this.originTerritory = territory;
    }
    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "SETTING TARGET TERRITORY");
        this.targetTerritory = territory;
    }

    public void enableAttackMode() {
        Log.i(TAG, "INVALID ACTION: -> CANNOT ENABLE ATTACK MODE");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "INVALID ACTION: -> CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID ACTION: -> ALREADY IN BATTLE RESULTS STATE!");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "INVALID ACTION: -> CANNOT UPDATE UNIT COUNT");
    }

    public void confirmAction() {
        Log.i(TAG, "USER CANCELED ACTION -> N/A");
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.defaultState);
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.activity.showBottomPaneFragment(BattleResultsFragment.newInstance(this.originTerritory, this.targetTerritory));
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.targetTerritory);
        game.cameraController.targetTerritory(this.targetTerritory);
        String ui_tag = "UI_EVENT";
        EventBus.publish(ui_tag, UIEvent.SET_ATTACK_MODE_ACTIVE);
        EventBus.publish(ui_tag, UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish(ui_tag, UIEvent.HIDE_CANCEL_BUTTON);
    }

}