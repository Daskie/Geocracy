package csc309.geocracy.states;

import csc309.geocracy.game.Game;

import csc309.geocracy.world.Territory;
import csc309.geocracy.game.Player;

public class GainArmyUnitsState implements GameState {

    private Game game;

    public GainArmyUnitsState(Game game) {
        this.game = game;
    }

    public void selectOriginTerritory(Territory territory) {
        System.out.println("GAIN ARMIES STATE:CANNOT SELECT ORIGIN TERRITORY");

    }

    public void selectTargetTerritory(Territory territory) {
        System.out.println("GAIN ARMIES STATE: CANNOT SELECT TARGET TERRITORY");
    }

    public void performDiceRoll(DiceRollDetails attackerDetails, DiceRollDetails defenderDetails){

    }
    public void battleCompleted(BattleResultDetails battleResultDetails){

    }

    public void enableAttackMode() {
        System.out.println("GAIN ARMIES STATE: CANNOT ENABLE ATTACK MODE");
    }

    public void cancelAction() {
        System.out.println("GAIN ARMIES STATE: USER CANCELED ACTION -> NULL ACTION");
    }


    public void initState() {
        System.out.println("INIT GAIN ARMIES STATE:");
        game.activity.removeActiveBottomPaneFragment();

        for(Player player : game.players){
            int numInitArmies = player.getTerritories().size()/3;
            if(numInitArmies < 3)
                numInitArmies =3;
            player.addNArmies(numInitArmies);

            System.out.println(player.name + " ADDED " + numInitArmies + " ARMIES.");
        }

        game.setState(game.SelectedTerritoryState);

    }
}
