package csc309.geocracy.GameStates;

import android.widget.Button;
import android.content.Context;


public class GameData{
    public State state;
    public Button rollDiceButton;
    public Button endTurnButton;
    public Button settingsButton;
    public Button exitButton;
    private Button backButton;
    public Button okButton;
    public Button doneButton;
    public Button attackButton;
    public Button fortTerrButton;
    public Button startGameButton;


    public GameData(GameState game_state){

        this.state = game_state.start;

    }


    public void handleInput(GameState states){
        this.state.handleInput(states, this);
    }


    public void draw(){
        this.state.draw(this);
    }

    public void setUpButtons(){
        this.startGameButton.setText("START GAME!");

    }
}
