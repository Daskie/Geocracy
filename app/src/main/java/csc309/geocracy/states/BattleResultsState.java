package csc309.geocracy.states;

import android.os.Bundle;

import csc309.geocracy.EventBus;
import csc309.geocracy.fragments.BattleResultsFragment;
import csc309.geocracy.fragments.DiceRollFragment;
import csc309.geocracy.fragments.TroopSelectionFragment;
import csc309.geocracy.game.Game;
import csc309.geocracy.game.GameActivity;
import csc309.geocracy.game.UIEvent;
import csc309.geocracy.world.Territory;

public class BattleResultsState implements  GameState {

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;

    public BattleResultsState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("BATTLE RESULTS STATE: SETTING ORIGIN TERRITORY");
        this.originTerritory = territory;
    }
    public void selectTargetTerritory(Territory territory) {
        System.out.println("BATTLE RESULTS STATE: SETTING TARGET TERRITORY");
        this.targetTerritory = territory;
    }

    public void enableAttackMode() {
        System.out.println("BATTLE RESULTS STATE: -> CANNOT ENABLE ATTACK MODE");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        System.out.println("BATTLE RESULTS STATE: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        System.out.println("BATTLE RESULTS STATE: ALREADY IN BATTLE RESULTS STATE!");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        System.out.println("BATTLE RESULTS STATE: CANNOT UPDATE UNIT COUNT");
    }

    public void cancelAction() {
        System.out.println("USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.DefaultState);
        game.getState().initState();
    }

    public void initState() {
        System.out.println("INIT BATTLE RESULTS STATE:");
        game.activity.showBottomPaneFragment(BattleResultsFragment.newInstance(this.originTerritory, this.targetTerritory));
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.targetTerritory);
        game.cameraController.targetTerritory(this.targetTerritory);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_ACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_CANCEL_BUTTON);
    }

}