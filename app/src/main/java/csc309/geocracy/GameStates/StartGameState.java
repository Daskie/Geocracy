package csc309.geocracy.GameStates;

import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;

import csc309.geocracy.EventBus;
import csc309.geocracy.game.Game;
import csc309.geocracy.GameInputHandler;
import csc309.geocracy.game.GameActivity;
import io.reactivex.disposables.CompositeDisposable;

public class StartGameState implements State{
    public StartGameState(){


    }

    @Override
    public void handleInput(GameState states, GameData game, GameActivity game_act) {

//        GameActivity.uiLayout.addView(game.rollDiceButton);
//        GameActivity.frame.addView(GameActivity.uiLayout);
        game.state = states.choosing_num_armies;
    }

    @Override
    public void draw(GameData game) {

    }




    private void enter(Game game, GameInputHandler input){

    }
    private void exit(Game game, GameInputHandler input){

    }


}
