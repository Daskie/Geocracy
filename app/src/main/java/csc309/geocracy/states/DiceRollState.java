
package csc309.geocracy.states;

import java.util.concurrent.TimeUnit;

import csc309.geocracy.EventBus;
import csc309.geocracy.fragments.DiceRollFragment;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.UIEvent;
import csc309.geocracy.world.Territory;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class DiceRollState implements  GameState {

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;

    public DiceRollState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("DICE ROLL STATE: SETTING ORIGIN TERRITORY");
        this.originTerritory = territory;
    }
    public void selectTargetTerritory(Territory territory) {
        System.out.println("DICE ROLL STATE: SETTING TARGET TERRITORY");
        this.targetTerritory = territory;
    }

    public void enableAttackMode() {
        System.out.println("DICE ROLL STATE: -> CANNOT ENABLE ATTACK MODE");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
//        this.originTerritory = attackerDetails.territory;
//        this.targetTerritory = defenderDetails.territory;

        System.out.println("DICE ROLL STATE: ALREADY PERFORMING DICE ROLL");

        System.out.println(attackerDetails);
        System.out.println(defenderDetails);
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        System.out.println("DICE ROLL STATE: -> ENTER BATTLE RESULTS STATE");
        game.setState(game.BattleResultsState);
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().selectTargetTerritory(this.targetTerritory);
        game.getState().initState();
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        System.out.println("USER CANCELED ACTION: CANNOT UPDATE UNIT COUNT");
    }

    public void cancelAction() {
        System.out.println("USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.DefaultState);
        game.getState().initState();
    }

    public void initState() {
        System.out.println("INIT DICE ROLL STATE:");
        game.activity.showBottomPaneFragment(DiceRollFragment.newInstance(this.originTerritory, this.targetTerritory));
        game.getWorld().unhighlightTerritories();
        System.out.println(this.originTerritory);
        System.out.println(this.targetTerritory);
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.targetTerritory);
        game.cameraController.targetTerritory(this.targetTerritory);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_ACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_CANCEL_BUTTON);

        Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::goToBattleResults);

    }

    private void goToBattleResults() {
        this.battleCompleted(null);
    }

}