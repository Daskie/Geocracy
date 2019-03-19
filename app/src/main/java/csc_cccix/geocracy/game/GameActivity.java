package csc_cccix.geocracy.game;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

import com.jakewharton.rxbinding2.view.RxView;

import androidx.appcompat.app.AppCompatActivity;
import csc_cccix.R;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.backend.Game;
import io.reactivex.disposables.CompositeDisposable;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "GAME_ACTIVITY";
    public static final String USER_ACTION = "USER_ACTION";

    public Game game;
    public GameUI gameUI;
    private GameSurfaceView gameSurfaceView;

    public CompositeDisposable disposables;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposables = new CompositeDisposable();

        // 8 bit color format
        getWindow().setFormat(PixelFormat.RGBA_8888);
        // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // No title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.gameplay);

        // Initialize Surface View
        gameSurfaceView = findViewById(R.id.gameplaySurfaceView);
        gameSurfaceView.getHolder().addCallback(this);
        gameSurfaceView.setActivity(this);

        //EventBus.subscribe("SAVE_GAME_EVENT", this, event -> handleSaveEvent((GameEvent) event));

        // Setup game
        //Boolean load = (Boolean)getIntent().getSerializableExtra("GAME_LOAD");
        // Load game
        //if (false && load != null && load) {
        //    game = game.loadGame();
        //    game.setupFromLoad(this);
        //    if (game == null) {
        //        Log.e("", "Failed to load game");
        //        Util.exit();
        //    }
        //}
        // Start new game
        //else {
            String playerName = (String)getIntent().getSerializableExtra("PLAYER_NAME");
            int numPlayers = (int)getIntent().getSerializableExtra("NUM_PLAYERS");
            int mainPlayerColor = (int)getIntent().getSerializableExtra("MAIN_PLAYER_COLOR");
            long seed = (long)getIntent().getSerializableExtra(("SEED"));
            game = new Game(this, playerName, numPlayers, Util.colorToVec3(mainPlayerColor), seed);

            gameUI = new GameUI(this, getSupportFragmentManager());
            gameUI.showCurrentPlayerFragment();
        //}

//        game.UI.showOverlayFragment(new LoadingFragment());

    }

    //private void handleSaveEvent(GameEvent event) {
    //    if (event.action == GameAction.SAVE_GAME_TAPPED) {
    //        if (game.saveGame(game)) {
    //            runOnUiThread(() -> Toasty.info(this, "game Saved!", Toast.LENGTH_LONG).show());
    //        }
    //        else {
    //            Log.e("", "Failed to save game");
    //            runOnUiThread(() -> Toasty.info(this, "Error saving game", Toast.LENGTH_LONG).show());
    //        }
    //    }
    //}


    @Override
    protected void onResume() {
        super.onResume();
        gameSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
        EventBus.unregister(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "SURFACE CREATED");
        disposables.add(RxView.touches(gameSurfaceView).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN ||
                e.getAction() == MotionEvent.ACTION_UP ||
                e.getAction() == MotionEvent.ACTION_MOVE) {
                EventBus.publish("WORLD_TOUCH_EVENT", e);
            }
        }));
        new Handler().postDelayed(gameUI::removeOverlayFragment, 4000);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {
        // do nothing for now
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // do nothing for now
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // TODO: all state needs to be stuffed in bundle
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // TODO: all state needs to be retrieved from bundle
    }

}