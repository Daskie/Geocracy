package csc_cccix.geocracy.states;

import android.util.Log;

import csc_cccix.geocracy.fragments.BattleResultsFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;

public class BattleResultsState extends GameState {

    private Territory originTerritory;
    private Territory targetTerritory;
    private int attackerArmiesLost = 0;
    private int defenderArmiesLost = 0;
    private DiceRollDetails attackerDetails;
    private DiceRollDetails defenderDetails;

    public BattleResultsState(Game game) {
        TAG = "BATTLE_RESULTS_STATE";
        this.game = game;
    }

    public void selectPrimaryTerritory(Territory territory) {
        Log.i(TAG, "SETTING ORIGIN TERRITORY");
        this.originTerritory = territory;
    }
    public void selectSecondaryTerritory(Territory territory) {
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
        Territory oTerritory = this.originTerritory;
        Territory tTerritory = this.targetTerritory;

        oTerritory.setNArmies(oTerritory.getNArmies()-this.attackerArmiesLost);
        tTerritory.setNArmies(tTerritory.getNArmies()-this.defenderArmiesLost);

        Player attacker = oTerritory.getOwner();
        Player defender = tTerritory.getOwner();

        if(oTerritory.getNArmies()==0) {
            attacker.removeTerritory(oTerritory);
            oTerritory.setOwner(defender);
            defender.addTerritory(oTerritory);

            int numArmiesToMove = this.defenderDetails.unitCount;
            if(numArmiesToMove == tTerritory.getNArmies())
                numArmiesToMove -= 1;
            oTerritory.setNArmies(numArmiesToMove);
            tTerritory.setNArmies(tTerritory.getNArmies()-numArmiesToMove);
        }
        if(tTerritory.getNArmies()==0) {
            defender.removeTerritory(tTerritory);
            tTerritory.setOwner(attacker);
            attacker.addTerritory(tTerritory);

            int numArmiesToMove = this.attackerDetails.unitCount;
            if(numArmiesToMove >= oTerritory.getNArmies())
                numArmiesToMove -= 1;
            tTerritory.setNArmies(numArmiesToMove);
            oTerritory.setNArmies(oTerritory.getNArmies()-numArmiesToMove);
        }


    }

}