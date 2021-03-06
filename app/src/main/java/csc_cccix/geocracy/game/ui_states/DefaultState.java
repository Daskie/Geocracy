package csc_cccix.geocracy.game.ui_states;

import android.util.Log;

import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.world.Territory;

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

        SM.Game.UI.removeActiveBottomPaneFragment();

        SM.Game.getWorld().unselectTerritory();
        SM.Game.getWorld().untargetTerritory();
        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().highlightTerritories(SM.Game.getGameData().getCurrentPlayer().getOwnedTerritories());

//        SM.Game.UI.updateCurrentPlayerFragment();
        SM.Game.UI.removeOverlayFragment();
        SM.Game.UI.hideAllGameInteractionButtons();

        if (SM.Game.getControllingPlayer() instanceof HumanPlayer) {
            SM.Game.getActivity().runOnUiThread(() -> {
                SM.Game.UI.getEndTurnButton().show();
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
                SM.Game.nextPlayer();
                SM.Advance(new DefaultState(SM));
                break;
        }

        return false;
    }
}
