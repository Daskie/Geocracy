package cscCCCIX.geocracy.GameStates;

import cscCCCIX.geocracy.game.GameActivity;

public interface State {

    void handleInput(GameState states, GameData game, GameActivity game_act);

    void draw(GameData game);
}
