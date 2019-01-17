package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.game.GameActivity;
import csc_cccix.geocracy.game.IState;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.states.GameAction;

public class SettingsVisibleState extends IState {

    private final String TAG = "SETTINGS_VISIBLE_STATE";

    public SettingsVisibleState(IStateMachine SM) {
        super(SM);
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.i(TAG, "INIT STATE");
        SM.Game.getActivity().showOverlayFragment(GameActivity.settingsFragment);
    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");
        SM.Game.getActivity().removeActiveOverlayFragment();
    }

    @Override
    public boolean HandleAction(GameAction action) {

        switch (action) {

            case CLOSE_OVERLAY_TAPPED:



            default:
                Log.d(TAG, "UNREGISTERED ACTION TRIGGERED (DEFAULT)");
        }

        return false;
    }
}
