package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.BattleResultsFragment;
import csc_cccix.geocracy.game.HumanPlayer;
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

        // If the defending territory now has 0 units, attacking territory takes over
        if (result.defendingTerritory.getNArmies() < 1) {

            result.defendingTerritory.getOwner().removeTerritory(result.defendingTerritory);
            result.defendingTerritory.setOwner(result.attackingTerritory.getOwner());
            result.defendingTerritory.getOwner().addTerritory(result.defendingTerritory);

            result.defendingTerritory.setNArmies(result.attackingUnitCount - result.attackingUnitLoss);

        }

        SM.Game.UI.showBottomPaneFragment(BattleResultsFragment.newInstance(result));
        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().selectTerritory(result.attackingTerritory);
        SM.Game.getWorld().highlightTerritory(result.defendingTerritory);
        SM.Game.getCameraController().targetTerritory(result.defendingTerritory);
        SM.Game.getActivity().runOnUiThread(() -> SM.Game.UI.hideAllGameInteractionButtons());

        if (SM.Game.currentPlayerIsHuman()) {
            SM.Game.getActivity().runOnUiThread(() -> SM.Game.UI.setConfirmButtonVisibilityAndActiveState(true, true));
        }

    }

    @Override
    public void DeinitializeState() {
        SM.Game.UI.removeActiveBottomPaneFragment();
        SM.Game.getActivity().runOnUiThread(() -> SM.Game.UI.hideAllGameInteractionButtons());
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
