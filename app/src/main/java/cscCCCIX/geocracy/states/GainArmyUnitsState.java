package cscCCCIX.geocracy.states;

import android.widget.Toast;

import cscCCCIX.geocracy.EventBus;
import cscCCCIX.geocracy.Util;
import cscCCCIX.geocracy.fragments.DistributeTroopsDetailFragment;
import cscCCCIX.geocracy.game.Game;
import cscCCCIX.geocracy.game.GameActivity;
import cscCCCIX.geocracy.game.Player;
import cscCCCIX.geocracy.game.UIEvent;
import cscCCCIX.geocracy.world.Territory;
import es.dmoral.toasty.Toasty;

public class GainArmyUnitsState implements GameState {

    private Game game;

    private Territory territory;

    private static GameActivity parent;

    private int unitsToDistribute;
    private final int MAX_UNITS = 10; // whats the max?
    private final int MIN_UNITS = 1; // whats the max?


    public GainArmyUnitsState(Game game, GameActivity parent) {
        this.game = game;
        this.parent = parent;
    }

    public void selectOriginTerritory(Territory territory) {

    }

    public void selectTargetTerritory(Territory territory) {
        System.out.println("GAIN ARMIES STATE: SELECTING TARGET TERRITORY TO ADD/REMOVE UNITS");

        if (territory != null) {
            //illegal territory selection for assigning units
            if(territory.getOwner() != game.players[game.currentPlayer]){
                this.parent.runOnUiThread(() -> {
                    Toasty.info(parent.getBaseContext(), "Cannot assign units to another players territory!.", Toast.LENGTH_LONG).show();
                });
                return;
            }

            this.territory = territory;

            game.cameraController.targetTerritory(territory);
            EventBus.publish("UI_EVENT", UIEvent.SHOW_UPDATE_UNITS_MODE_BUTTONS);
            parent.showBottomPaneFragment(DistributeTroopsDetailFragment.newInstance(this.territory));
        }
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails){

    }
    public void battleCompleted(BattleResultDetails battleResultDetails){

    }

    public void enableAttackMode() {
        System.out.println("GAIN ARMIES STATE: CANNOT ENABLE ATTACK MODE");
    }

    public void addToSelectedTerritoryUnitCount(int amount) {
        if (this.territory != null) {
            System.out.println("GAIN ARMIES STATE: UPDATING UNITS IN TERRITORY BY " + amount);
            int clampedNArmies = Util.clamp(territory.getNArmies() + amount, MIN_UNITS, MAX_UNITS);
            this.territory.setNArmies(clampedNArmies);
            System.out.println("PLAYER" + game.currentPlayer + " UPDATED UNITS AT " + territory.getTerritoryName());
        } else {
            System.out.println("GAIN ARMIES STATE: CANNOT UPDATE UNIT COUNT, NO TERRITORY SELECTED");
        }
    }

    public void cancelAction() {
        System.out.println("GAIN ARMIES STATE: USER CANCELED ACTION -> DESELECT TERRITORY IF SELECTED");
        this.territory = null;
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }


    public void initState() {
        System.out.println("INIT GAIN ARMIES STATE:");
        game.activity.removeActiveBottomPaneFragment();
        Player currentPlayer = game.players[game.currentPlayer];
        this.unitsToDistribute = currentPlayer.getBonus();
        game.getWorld().unhighlightTerritories();
        game.getWorld().unselectTerritory();
        game.getWorld().highlightTerritories(currentPlayer.getTerritories());
        System.out.println(currentPlayer);
        System.out.println("HAS " + this.unitsToDistribute + " UNITS TO DISTRIBUTE");
        currentPlayer.addNArmies(this.unitsToDistribute);
        EventBus.publish("UI_EVENT", UIEvent.HIDE_UPDATE_UNITS_MODE_BUTTONS);
    }
}
