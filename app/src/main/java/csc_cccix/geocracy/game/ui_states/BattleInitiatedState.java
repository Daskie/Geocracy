package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import csc_cccix.geocracy.fragments.DiceRollFragment;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.old_states.GameEvent;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BattleInitiatedState extends IGameplayState {

    private final String TAG = "BATTLE_INITIATED_STATE";

    private DiceRoll attackingDiceRoll;
    private DiceRoll defendingDiceRoll;

    public BattleInitiatedState(IStateMachine SM, DiceRoll attackingDiceRoll, DiceRoll defendingDiceRoll) {
        super(SM);
        this.attackingDiceRoll = attackingDiceRoll;
        this.defendingDiceRoll = defendingDiceRoll;
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {

        Log.i(TAG, "INIT STATE");

        SM.Game.showBottomPaneFragment(DiceRollFragment.newInstance(attackingDiceRoll, defendingDiceRoll));

        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().selectTerritory(attackingDiceRoll.territory);
        SM.Game.getWorld().highlightTerritory(defendingDiceRoll.territory);
        SM.Game.getCameraController().targetTerritory(defendingDiceRoll.territory);
        SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().hideAllGameInteractionButtons());

        Completable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::goToBattleResultsState);

    }

    @Override
    public void DeinitializeState() {
        SM.Game.removeActiveBottomPaneFragment();
    }

    // TODO: probably don't need to do anything here... will remove if so
    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);
        return false;
    }

    private void goToBattleResultsState() {
        SM.Advance(new BattleResultsState(SM));
    }

//    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails) {
//        Log.i(TAG, "PERFORMING DICE ROLL");
//        this.attackerDetails = attackerDetails;
//        this.defenderDetails = defenderDetails;
//        roll(attackerDetails.unitCount, defenderDetails.unitCount);
//
//        Player attacker = this.originTerritory.getOwner();
//        Player defender = this.targetTerritory.getOwner();
//
//        attacker.sortDie();
//        defender.sortDie();
//
//        for(int i = 2; i > 0; i--) {
//            boolean end = checkLosers(attacker, defender, i);
//            if(end)
//                break;
//        }
//
//        resultsToString();
//    }
//
//    private void roll(int attackerNumDie, int defenderNumDie){
//        Random rGen = new Random();
//        for(int i = 0; i < attackerNumDie; i++)
//            this.originTerritory.getOwner().setDie(i, (rGen.nextInt(6)) + 1);
//        for(int i = 0; i < defenderNumDie; i++)
//            this.targetTerritory.getOwner().setDie(i, (rGen.nextInt(6)) + 1);
//    }
//
//    private boolean checkLosers(Player attacker, Player defender, int index){
//        int attackerDie = attacker.getDie()[index];
//        int defenderDie = defender.getDie()[index];
//        if(attackerDie==-1 || defenderDie==-1)
//            return true;
//        else if(attackerDie<=defenderDie)
//            attackerArmiesLost++;
//        else
//            defenderArmiesLost++;
//        return false;
//    }
}
