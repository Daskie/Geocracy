package csc_cccix.geocracy.game.ui_states;

/*import android.util.Log;

import csc_cccix.geocracy.backend.HumanPlayer;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.backend.world.Territory;

public class DefaultState extends IGameplayState {

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

        SM.game.UI.removeActiveBottomPaneFragment();

        SM.game.getWorld().unselectTerritory();
        SM.game.getWorld().untargetTerritory();
        SM.game.getWorld().unhighlightTerritories();
        SM.game.getWorld().highlightTerritories(SM.game.getGameData().getCurrentPlayer().getOwnedTerritories());

//        SM.game.UI.updateCurrentPlayerFragment();
        SM.game.UI.removeOverlayFragment();
        SM.game.UI.hideAllGameInteractionButtons();

        if (SM.game.getControllingPlayer() instanceof HumanPlayer) {
            SM.game.getActivity().runOnUiThread(() -> {
                SM.game.UI.getEndTurnButton().show();
            });
        }
    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");
    }

    @Override
    public boolean HandleEvent(GameEvent event) {
        super.HandleEvent(event);

        switch(event.action) {

            case TERRITORY_SELECTED:
                Log.d(TAG, "TERRITORY SELECTED");
                if (event.payload != null) SM.Advance(new SelectedTerritoryState(SM, (Territory) event.payload));
                break;

            case END_TURN_TAPPED:
                Log.d(TAG, "PLAYER ENDED THEIR TURN");
                SM.game.nextPlayer();
                SM.Advance(new DefaultState(SM));
                break;
        }

        return false;
    }
}*/
