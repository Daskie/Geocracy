package cscCCCIX.geocracy.GameStates;

import cscCCCIX.geocracy.game.GameActivity;
import cscCCCIX.geocracy.game.GameData;
import cscCCCIX.geocracy.states.GameState;

public interface State {

    void handleInput(GameState states, GameData game, GameActivity game_act);

    void draw(GameData game);
}
