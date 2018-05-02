package csc309.geocracy.GameStates;

import android.widget.Button;

public class GameData {
    public State state;
    public Button rollDiceButton;
    public Button endTurnButton;
    public Button settingsButton;
    public Button exitButton;

    public GameData(GameState game_state){
        this.state = game_state.start;
    }
}
