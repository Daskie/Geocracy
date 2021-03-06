package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.fragments.SettingsFragment;
import csc_cccix.geocracy.game.GameActivity;
import csc_cccix.geocracy.game.IState;
import csc_cccix.geocracy.game.IStateMachine;

public class SettingsVisibleState extends IGameplayState {

    private final String TAG = "SETTINGS_VISIBLE_STATE";
    private IState previousState;

    public SettingsVisibleState(IStateMachine SM, IState prev) {
        super(SM);
        previousState = prev;
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.i(TAG, "INIT STATE");
        SM.Game.UI.showOverlayFragment(new SettingsFragment());
    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");
        SM.Game.UI.removeOverlayFragment();
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

        }

        return false;
    }
}
