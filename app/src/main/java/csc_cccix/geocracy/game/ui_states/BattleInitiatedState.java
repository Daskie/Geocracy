package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.fragments.DiceRollFragment;
import csc_cccix.geocracy.game.IStateMachine;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BattleInitiatedState extends IGameplayState {

    private final String TAG = "BATTLE_INITIATED_STATE";

    private DiceRoll attackingDiceRoll;
    private DiceRoll defendingDiceRoll;

    private BattleResult result = null;

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

        this.result = performDiceRoll();

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
        SM.Advance(new BattleResultsState(SM, result));
    }

    public BattleResult performDiceRoll() {
        Log.i(TAG, "PERFORMING DICE ROLL");

        List<Integer> attackingDiceRolls = attackingDiceRoll.getRolledDiceValues();
        List<Integer> defendingDiceRolls = defendingDiceRoll.getRolledDiceValues();

        int attackerUnitLoss = 0;
        int defenderUnitLoss = 0;

        // for each pair of dice rolled (or default -1)
        for (int i = 0; i < 3; i++) {

            if (attackingDiceRolls.get(i) <= defendingDiceRolls.get(i)) {
                attackerUnitLoss++;
            } else {
                defenderUnitLoss++;
            }

        }

        Util.clamp(attackerUnitLoss, 0, attackingDiceRoll.unitCount);
        Util.clamp(defenderUnitLoss, 0, defendingDiceRoll.unitCount);

        return new BattleResult(attackingDiceRoll.territory, attackerUnitLoss, defendingDiceRoll.territory, defenderUnitLoss);
    }

}
