package csc_cccix.geocracy;

import android.content.Context;
import android.util.Log;

import csc_cccix.geocracy.game.Game;
import csc_cccix.geocracy.game.GameActivity;

public class GameSaves {

    private Context context;

    public GameSaves(Context context) {
        this.context = context;
    }

    public void saveGameToLocalStorage(Game game) {
        Log.i("GAME_SAVE", game.toString());
        Util.saveObjectToSharedPreference(context, "gameSave", "mainSave", game);
        loadGameFromLocalStorage(); // TEST LOAD HERE FOR NOW...
    }

    public Game loadGameFromLocalStorage() {
        Game game = Util.getSavedObjectFromPreference(context, "gameSave", "mainSave", Game.class);
        if (game != null) Log.i("GAME_LOAD", game.toString());
        return game;
    }

}
