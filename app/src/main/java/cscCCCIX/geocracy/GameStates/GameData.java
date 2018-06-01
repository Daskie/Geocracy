package cscCCCIX.geocracy.GameStates;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.util.Random;

import cscCCCIX.geocracy.game.GameActivity;
import io.reactivex.disposables.CompositeDisposable;


public class GameData extends AppCompatActivity {
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

    private final CompositeDisposable disposables = new CompositeDisposable();
    private FragmentTransaction userInterfaceFT;
    private static final Random random = new Random();



    public GameData(GameState game_state, GameActivity game_act){

        this.state = game_state.start;

//        this.rollDiceButton = game_act.rollDice;

    }


//    public void handleInput(GameState states, GameActivity game_act){
//        this.state.handleInput(states, this, game_act);
//    }
//
//
//    public void draw(){
//        this.state.draw(this);
//    }
//
//    public void setUpButtons(){
//        disposables.add(RxView.touches(this.rollDiceButton).subscribe(e -> {
//            if (e.getAction() != MotionEvent.ACTION_UP) return;
//            Toasty.warning(this, "Rolling dice...", Toast.LENGTH_SHORT, true).show();
//            userInterfaceFT = getSupportFragmentManager().beginTransaction();
//            userInterfaceFT.replace(R.id.gameLayout, new SettingsFragment());
//            userInterfaceFT.commit();
//            disposables.add(rollDice()
//                // Run on a background thread
//                .subscribeOn(Schedulers.computation())
//                // Be notified on the main thread
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<Integer>() {
//                    @Override public void onError(Throwable e) {
//                        Log.e("GAME", "onError()", e);
//                    }
//                    @Override public void onSuccess(Integer number) {
//                        Log.d("GAME", "onNext(" + number + ")");
//                        Toasty.success(GameData.this, "You rolled a: " + number, Toast.LENGTH_SHORT, true).show();
//                    }
//                }));
//        }));
//    }
//
//    static Single<Integer> rollDice() {
//        return Single.fromCallable(new Callable<Integer>() {
//            @Override public Integer call() throws Exception {
//                // wait 1.5 sec before returning a value between 1 and 6 (inclusive)
//                SystemClock.sleep(1500);
//                return random.nextInt(6) + 1;
//            }
//        });
//    }
}
