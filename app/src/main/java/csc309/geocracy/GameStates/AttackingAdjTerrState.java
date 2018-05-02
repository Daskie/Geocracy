package csc309.geocracy.GameStates;
// You may only attack other territories that are adjacent
// to a territory you own or that are connected to a territory
// you own by a sea-lane. For example, you cannot attack India
// from the Eastern United States because
// the territories are not adjacent.

//Attack any number of times from any one of your territories
// to any adjacent territory. You may attack the same territory
// more than once, or you may attack different territories.
// You can attack the same territory from the same adjacent position,
// or you can attack it from different adjacent positions.
//Understand that attacking is optional.
// A player may decide not to attack at all during a turn,
// only deploying armies.

public class AttackingAdjTerrState implements State{
    public AttackingAdjTerrState(){

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