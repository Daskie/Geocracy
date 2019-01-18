package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.BattleResultsFragment;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.old_states.GameEvent;

public class BattleResultsState extends IGameplayState {

    private final String TAG = "BATTLE_RESULTS_STATE";

    public BattleResultsState(IStateMachine SM) {
        super(SM);
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.i(TAG, "INIT STATE");

//        SM.Game.showBottomPaneFragment(BattleResultsFragment.newInstance(this.originTerritory, this.targetTerritory, this.attackerArmiesLost, this.defenderArmiesLost));
//        game.getWorld().unhighlightTerritories();
//        game.getWorld().selectTerritory(this.originTerritory);
//        game.getWorld().highlightTerritory(this.targetTerritory);
//        game.getCameraController().targetTerritory(this.targetTerritory);
//        game.getActivity().runOnUiThread(() -> game.getActivity().hideAllGameInteractionButtons());
    }

    @Override
    public void DeinitializeState() {

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
