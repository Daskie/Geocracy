package csc_cccix.geocracy.old_states;

import android.util.Log;
import android.widget.Toast;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.fragments.TroopSelectionFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class SelectedAttackTargetTerritoryState extends IGameState {

    private Territory originTerritory;
    private Territory targetTerritory;
    private boolean originTerritoryLock;
    private TroopSelectionFragment troopSelectionFragment;

    public SelectedAttackTargetTerritoryState(Game game) {
        TAG = "SELECTED_ATTACK_TARGET_TERRITORY_STATE";
        this.game = game;
    }

    public void selectPrimaryTerritory(Territory territory) {
        Log.i(TAG, "SETTING ORIGIN TERRITORY");
        if (!originTerritoryLock) this.originTerritory = territory;
    }

    public void selectSecondaryTerritory(Territory territory) {
        Log.i(TAG, "SETTING TARGET TERRITORY");
        this.targetTerritory = territory;
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
        Log.i(TAG, "-> ENTER DICE ROLL STATE");
        game.setState(new DiceRollState(game));
        game.getState().selectPrimaryTerritory(this.originTerritory);
        game.getState().selectSecondaryTerritory(this.targetTerritory);

        int numArmiesToDefendWith;
        int numArmiesToAttackWith;


        if(game.getCurrentPlayer() instanceof HumanPlayer) {
            if(this.targetTerritory.getNArmies()>=2)
                numArmiesToDefendWith = 2;
            else
                numArmiesToDefendWith = 1;

            game.getState().performDiceRoll(new DiceRollDetails(this.originTerritory, game.getCurrentPlayer().getNumArmiesAttacking()),
                    new DiceRollDetails(this.targetTerritory, numArmiesToDefendWith));
        }

        else{
            //deciding how many troops to defend with
            if(this.targetTerritory.getNArmies()>=2)
                numArmiesToDefendWith = 2;
            else
                numArmiesToDefendWith = 1;

            //deciding how many dice to roll for ai
            if(this.originTerritory.getNArmies()>=4)
                numArmiesToAttackWith = 3;
            else if(this.originTerritory.getNArmies()==3)
                numArmiesToAttackWith = 2;
            else
                numArmiesToAttackWith = 1;

            game.getState().performDiceRoll(new DiceRollDetails(this.originTerritory, numArmiesToAttackWith),
                    new DiceRollDetails(this.targetTerritory, numArmiesToDefendWith));
        }
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

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        originTerritoryLock = false;
        game.setState(new DefaultState(game));
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT SELECTED ATTACK TARGET TERRITORY STATE:");
        troopSelectionFragment = TroopSelectionFragment.newInstance(this.originTerritory, this.targetTerritory);
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