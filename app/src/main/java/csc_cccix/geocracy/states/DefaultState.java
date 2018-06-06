package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.GameActivity;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;

public class DefaultState implements GameState {

    private static final String TAG = "DEFAULT_STATE";
    private Game game;

    public DefaultState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "TERRITORY SELECTED ACTION -> DISPLAY TERRITORY DETAILS");
        if(!game.getWorld().allTerritoriesOccupied())
            game.setState(new SetUpInitTerritoriesState(game, game.getActivity()));
        else
            game.setState(new SelectedTerritoryState(game));
        game.getState().selectOriginTerritory(territory);
        game.getState().initState();
    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "CANNOT SELECT TARGET TERRITORY, NO ORIGIN TERRITORY");
    }

    public void enableAttackMode() {
        Log.i(TAG, "CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "CANNOT UPDATE UNIT COUNT");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID STATE ACCESSED");
    }

    public void confirmAction() {
        Log.i(TAG, "INVALID ACTION: CONFRIM NOT AVAILIBLE");
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> NULL ACTION");
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.getActivity().removeActiveBottomPaneFragment();
        game.getWorld().unselectTerritory();
        game.getWorld().untargetTerritory();
        game.getWorld().unhighlightTerritories();

        Player currentPlayer = game.getGameData().players[game.getGameData().currentPlayer];
        game.getWorld().highlightTerritories(currentPlayer.getTerritories());

        String ui_tag = "UI_EVENT";
        EventBus.publish(ui_tag, UIEvent.HIDE_ATTACK_MODE_BUTTON);
        EventBus.publish(ui_tag, UIEvent.SET_ATTACK_MODE_INACTIVE);
        EventBus.publish(ui_tag, UIEvent.HIDE_CANCEL_BUTTON);
        EventBus.publish(ui_tag, UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }
}