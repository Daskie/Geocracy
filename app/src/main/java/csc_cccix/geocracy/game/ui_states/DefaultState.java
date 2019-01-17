package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.game.IState;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.states.GameAction;

public class DefaultState extends IState {

    private final String TAG = "DEFAULT_STATE";

    public DefaultState(IStateMachine SM) {
        super(SM);
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.i(TAG, "INIT STATE");
    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");
    }

    @Override
    public boolean HandleAction(GameAction action) {
        return false;
    }
}
