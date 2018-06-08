package csc_cccix.geocracy.states;

import android.util.Log;
import android.widget.Toast;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.fragments.TroopSelectionFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class FortifyTerritoryState implements  GameState {

    private static final String TAG = "SELECTED_ATTACK_T_STATE";

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;
    private boolean originTerritoryLock;
    private boolean targetTerritoryLock;
    private TroopSelectionFragment troopSelectionFragment;

    public FortifyTerritoryState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "SETTING ORIGIN TERRITORY");
        if (!originTerritoryLock) this.originTerritory = territory;
    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "SETTING TARGET TERRITORY");

        if (game.getCurrentPlayer().getId() == territory.getOwner().getId() && this.originTerritory != territory) {
            this.targetTerritory = territory;
            game.getWorld().unhighlightTerritories();
            game.getWorld().selectTerritory(this.targetTerritory);
            game.getWorld().highlightTerritory(this.originTerritory);
            game.getWorld().highlightTerritories(this.originTerritory.getAdjacentFriendlyTerritories());
            game.getActivity().runOnUiThread(() -> {
                game.getActivity().hideAllGameInteractionButtons();
                game.getActivity().getAddUnitBtn().show();
                game.getActivity().getRemoveUnitBtn().show();
                game.getActivity().getCancelBtn().show();
            });
        } else {
            Log.i(TAG, "INVALID TARGET TERRITORY TO FORTIFY");
        }
    }

    public void enableAttackMode() {
        Log.i(TAG, "-> CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "CANNOT UPDATE UNIT COUNT");
    }


    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) { Log.i(TAG, "CANNOT PERFORM DICE ROLL"); }

    public void battleCompleted(BattleResultDetails battleResultDetails) { Log.i(TAG, "INVALID STATE ACCESSED"); }

    public void confirmAction() {
        int numArmiesSelected = troopSelectionFragment.getSelectedNumberOfUnits();
        if(numArmiesSelected<=originTerritory.getNArmies()) {
            game.getCurrentPlayer().setNumArmiesAttacking(numArmiesSelected);
            EventBus.publish("USER_ACTION", new GameEvent(GameAction.CONFIRM_UNITS_TAPPED, null));
        }
        else
            game.getActivity().runOnUiThread(() -> Toasty.info(game.getActivity().getBaseContext(), "You do not have enough armies in this territory to attack with the number you selected! ", Toast.LENGTH_LONG).show());
    }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> N/A"); }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        originTerritoryLock = false;
        game.setState(new DefaultState(game));
        game.getState().initState();
    }

    public void fortifyAction() { Log.i(TAG, "CANNOT REENABLE FORTIFY MODE"); }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.getActivity().removeActiveBottomPaneFragment();
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.originTerritory);
        game.getWorld().highlightTerritories(this.originTerritory.getAdjacentFriendlyTerritories());
        originTerritoryLock = true;

        game.getActivity().runOnUiThread(() -> {
            game.getActivity().hideAllGameInteractionButtons();
            game.getActivity().getConfirmButton().show();
            game.getActivity().getCancelBtn().show();
        });
    }

}