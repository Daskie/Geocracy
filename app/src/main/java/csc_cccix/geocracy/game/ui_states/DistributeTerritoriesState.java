package csc_cccix.geocracy.game.ui_states;

import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;

import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.IState;
import csc_cccix.geocracy.game.IStateMachine;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.states.GameEvent;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class DistributeTerritoriesState extends IState {

    private final String TAG = "DISTRIBUTE_TERRITORIES_STATE";

    Territory selectedTerritory = null;

    public DistributeTerritoriesState(IStateMachine SM) {
        super(SM);
    }

    @Override
    public String GetName() {
        return TAG;
    }

    @Override
    public void InitializeState() {
        Log.i(TAG, "INIT STATE");

//        SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().hideAllGameInteractionButtons());

        if (selectedTerritory != null) {
            SM.Game.getWorld().selectTerritory(selectedTerritory);
            SM.Game.getWorld().unhighlightTerritories();
            SM.Game.getCameraController().targetTerritory(selectedTerritory);
            if (selectedTerritory.getOwner() == null) {
                SM.Game.getActivity().runOnUiThread(() ->  SM.Game.getActivity().getConfirmButton().show());
            }
            SM.Game.getActivity().runOnUiThread(() -> {
                SM.Game.getActivity().removeActiveBottomPaneFragment();
                SM.Game.getActivity().showBottomPaneFragment(TerritoryDetailFragment.newInstance(selectedTerritory));
            });
        } else {
            SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "Please select a territory to acquire!", Toast.LENGTH_LONG).show());
        }

        SM.Game.getWorld().unhighlightTerritories();
        SM.Game.getWorld().highlightTerritories(new HashSet<>(SM.Game.getWorld().getUnoccupiedTerritories()));
    }

    @Override
    public void DeinitializeState() {
        Log.i(TAG, "DEINIT STATE");

        SM.Game.removeActiveBottomPaneFragment();
        SM.Game.getActivity().runOnUiThread(() ->  SM.Game.getActivity().getConfirmButton().hide());

    }

    @Override
    public boolean HandleEvent(GameEvent event) {

        switch (event.action) {

            case SETTINGS_TAPPED:
                Log.d(TAG, "SETTINGS BTN TAPPED!");
                SM.Advance(new SettingsVisibleState(SM, this));
                break;

            case GAME_INFO_TAPPED:
                Log.d(TAG, "GAME INFO BTN TAPPED!");
                SM.Advance(new GameInfoVisibleState(SM, this));
                break;

            case CONFIRM_TAPPED:

                if (selectedTerritory != null) {

                    //illegal territory selection for setting up territories
                    if(selectedTerritory.getOwner() != null){

                        if(SM.Game.getCurrentPlayer() instanceof HumanPlayer) {
                            SM.Game.getActivity().runOnUiThread(() -> Toasty.info(SM.Game.getActivity().getBaseContext(), "This territory is already taken! Choose another territory.", Toast.LENGTH_LONG).show());
                        }

                    } else {
                        addToSelectedTerritoryUnitCount(1);
                        SM.Game.getActivity().runOnUiThread(() -> SM.Game.removeActiveBottomPaneFragment());
                    }

                }

                break;

            case TERRITORY_SELECTED:

                if (((Territory) event.payload) != null) {
                    selectedTerritory = (Territory) event.payload;

                    SM.Game.getWorld().selectTerritory(selectedTerritory);
                    SM.Game.getWorld().unhighlightTerritories();
                    SM.Game.getCameraController().targetTerritory(selectedTerritory);
                    if (selectedTerritory.getOwner() == null) {
                        SM.Game.getActivity().runOnUiThread(() -> SM.Game.getActivity().getConfirmButton().show());
                    }
                    SM.Game.getActivity().runOnUiThread(() -> {
                        SM.Game.getActivity().removeActiveBottomPaneFragment();
                        SM.Game.getActivity().showBottomPaneFragment(TerritoryDetailFragment.newInstance(selectedTerritory));
                    });

                }

                break;


            default:

                Log.d(TAG, "UNREGISTERED ACTION TRIGGERED (DEFAULT)");

                break;
        }

        return false;
    }

    public void addToSelectedTerritoryUnitCount(int amount) {

        Log.i(TAG, "ADDING TERRITORY TO PLAYERS INITIAL TERRITORIES");
        Player currentPlayer = SM.Game.getCurrentPlayer();
        selectedTerritory.setOwner(currentPlayer);
        selectedTerritory.setNArmies(amount);
        currentPlayer.addOrRemoveNArmies(1);

        Log.i(TAG, currentPlayer.getName() + " ADDED " + selectedTerritory.getTerritoryName());

        SM.Game.nextPlayer();

        // If all territories occupied, exit state
        if(SM.Game.getWorld().allTerritoriesOccupied()) {
            SM.Game.setFirstPlayer(); // HUMAN PLAYER
            SM.Advance(new DefaultState(SM));

//            for(Player player : game.getPlayers())
//                player.addOrRemoveNArmiesToPool((int)Math.floor(3.0 * (float)game.getWorld().getNTerritories() / (float)game.getPlayers().length));

//            game.getState().initState();
        }

    }

}
