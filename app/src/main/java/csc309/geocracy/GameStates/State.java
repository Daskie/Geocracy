package csc309.geocracy.GameStates;

import android.widget.LinearLayout;

import io.reactivex.disposables.CompositeDisposable;

public interface State {

    void handleInput(GameState states, GameData game);

    void draw(GameData game);
}
