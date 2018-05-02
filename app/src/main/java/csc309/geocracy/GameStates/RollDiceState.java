package csc309.geocracy.GameStates;

//
//Roll the dice. You roll up to three red dice,
// depending on your troop size. The defending player rolls
// the same number of white dice as the number of troops
// in their defending territory, with a maximum of two.
//Match up the highest red die with the highest white die,
// and match the second highest red die with the second
// highest white die. If there is only one white die,
// only match up the highest red die with the white die.
//Remove one of your pieces from the attacking territory
// if the white die is higher or equal to its corresponding red die.
// Remove one of your opponent’s pieces from the defending
// territory if the red die is higher to its corresponding white die.

import csc309.geocracy.game.Game;

public class RollDiceState implements State{
    public RollDiceState(){

    }

    public void handleInput(GameData game, GameInputHandler input){

    }

    public void draw(GameData game){

    }
    public void enter(GameData game, GameInputHandler input){

    }
    public void exit(GameData game, GameInputHandler input){

    }
}