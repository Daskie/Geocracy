package csc_cccix.geocracy.game;

import java.io.Serializable;

public class GameData implements Serializable {

    public long startT; // time the game was started
    public long lastT; // time last frame happened

    public Player[] players;
    public int currentPlayer;

    public int gameTurn;

    public GameData() {
        gameTurn = 0;
    }

}
