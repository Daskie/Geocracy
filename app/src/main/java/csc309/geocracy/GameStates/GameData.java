package csc309.geocracy.GameStates;

import android.widget.Button;

public class GameData {
    public State state;
    public Button rollDiceButton;
    public Button endTurnButton;
    public Button settingsButton;
    public Button exitButton;
    private Button backButton;
    private Button okButton;
    private Button doneButton;
    private Button attackButton;
    private Button fortTerrButton;

    public GameData(GameState game_state){
        this.state = game_state.start;
    }
}
