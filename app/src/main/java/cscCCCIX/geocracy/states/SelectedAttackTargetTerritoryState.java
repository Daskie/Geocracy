package cscCCCIX.geocracy.states;

import cscCCCIX.geocracy.EventBus;
import cscCCCIX.geocracy.fragments.TroopSelectionFragment;
import cscCCCIX.geocracy.game.Game;
import cscCCCIX.geocracy.game.UIEvent;
import cscCCCIX.geocracy.world.Territory;

public class SelectedAttackTargetTerritoryState implements  GameState {

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;

    public SelectedAttackTargetTerritoryState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("SELECTED ATTACK TARGET TERRITORY STATE: SETTING ORIGIN TERRITORY");
        this.originTerritory = territory;
    }
    public void selectTargetTerritory(Territory territory) {
        System.out.println("SELECTED ATTACK TARGET TERRITORY STATE: SETTING TARGET TERRITORY");
        this.targetTerritory = territory;
    }

    public void enableAttackMode() {
        System.out.println("SELECTED ATTACK TARGET TERRITORY STATE: -> CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        System.out.println("SELECTED ATTACK TARGET TERRITORY STATE: CANNOT UPDATE UNIT COUNT");
    }


    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        System.out.println("SELECTED ATTACK TARGET TERRITORY STATE: -> ENTER DICE ROLL STATE");
        game.setState(game.DiceRollState);
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().selectTargetTerritory(this.targetTerritory);

        System.out.println(this.originTerritory);
        System.out.println(this.targetTerritory);
        game.getState().performDiceRoll(new DiceRollDetails(this.originTerritory, 3),
                                        new DiceRollDetails(this.targetTerritory, 4));
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        System.out.println("SELECTED ATTACK TARGET TERRITORY STATE: INVALID STATE ACCESSED");
    }


    public void cancelAction() {
        System.out.println("USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(game.DefaultState);
        game.getState().initState();
    }

    public void initState() {
        System.out.println("INIT SELECTED ATTACK TARGET TERRITORY STATE:");
        game.activity.showBottomPaneFragment(TroopSelectionFragment.newInstance(this.originTerritory, this.targetTerritory));
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.targetTerritory);
        game.cameraController.targetTerritory(this.targetTerritory);
        EventBus.publish("UI_EVENT", UIEvent.SET_ATTACK_MODE_ACTIVE);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.SHOW_CANCEL_BUTTON);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);

    }

}