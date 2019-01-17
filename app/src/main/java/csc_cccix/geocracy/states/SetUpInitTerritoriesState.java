package csc_cccix.geocracy.states;

import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;

import csc_cccix.geocracy.fragments.TerritoryDetailFragment;
import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.HumanPlayer;
import csc_cccix.geocracy.game.Player;
import csc_cccix.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class SetUpInitTerritoriesState extends IGameState {

    public SetUpInitTerritoriesState(Game game) {
        TAG = "SET_UP_INIT_TERRITORIES_STATE";
        this.game = game;
    }

    public void selectPrimaryTerritory(Territory territory) {
        Log.i(TAG, "PRIMARY TERRITORY SELECTED");
        this.territory = territory;
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        Log.i(TAG, "ADDING TERRITORY TO PLAYERS INITIAL TERRITORIES");
        Player currentPlayer = game.getCurrentPlayer();
        territory.setOwner(currentPlayer);
        territory.setNArmies(amount);
        currentPlayer.addOrRemoveNArmies(1);

        Log.i(TAG, currentPlayer.getName() + " ADDED " + territory.getTerritoryName());

        game.nextPlayer();

        // If all territories occupied, exit state
        if(game.getWorld().allTerritoriesOccupied()) {
            game.setFirstPlayer(); // HUMAN PLAYER
            game.setState(new GainUnitsState(game));
            for(Player player : game.getPlayers())
                player.addOrRemoveNArmiesToPool((int)Math.floor(3.0 * (float)game.getWorld().getNTerritories() / (float)game.getPlayers().length));

            game.getState().initState();
        }

    }

    public void confirmAction() {

        //illegal territory selection for setting up territories
        if(this.territory.getOwner() != null){
            if(game.getCurrentPlayer() instanceof HumanPlayer) {
                game.getActivity().runOnUiThread(() -> Toasty.info(game.getActivity().getBaseContext(), "This territory is already taken! Choose another territory.", Toast.LENGTH_LONG).show());
            }
            return;
        }

        addToSelectedTerritoryUnitCount(1);
        game.getActivity().runOnUiThread(() -> game.getActivity().removeActiveBottomPaneFragment());
    }

    public void cancelAction() {
        Log.i(TAG, "USER CANCELED ACTION -> ENTER DEFAULT STATE");
        this.territory = null;
        game.getActivity().runOnUiThread(() -> {
            game.getActivity().removeActiveBottomPaneFragment();
            game.getActivity().getConfirmButton().hide();
        });
    }


    public void initState() {
        Log.i(TAG, "INIT STATE");

        game.getActivity().runOnUiThread(() -> game.getActivity().hideAllGameInteractionButtons());

        if (this.territory != null) {
            game.getWorld().selectTerritory(this.territory);
            game.getWorld().unhighlightTerritories();
            game.getCameraController().targetTerritory(this.territory);
            if (this.territory.getOwner() == null) {
                game.getActivity().runOnUiThread(() -> game.getActivity().getConfirmButton().show());
            }
            game.getActivity().runOnUiThread(() -> {
                game.getActivity().removeActiveBottomPaneFragment();
                game.getActivity().showBottomPaneFragment(TerritoryDetailFragment.newInstance(this.territory));
            });
        } else {
            game.getActivity().runOnUiThread(() -> Toasty.info(game.getActivity().getBaseContext(), "Please select a territory to acquire!", Toast.LENGTH_LONG).show());
        }

        game.getWorld().unhighlightTerritories();
        game.getWorld().highlightTerritories(new HashSet<>(game.getWorld().getUnoccupiedTerritories()));


    }

}
