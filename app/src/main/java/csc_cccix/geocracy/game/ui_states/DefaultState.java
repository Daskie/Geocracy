package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.game.IState;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.states.GameAction;
import csc_cccix.geocracy.states.GameEvent;
import csc_cccix.geocracy.world.Territory;

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

        SM.Game.removeActiveBottomPaneFragment();

        SM.Game.getWorld().unselectTerritory();
        SM.Game.getWorld().untargetTerritory();
        SM.Game.getWorld().unhighlightTerritories();
//        SM.Game.getWorld().highlightTerritories(SM.Game.getCurrentPlayer().getTerritories());



//        SM.Game.getActivity().runOnUiThread(() -> {
//            SM.Game.getActivity().updateCurrentPlayerFragment();
//            SM.Game.removeOverlayFragment();
//            SM.Game.getActivity().hideAllGameInteractionButtons();
//            SM.Game.getActivity().getEndTurnButton().show();
//        });

    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");
    }

    @Override
    public boolean HandleEvent(GameEvent event) {

        switch(event.action) {
            case SETTINGS_TAPPED:
                Log.d(TAG, "SETTINGS BTN TAPPED!");
                SM.Advance(new SettingsVisibleState(SM));
                break;

            case GAME_INFO_TAPPED:
                Log.d(TAG, "GAME INFO BTN TAPPED!");
                SM.Advance(new GameInfoVisibleState(SM));
                break;

            case TERRITORY_SELECTED:
                Log.d(TAG, "TERRITORY SELECTED");
                if (((Territory) event.payload) != null) SM.Advance(new SelectedTerritoryState(SM, (Territory) event.payload));
                break;

            default:
                Log.d(TAG, "UNREGISTERED ACTION TRIGGERED (DEFAULT)");
                break;
        }

        return false;
    }
}
