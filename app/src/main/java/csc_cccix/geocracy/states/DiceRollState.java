
package csc_cccix.geocracy.states;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.fragments.DiceRollFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class DiceRollState implements  GameState {

    private static final String TAG = "DICE_ROLL_STATE";

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;

    public DiceRollState(Game game) {
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
        Log.i(TAG, "INVALID ACTION: -> ALREADY PERFORMING DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "BATTLE COMPLETED -> ENTER BATTLE RESULTS STATEL");
        game.setState(game.battleResultsState);
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().selectTargetTerritory(this.targetTerritory);
        game.getState().initState();
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "INVALID ACTION: CANNOT UPDATE UNIT COUNT");
    }

    public void confirmAction() {
        Log.i(TAG, "USER CONFIRM ACTION -> N/A");
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.defaultState);
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.activity.showBottomPaneFragment(DiceRollFragment.newInstance(this.originTerritory, this.targetTerritory));
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.targetTerritory);
        game.cameraController.targetTerritory(this.targetTerritory);

        String ui_tag = "UI_EVENT";
        EventBus.publish(ui_tag, UIEvent.SET_ATTACK_MODE_ACTIVE);
        EventBus.publish(ui_tag, UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish(ui_tag, UIEvent.HIDE_CANCEL_BUTTON);

        Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::goToBattleResults);

    }

    private void goToBattleResults() {
        this.battleCompleted(null);
    }

}