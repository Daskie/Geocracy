package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.fragments.BattleResultsFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;

public class BattleResultsState implements  GameState {

    private static final String TAG = "BATTLE_RESULTS_STATE";

    private Game game;
    private Territory originTerritory;
    private Territory targetTerritory;
    private int attackerArmiesLost = 0;
    private int defenderArmiesLost = 0;
    private DiceRollDetails attackerDetails;
    private DiceRollDetails defenderDetails;

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
        this.attackerDetails = attackerDetails;
        this.defenderDetails = defenderDetails;
    }

    public void battleCompleted(BattleResultDetails battleResultDetails) {
        Log.i(TAG, "INVALID ACTION: -> ALREADY IN BATTLE RESULTS STATE!");
        this.attackerArmiesLost = battleResultDetails.attackerArmiesLost;
        this.defenderArmiesLost = battleResultDetails.defenderArmiesLost;
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "INVALID ACTION: -> CANNOT UPDATE UNIT COUNT");
    }

    public void confirmAction() {
        Log.i(TAG, "USER CANCELED ACTION -> N/A");
    }

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> N/A"); }

    public void fortifyAction() { Log.i(TAG, "CANNOT ENABLE FORTIFY MODE"); }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        game.setState(new DefaultState(game));
        game.getState().initState();
    }

    public void initState() {
        Log.i(TAG, "INIT STATE");
        game.getActivity().showBottomPaneFragment(BattleResultsFragment.newInstance(this.originTerritory, this.targetTerritory, this.attackerArmiesLost, this.defenderArmiesLost));
        game.getWorld().unhighlightTerritories();
        game.getWorld().selectTerritory(this.originTerritory);
        game.getWorld().highlightTerritory(this.targetTerritory);
        game.getCameraController().targetTerritory(this.targetTerritory);
        game.getActivity().runOnUiThread(() -> game.getActivity().hideAllGameInteractionButtons());

        goToBattleResults();
    }

    private void goToBattleResults() {
        Territory originTerritory = this.originTerritory;
        Territory targetTerritory = this.targetTerritory;

        originTerritory.setNArmies(originTerritory.getNArmies()-this.attackerArmiesLost);
        targetTerritory.setNArmies(targetTerritory.getNArmies()-this.defenderArmiesLost);

        Player attacker = originTerritory.getOwner();
        Player defender = targetTerritory.getOwner();

        if(originTerritory.getNArmies()==0) {
            attacker.removeTerritory(originTerritory);
            originTerritory.setOwner(defender);
            defender.addTerritory(originTerritory);

            int numArmiesToMove = this.defenderDetails.unitCount;
            if(numArmiesToMove == targetTerritory.getNArmies())
                numArmiesToMove -= 1;
            originTerritory.setNArmies(numArmiesToMove);
            targetTerritory.setNArmies(targetTerritory.getNArmies()-numArmiesToMove);
        }
        if(targetTerritory.getNArmies()==0) {
            defender.removeTerritory(targetTerritory);
            targetTerritory.setOwner(attacker);
            attacker.addTerritory(targetTerritory);

            int numArmiesToMove = this.attackerDetails.unitCount;
            if(numArmiesToMove >= originTerritory.getNArmies())
                numArmiesToMove -= 1;
            targetTerritory.setNArmies(numArmiesToMove);
            originTerritory.setNArmies(originTerritory.getNArmies()-numArmiesToMove);
        }


    }

}