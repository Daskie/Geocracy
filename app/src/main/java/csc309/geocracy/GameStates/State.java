package csc309.geocracy.GameStates;

public interface State {

    void handleInput(GameData game, GameInputHandler input);

    void draw(GameData game);
}
