package csc_cccix.geocracy.game;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.lifecycle.ViewModelProviders;
import csc_cccix.geocracy.game.view_models.GameInfoViewModel;
import csc_cccix.geocracy.game.view_models.GameViewModel;

public class GameData {

    private Game game;

    private boolean outOfGameSetUp;

    final private Long worldSeed;
    final private Player[] players;
    private int currentPlayerIndex;

    public GameData(Game game, Player[] players) {
        this.game = game;
        this.worldSeed = game.getWorld().getSeed();
        this.players = players;
        this.outOfGameSetUp = false;
        this.currentPlayerIndex = 0;

        GameInfoViewModel gameInfoViewModel = ViewModelProviders.of(game.getActivity()).get(GameInfoViewModel.class);
        gameInfoViewModel.setPlayers(new ArrayList<>(Arrays.asList(this.players)));
        gameInfoViewModel.setWorldSeed(this.worldSeed);
    }

    public Player[] getPlayers() { return players; }
    public Player getCurrentPlayer() { return players[currentPlayerIndex]; }
//    public int getCurrentPlayerIndex() { return currentPlayerIndex; }

    public boolean getGameStatus(){ return outOfGameSetUp; }

    public void setCurrentPlayerIndex(int idx) {
        if (idx < 0 || idx >= players.length) return;
        this.currentPlayerIndex = idx;
        ViewModelProviders.of(game.getActivity()).get(GameViewModel.class).setCurrentPlayer(getCurrentPlayer());
//        game.getGameViewModel().setCurrentPlayer(getCurrentPlayer());
    }

    public void setFirstPlayer() {
        setCurrentPlayerIndex(0);
    }

    public void nextPlayerIndex() {
        setCurrentPlayerIndex((currentPlayerIndex + 1) % players.length);
    }

}
