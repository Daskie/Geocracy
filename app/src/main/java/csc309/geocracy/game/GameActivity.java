package csc309.geocracy.game;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import csc309.geocracy.EventBus;
import csc309.geocracy.R;
import csc309.geocracy.fragments.SettingsFragment;
import csc309.geocracy.fragments.TerritoryDetailFragment;
import csc309.geocracy.fragments.TroopSelectionFragment;
import es.dmoral.toasty.Toasty;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "GAME";

    static public GameSurfaceView gameSurfaceView;
    static public Game game;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private static final Random random = new Random();

    private Fragment activeBottomPaneFragment = null;
    private TerritoryDetailFragment territoryDetailFragment = new TerritoryDetailFragment();
    private TroopSelectionFragment troopSelectionFragment = new TroopSelectionFragment();

    private SettingsFragment settingsFragment = new SettingsFragment();
    private FragmentTransaction userInterfaceFT;
    private boolean settingsVisible = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 8 bit color format
        getWindow().setFormat(PixelFormat.RGBA_8888);
        // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // No title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.gameplay);

        // Setup game
        game = new Game();

        gameSurfaceView = findViewById(R.id.gameplaySurfaceView);
        gameSurfaceView.getHolder().addCallback(this);

        disposables.add(RxView.touches(gameSurfaceView).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) EventBus.publish("WORLD_TOUCH_EVENT", e);
            if (e.getActionMasked() == MotionEvent.ACTION_MOVE) EventBus.publish("WORLD_TOUCH_EVENT", e);
        }));

//        disposables.add(RxView.touches(gameSurfaceView).subscribe(e -> {
//        }));


//        // Begin the transaction
//        userInterfaceFT = getSupportFragmentManager().beginTransaction();
//        userInterfaceFT.replace(R.id.gameLayout, new TerritoryDetailFragment());
//        userInterfaceFT.commit();

        CoordinatorLayout frame = findViewById(R.id.gameLayout);
//        frame.addView(gameSurfaceView);

        LinearLayout uiLayout = new LinearLayout(this);
//
        Button gameDevBtn = new  Button(this);
        gameDevBtn.setText("Geocracy (v0.0.3)");
        disposables.add(RxView.touches(gameDevBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) EventBus.publish("GAME_NAME_TAP_EVENT", e);
        }));
        EventBus.subscribe("GAME_NAME_TAP_EVENT", this,  e -> showGameDevelopers());


        Button selectBtn = new Button(this);
        selectBtn.setText("SELECT");

        disposables.add(RxView.touches(selectBtn).subscribe(e -> {
            if (e.getAction() != MotionEvent.ACTION_UP) return;
            showBottomPaneFragment(new TerritoryDetailFragment());
        }));

        Button attackBtn = new Button(this);
        attackBtn.setText("ATTACK");

        disposables.add(RxView.touches(attackBtn).subscribe(e -> {
            if (e.getAction() != MotionEvent.ACTION_UP) return;
            showBottomPaneFragment(new TroopSelectionFragment());
        }));


        Button settingBtn = new Button(this);
        settingBtn.setText("SETTINGS");

        disposables.add(RxView.touches(settingBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) toggleSettingsFragment();
        }));

//        Button rollDice = new Button(this);
//        rollDice.setText("Roll dice!");


//        disposables.add(RxView.touches(rollDice).subscribe(e -> {
//            if (e.getAction() != MotionEvent.ACTION_UP) return;
//            Toasty.warning(GameActivity.this, "Rolling dice...", Toast.LENGTH_SHORT, true).show();
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
//                        Log.e(TAG, "onError()", e);
//                    }
//                    @Override public void onSuccess(Integer number) {
//                        Log.d(TAG, "onNext(" + number + ")");
//                        Toasty.success(GameActivity.this, "You rolled a: " + number, Toast.LENGTH_SHORT, true).show();
//                    }
//                }));
//        }));

        uiLayout.addView(gameDevBtn);
        uiLayout.addView(settingBtn);
        uiLayout.addView(selectBtn);
        uiLayout.addView(attackBtn);

        frame.addView(uiLayout);

    }
    void toggleSettingsFragment() {
        if (settingsVisible) {
            userInterfaceFT = getSupportFragmentManager().beginTransaction();
            userInterfaceFT.remove(settingsFragment);
            userInterfaceFT.commit();
        } else {
            userInterfaceFT = getSupportFragmentManager().beginTransaction();
            userInterfaceFT.add(R.id.gameLayout, settingsFragment);
            userInterfaceFT.commit();
        }
        settingsVisible = !settingsVisible;
    }

    void showBottomPaneFragment(Fragment bottomPaneFragment) {
        if (activeBottomPaneFragment != null) {
            userInterfaceFT = getSupportFragmentManager().beginTransaction();
            userInterfaceFT.remove(activeBottomPaneFragment);
            userInterfaceFT.commit();
            activeBottomPaneFragment = null;
        }

        userInterfaceFT = getSupportFragmentManager().beginTransaction();
        userInterfaceFT.add(R.id.gameLayout, bottomPaneFragment);
        userInterfaceFT.commit();

        activeBottomPaneFragment = bottomPaneFragment;
    }

    void showGameDevelopers() {
        Toasty.info(this, "OUR DEV TEAM:\n\nAustin Quick\nAndrew Exton\nGuraik Clair\nSydney Baroya\nSamantha Koski\nRyan\n\nThanks for playing!", Toast.LENGTH_LONG).show();
    }

    static Single<Integer> rollDice() {
        return Single.fromCallable(new Callable<Integer>() {
            @Override public Integer call() throws Exception {
                // wait 1.5 sec before returning a value between 1 and 6 (inclusive)
                SystemClock.sleep(1500);
                return random.nextInt(6) + 1;
            }
        });
    }

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
        System.out.println("SURFACE CREATED");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

}
