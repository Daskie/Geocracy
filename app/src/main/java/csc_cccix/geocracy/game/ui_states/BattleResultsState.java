package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.BattleResultsFragment;
import csc_cccix.geocracy.game.IStateMachine;

public class BattleResultsState extends IGameplayState {

    private final String TAG = "BATTLE_RESULTS_STATE";

    private BattleResult result;

    public BattleResultsState(IStateMachine SM, BattleResult result) {
        super(SM);
        this.result = result;
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.i(TAG, "INIT STATE");

        result.attackingTerritory.setNArmies(result.attackingTerritory.getNArmies()-result.attackingUnitLoss);
        result.defendingTerritory.setNArmies(result.defendingTerritory.getNArmies()-result.defendingUnitLoss);

        SM.Game.showBottomPaneFragment(BattleResultsFragment.newInstance(result.attackingTerritory, result.defendingTerritory, result.attackingUnitLoss, result.defendingUnitLoss));
        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().selectTerritory(result.attackingTerritory);
        SM.Game.getWorld().highlightTerritory(result.defendingTerritory);
        SM.Game.getCameraController().targetTerritory(result.defendingTerritory);
        SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().hideAllGameInteractionButtons());
    }

    @Override
    public void DeinitializeState() {
        SM.Game.removeActiveBottomPaneFragment();
        SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().hideAllGameInteractionButtons());
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {
            case CONFIRM_TAPPED:
                SM.Advance(new DefaultState(SM));
            case CANCEL_TAPPED:
                SM.Advance(new DefaultState(SM));
        }

        return false;
    }
}
