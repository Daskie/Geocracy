package csc309.geocracy.GameStates;

//At the beginning of each turn, players receive more armies.
// The number of armies is determined by:
//      -   The number of territories you own. For every three countries,
//          the player gets one army. For example, if you had 11 countries,
//          you would receive 3 armies; if you had 22 countries,
//          you would receive 7 armies.
//      -   Turning in cards. Cards can be turned in when you have a three of a kind
//          (e.g. all three cards have artillery pictures) or all three types of armies
//          (soldier, cavalry, artillery). For the first set of cards you turn in,
//          you receive 4 armies; 6 for the second; 8 for the third; 10 for the fourth;
//          12 for the fifth; 15 for the sixth; and for every additional set thereafter,
//          5 more armies than the previous set turned in. If you have 5 or more Risk cards
//          at the beginning of a turn, you must turn at least one set of them in.
//      -   Owning all the territories of a continent. For each continent that you
//          completely dominate (no other enemy armies are present), you receive reinforcements.
//          You receive 3 armies for Africa, 7 armies for Asia, 2 armies for Australia,
//          5 armies for Europe, 5 armies for North America and 2 armies for South America.

public class GainArmiesState implements State{
    public GainArmiesState(){

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