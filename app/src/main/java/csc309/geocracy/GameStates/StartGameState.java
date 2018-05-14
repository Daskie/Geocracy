package csc309.geocracy.GameStates;

import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;

import csc309.geocracy.EventBus;
import csc309.geocracy.game.Game;
import csc309.geocracy.GameInputHandler;
import io.reactivex.disposables.CompositeDisposable;

public class StartGameState implements State{
    public StartGameState(){
//        EventBus.subscribe();

    }

    @Override
    public void handleInput(GameState states, GameData game) {

        game.state = states.turns;
    }

    @Override
    public void draw(GameData game) {

    }

    void enter(Game game, GameInputHandler input){

    }
    void exit(Game game, GameInputHandler input){

    }


}
