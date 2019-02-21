package csc_cccix.geocracy.game;

public class GameData {

    private boolean outOfGameSetUp;

    final private Player[] players;
    private int currentPlayerIndex;

    public GameData(Player[] players) {
        this.players = players;
        this.outOfGameSetUp = false;
        setFirstPlayer();
    }

    public Player[] getPlayers() { return players; }
    public Player getCurrentPlayer() { return players[currentPlayerIndex]; }
//    public int getCurrentPlayerIndex() { return currentPlayerIndex; }

    public boolean getGameStatus(){ return outOfGameSetUp; }

    public void setCurrentPlayerIndex(int idx) {
        if (idx < 0 || idx >= players.length) return;
        this.currentPlayerIndex = idx;
    }

    public void setFirstPlayer() {
        setCurrentPlayerIndex(0);
    }

    public void nextPlayerIndex() {
        setCurrentPlayerIndex((currentPlayerIndex + 1) % players.length);
    }

}
