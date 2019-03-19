package csc_cccix.geocracy.game.ui_states;

/*import android.util.Log;

import csc_cccix.geocracy.fragments.GameInfoFragment;
import csc_cccix.geocracy.game.IState;
import csc_cccix.geocracy.game.IStateMachine;

public class GameInfoVisibleState extends IGameplayState {

    private IState previousState;
    private final String TAG = "GAME_INFO_VISIBLE_STATE";

    public GameInfoVisibleState(IStateMachine SM, IState prev) {
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
        SM.game.UI.showOverlayFragment(GameInfoFragment.newInstance());
    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");
        SM.game.UI.removeOverlayFragment();
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch (event.action) {

        }

        return false;
    }
}*/
