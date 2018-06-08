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

public class SetUpInitTerritoriesState implements GameState {

    private static final String TAG = "INIT_TERRITORIES_STATE";

    private Game game;
    private Territory territory;


    public SetUpInitTerritoriesState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        Log.i(TAG, "TERRITORY SELECTED");
        this.territory = territory;
    }

    public void selectTargetTerritory(Territory territory) {
        Log.i(TAG, "CANNOT SELECT TARGET TERRITORY");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails){
        Log.i(TAG, "INVALID ACTION: CANNOT PERFORM DICE ROLL");
    }

    public void battleCompleted(BattleResultDetails battleResultDetails){
        Log.i(TAG, "INVALID ACTION: CANNOT PERFORM BATTLE COMPLETED");
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
            game.firstPlayer(); // HUMAN PLAYER
            game.setState(new GainArmyUnitsState(game));
            for(Player player : game.getPlayers())
                player.addOrRemoveNArmiesToPool((int)Math.floor(3.0 * (float)game.getWorld().getNTerritories() / (float)game.getPlayers().length));

            game.getState().initState();
        }

    }

    public void enableAttackMode() {
        Log.i(TAG, "CANNOT ENABLE ATTACK MODE");
    }
    public void fortifyAction() { Log.i(TAG, "CANNOT ENABLE FORTIFY MODE"); }

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

    public void endTurn() { Log.i(TAG, "END TURN ACTION -> N/A"); }

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
            game.getActivity().runOnUiThread(() -> {
                game.getActivity().removeActiveBottomPaneFragment();
                if (this.territory.getOwner() == null) game.getActivity().getConfirmButton().show();
                game.getActivity().showBottomPaneFragment(TerritoryDetailFragment.newInstance(this.territory));
            });
        } else {
            game.getActivity().runOnUiThread(() -> Toasty.info(game.getActivity().getBaseContext(), "Please select a territory to acquire!", Toast.LENGTH_LONG).show());
        }

        game.getWorld().unhighlightTerritories();
        game.getWorld().highlightTerritories(new HashSet<>(game.getWorld().getUnoccupiedTerritories()));


    }

}
