package cscCCCIX.geocracy.GameStates;

import cscCCCIX.geocracy.GameInputHandler;
import cscCCCIX.geocracy.game.Game;
import cscCCCIX.geocracy.game.GameActivity;

public class StartGameState implements State{
    public StartGameState(){


    }

    @Override
    public void handleInput(GameState states, GameData game, GameActivity game_act) {

//        GameActivity.uiLayout.addView(game.rollDiceButton);
//        GameActivity.frame.addView(GameActivity.uiLayout);
        game.state = states.choosing_num_armies;
    }

    @Override
    public void draw(GameData game) {

    }




    private void enter(Game game, GameInputHandler input){

    }
    private void exit(Game game, GameInputHandler input){

    }


}
