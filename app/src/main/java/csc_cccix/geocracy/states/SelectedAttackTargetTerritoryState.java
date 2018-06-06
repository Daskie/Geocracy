package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.fragments.TroopSelectionFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;

public class SelectedAttackTargetTerritoryState implements  GameState {

    private static final String TAG = "SELECTED_ATTACK_T_STATE";

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;
    private boolean originTerritoryLock;

    public SelectedAttackTargetTerritoryState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "SETTING ORIGIN TERRITORY");
        if (originTerritoryLock == false) this.originTerritory = territory;
    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "SETTING TARGET TERRITORY");
        this.targetTerritory = territory;
    }

    public void enableAttackMode() {
        Log.i(TAG, "-> CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "CANNOT UPDATE UNIT COUNT");
    }


    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "-> ENTER DICE ROLL STATE");
        game.setState(new DiceRollState(game));
        game.getState().selectOriginTerritory(this.originTerritory);
        game.getState().selectTargetTerritory(this.targetTerritory);
        game.getState().performDiceRoll(new DiceRollDetails(this.originTerritory, 3),
                                        new DiceRollDetails(this.targetTerritory, 4));
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID STATE ACCESSED");
    }

    public void confirmAction() {
        Log.i("", "SETUP INITIAL TERRITORIES STATE: USER CANCELED ACTION -> N/A");
    }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> N/A"); }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        originTerritoryLock = false;
        game.setState(new DefaultState(game));
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT SELECTED ATTACK TARGET TERRITORY STATE:");
        game.getActivity().showBottomPaneFragment(TroopSelectionFragment.newInstance(this.originTerritory, this.targetTerritory));
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().targetTerritory(this.targetTerritory);
        game.getCameraController().targetTerritory(this.targetTerritory);

        originTerritoryLock = true;

        String ui_tag = "UI_EVENT";
        EventBus.publish(ui_tag, UIEvent.SET_ATTACK_MODE_ACTIVE);
        EventBus.publish(ui_tag, UIEvent.SHOW_ATTACK_MODE_BUTTON);
        EventBus.publish(ui_tag, UIEvent.SHOW_CANCEL_BUTTON);
        EventBus.publish(ui_tag, UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }

}