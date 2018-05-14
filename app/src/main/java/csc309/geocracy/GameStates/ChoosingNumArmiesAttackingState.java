package csc309.geocracy.GameStates;

//
//Decide how many armies you are going to use in your attack.
// Because your territory must be occupied at all times,
// you must leave at least one army behind.
// The number of armies you attack with will determine
// how many dice you get to roll when you square off the
// opponent whose territory you are defending.
//1 army = 1 die
//2 armies = 2 dice
//3 armies = 3 dice

import csc309.geocracy.fragments.TroopSelectionFragment;
import csc309.geocracy.game.Game;
import csc309.geocracy.GameInputHandler;
import csc309.geocracy.game.GameActivity;


public class ChoosingNumArmiesAttackingState implements State{


    public ChoosingNumArmiesAttackingState(){

    }

    @Override
    public void handleInput(GameState states, GameData game, GameActivity game_act){
//        game_act.showBottomPaneFragment(new TroopSelectionFragment());

    }

    @Override
    public void draw(GameData game) {

    }
    void enter(Game game, GameInputHandler input){

    }
    void exit(Game game, GameInputHandler input){

    }
}
