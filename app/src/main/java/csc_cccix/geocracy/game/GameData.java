package csc_cccix.geocracy.game;

import android.graphics.Color;

import java.io.Serializable;

public class GameData implements Serializable {

    public long startT; // time the game was started
    public long lastT; // time last frame happened

    public int mainPlayerColor;
    public Player[] players;
    public int currentPlayer;

    public int gameTurn;

    public GameData() {
        gameTurn = 0;
        mainPlayerColor = Color.RED;
    }

}
