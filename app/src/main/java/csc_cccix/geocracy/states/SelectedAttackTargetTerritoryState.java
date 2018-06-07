package csc_cccix.geocracy.states;

import android.util.Log;
import android.widget.Toast;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.fragments.TroopSelectionFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.UIEvent;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class SelectedAttackTargetTerritoryState implements  GameState {

    private static final String TAG = "SELECTED_ATTACK_T_STATE";

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;
    private boolean originTerritoryLock;
    private TroopSelectionFragment troopSelectionFragment;

    public SelectedAttackTargetTerritoryState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "SETTING ORIGIN TERRITORY");
        if (!originTerritoryLock) this.originTerritory = territory;
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

        int randNumArmies;
        if(game.getCurrentPlayer() instanceof HumanPlayer) {
             randNumArmies = (int)(Math.random()*this.targetTerritory.getNArmies()) + 1;
            game.getState().performDiceRoll(new DiceRollDetails(this.originTerritory, game.getCurrentPlayer().getNumArmiesAttacking()),
                    new DiceRollDetails(this.targetTerritory, randNumArmies));
        }

//        else{
//            randNumArmies = (int)(Math.random()*this.originTerritory.getNArmies()) + 1;
//            game.getState().performDiceRoll(new DiceRollDetails(this.originTerritory, randNumArmies),
//                    new DiceRollDetails(this.targetTerritory, game.getCurrentPlayer().getNumArmiesDefending()));
//        }
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID STATE ACCESSED");
    }

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

    public void initState() {
        Log.i(TAG, "INIT SELECTED ATTACK TARGET TERRITORY STATE:");
        troopSelectionFragment = TroopSelectionFragment.newInstance(this.originTerritory, this.targetTerritory, game.getCurrentPlayer());
        game.getActivity().showBottomPaneFragment(troopSelectionFragment);
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().targetTerritory(this.targetTerritory);
        game.getCameraController().targetTerritory(this.targetTerritory);

        originTerritoryLock = true;

        game.getActivity().runOnUiThread(() -> {
            game.getActivity().hideAllGameInteractionButtons();
            game.getActivity().setAttackModeButtonVisibilityAndActiveState(true, true);
            game.getActivity().getConfirmButton().show();
            game.getActivity().getCancelBtn().show();
        });
    }

}