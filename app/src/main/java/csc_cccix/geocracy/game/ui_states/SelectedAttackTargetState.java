package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.states.GameEvent;
import csc_cccix.geocracy.world.Territory;

public class SelectedAttackTargetState extends IGameplayState {

    private final String TAG = "SELECTED_ATTACK_TARGET_STATE";

    private Territory attackingTerritory;
    private Territory defendingTerritory;

    public SelectedAttackTargetState(IStateMachine SM, Territory attackingTerritory, Territory defendingTerritory) {
        super(SM);
        this.attackingTerritory = attackingTerritory;
        this.defendingTerritory = defendingTerritory;
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.d(TAG, "INIT STATE");
    }

    @Override
    public void DeinitializeState() {
        Log.d(TAG, "DEINIT STATE");
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

            case CONFIRM_TAPPED:

                break;

            case CANCEL_TAPPED:
                Log.d(TAG, "CANCELED!");
                SM.Advance(new DefaultState(SM));
                break;

        }

        return false;
    }
}
