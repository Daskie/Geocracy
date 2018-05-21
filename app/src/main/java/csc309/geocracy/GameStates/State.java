package csc309.geocracy.GameStates;

import android.widget.LinearLayout;

import csc309.geocracy.game.GameActivity;
import io.reactivex.disposables.CompositeDisposable;

public interface State {

    void handleInput(GameState states, GameData game, GameActivity game_act);

    void draw(GameData game);
}
