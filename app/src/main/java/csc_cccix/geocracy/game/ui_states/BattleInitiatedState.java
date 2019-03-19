package csc_cccix.geocracy.game.ui_states;

/*import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.ViewModelProviders;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.game.view_models.DiceRollViewModel;
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

        DiceRollViewModel viewModel = ViewModelProviders.of(SM.game.getActivity()).get(DiceRollViewModel.class);
        viewModel.setAttackerDiceRoll(attackingDiceRoll);
        viewModel.setDefenderDiceRoll(defendingDiceRoll);

        SM.game.UI.showBottomPaneFragment(DiceRollFragment.newInstance());

        SM.game.getWorld().unhighlightTerritories();
        SM.game.getWorld().selectTerritory(attackingDiceRoll.territory);
        SM.game.getWorld().highlightTerritory(defendingDiceRoll.territory);
        SM.game.getCameraController().targetTerritory(defendingDiceRoll.territory);
        SM.game.getActivity().runOnUiThread(() -> SM.game.UI.hideAllGameInteractionButtons());

        this.result = performDiceRoll();

        Completable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::goToBattleResultsState);

    }

    @Override
    public void DeinitializeState() {
        SM.game.UI.removeActiveBottomPaneFragment();
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
                if (attackingDiceRolls.get(i) != -1) attackerUnitLoss++;
            } else {
                if (attackingDiceRolls.get(i) != -1) defenderUnitLoss++;
            }

        }

        Util.clamp(attackerUnitLoss, 0, attackingDiceRoll.unitCount);
        Util.clamp(defenderUnitLoss, 0, defendingDiceRoll.unitCount);

        return new BattleResult(attackingDiceRoll.territory, attackingDiceRoll.unitCount, attackerUnitLoss, defendingDiceRoll.territory, defendingDiceRoll.unitCount, defenderUnitLoss);
    }

}*/
