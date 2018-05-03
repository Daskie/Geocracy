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

public class ChoosingNumArmiesAttackingState implements State{
    public ChoosingNumArmiesAttackingState(){

    }

    public void handleInput(GameData game, GameInputHandler input){

    }

    public void draw(GameData game){
//        View view = inflater.inflate(R.layout.troop_selection, container, false);

    }
    public void enter(GameData game, GameInputHandler input){

    }
    public void exit(GameData game, GameInputHandler input){

    }
}
