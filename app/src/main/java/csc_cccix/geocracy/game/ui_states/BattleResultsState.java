package csc_cccix.geocracy.game.ui_states;

/*import android.util.Log;

import androidx.lifecycle.ViewModelProviders;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.game.view_models.BattleResultsViewModel;

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

        ViewModelProviders.of(SM.game.getActivity()).get(BattleResultsViewModel.class).setBattleResult(result);

        SM.gameUI.showBottomPaneFragment(BattleResultsFragment.newInstance());
        SM.game.getWorld().unhighlightTerritories();
        SM.game.getWorld().selectTerritory(result.attackingTerritory);
        SM.game.getWorld().highlightTerritory(result.defendingTerritory);
        SM.game.getCameraController().targetTerritory(result.defendingTerritory);
        SM.game.getActivity().runOnUiThread(() -> SM.game.UI.hideAllGameInteractionButtons());

        if (SM.game.currentPlayerIsHuman()) {
            SM.game.getActivity().runOnUiThread(() -> SM.game.UI.setConfirmButtonVisibilityAndActiveState(true, true));
        }

    }

    @Override
    public void DeinitializeState() {
        SM.game.UI.removeActiveBottomPaneFragment();
        SM.game.getActivity().runOnUiThread(() -> SM.game.UI.hideAllGameInteractionButtons());
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
}*/
