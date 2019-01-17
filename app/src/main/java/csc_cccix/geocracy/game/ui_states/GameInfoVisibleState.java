package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.GameInfoFragment;
import csc_cccix.geocracy.game.GameActivity;
import csc_cccix.geocracy.game.IState;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.states.GameEvent;

public class GameInfoVisibleState extends IState {

    private final String TAG = "GAME_INFO_VISIBLE_STATE";

    public GameInfoVisibleState(IStateMachine SM) {
        super(SM);
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.i(TAG, "INIT STATE");
        SM.Game.showOverlayFragment(GameInfoFragment.newInstance(SM.Game));
    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");
        SM.Game.removeOverlayFragment();
    }

    @Override
    public boolean HandleEvent(GameEvent event) {

        switch (event.action) {

            case CANCEL_TAPPED:
                Log.d(TAG, "CLOSE OVERLAY BTN TAPPED!");
                SM.Advance(new DefaultState(SM));

            default:
                Log.d(TAG, "UNREGISTERED ACTION TRIGGERED (DEFAULT)");
                break;
        }

        return false;
    }
}
