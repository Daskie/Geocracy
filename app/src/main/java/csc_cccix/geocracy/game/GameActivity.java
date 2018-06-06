package csc_cccix.geocracy.game;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import csc_cccix.R;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.fragments.LoadingFragment;
import csc_cccix.geocracy.fragments.SettingsFragment;
import csc_cccix.geocracy.states.DefaultState;
import csc_cccix.geocracy.states.GameAction;
import csc_cccix.geocracy.states.GameEvent;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "GAME_ACTIVITY";
    public static final transient String USER_ACTION = "USER_ACTION";

    static final public SettingsFragment settingsFragment = new SettingsFragment();

    static public Game game;
    static public GameSurfaceView gameSurfaceView;

    private FragmentTransaction userInterfaceFT;
    private FragmentManager fragmentManager;

    private Fragment activeBottomPaneFragment = null;
    private Fragment activeOverlayFragment = null;

    private CompositeDisposable disposables;

    private FloatingActionButton gameInfoBtn;
    private FloatingActionButton settingBtn;
    private FloatingActionButton closeOverlayBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposables = new CompositeDisposable();

        FloatingActionButton endTurnButton;
        FloatingActionButton attackBtn;
        FloatingActionButton addUnitBtn;
        FloatingActionButton removeUnitBtn;
        FloatingActionButton cancelBtn;

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

        fragmentManager = getSupportFragmentManager();

        // Setup game
        Boolean load = (Boolean)getIntent().getSerializableExtra("GAME_LOAD");
        // Load game
        if (load != null && load) {
            game = Game.loadGame();
            if (game == null) {
                Log.e("", "Failed to load game");
                Util.exit();
            }
            game.setActivityAndState(this, new DefaultState(game));
        }
        // Start new game
        else {
            int numPlayers = (int)getIntent().getSerializableExtra("NUM_PLAYERS");
            int mainPlayerColor = (int)getIntent().getSerializableExtra("MAIN_PLAYER_COLOR");
            long seed = (long)getIntent().getSerializableExtra(("SEED"));
            game = new Game(numPlayers, Util.colorToVec3(mainPlayerColor), seed);
            game.setActivityAndState(this, new DefaultState(game));
        }

        // Get Layout Frame +
        CoordinatorLayout frame = findViewById(R.id.gameLayout);
        LinearLayout uiLayout = new LinearLayout(this);
        uiLayout.setOrientation(LinearLayout.VERTICAL);

        TextView geocracyHeader = new TextView(this);
        geocracyHeader.setTextColor(Color.argb(240, 255, 255, 255));
        geocracyHeader.setText("Geocracy (v0.2)");
        geocracyHeader.setTextSize(18.0f);
        geocracyHeader.setPadding(20, 20, 0, 40);

        disposables.add(RxView.touches(geocracyHeader).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish("GAME_NAME_TAP_EVENT", e);
            }
        }));
        EventBus.subscribe("GAME_NAME_TAP_EVENT", this, e -> showGameDevelopers());

        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.hide();
        disposables.add(RxView.touches(cancelBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.CANCEL_ACTION, null));
            }
        }));

        endTurnButton = findViewById(R.id.endTurnButton);
        endTurnButton.hide();
        disposables.add(RxView.touches(endTurnButton).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.END_TURN_ACTION, null));
            }
        }));

        attackBtn = findViewById(R.id.attackBtn);
        attackBtn.hide();
        disposables.add(RxView.touches(attackBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.ATTACK_TAPPED, null));
            }
        }));

        addUnitBtn = findViewById(R.id.addUnitBtn);
        addUnitBtn.hide();
        disposables.add(RxView.touches(addUnitBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.ADD_UNIT_TAPPED, null));
            }
        }));

        removeUnitBtn = findViewById(R.id.removeUnitBtn);
        removeUnitBtn.hide();
        disposables.add(RxView.touches(removeUnitBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.REMOVE_UNIT_TAPPED, null));
            }
        }));

        settingBtn = findViewById(R.id.inGameSettingsBtn);
        settingBtn.show();
        disposables.add(RxView.touches(settingBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.TOGGLE_SETTINGS_VISIBILITY, null));
            }
        }));

        gameInfoBtn = findViewById(R.id.gameInfoBtn);
        gameInfoBtn.show();
        disposables.add(RxView.touches(gameInfoBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.TOGGLE_GAME_INFO_VISIBILITY, null));
            }
        }));

        closeOverlayBtn = findViewById(R.id.closeOverlayBtn);
        closeOverlayBtn.hide();
        disposables.add(RxView.touches(closeOverlayBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                removeActiveOverlayFragment();
            }
        }));


        uiLayout.addView(geocracyHeader);
        frame.addView(uiLayout);

        disposables.add(EventBus.subscribe("UI_EVENT")
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> {
                    UIEvent event = (UIEvent) e;
                    Log.i(TAG, "UI EVENT: " + event);

                    switch (event) {

                        case HIDE_CANCEL_BUTTON:
                            cancelBtn.hide();
                            break;

                        case SHOW_CANCEL_BUTTON:
                            cancelBtn.show();
                            break;

                        case SHOW_END_TURN_BUTTON:
                            endTurnButton.show();
                            break;

                        case HIDE_END_TURN_BUTTON:
                            endTurnButton.hide();
                            break;

                        case HIDE_UPDATE_UNITS_MODE_BUTTONS:
                            removeUnitBtn.hide();
                            addUnitBtn.hide();
                            break;

                        case SHOW_UPDATE_UNITS_MODE_BUTTONS:
                            removeUnitBtn.show();
                            addUnitBtn.show();
                            break;

                        case HIDE_ATTACK_MODE_BUTTON:
                            attackBtn.hide();
                            break;

                        case SHOW_ATTACK_MODE_BUTTON:
                            attackBtn.show();
                            break;

                        case SET_ATTACK_MODE_ACTIVE:
                            attackBtn.setAlpha(1.0f);
                            attackBtn.refreshDrawableState();
                            break;

                        case SET_ATTACK_MODE_INACTIVE:
                            attackBtn.setAlpha(0.4f);
                            attackBtn.refreshDrawableState();
                            break;

                        default:
                            break;

                    }
                })
        );
        showOverlayFragment(new LoadingFragment());

        EventBus.subscribe("SAVE_GAME_EVENT", this, event -> handleSaveEvent((GameEvent) event));
    }

    private void handleSaveEvent(GameEvent event) {
        if (event.action == GameAction.SAVE_GAME_TAPPED) {
            if (Game.saveGame(game)) {
                Toasty.info(this, "Game Saved!", Toast.LENGTH_LONG).show();
            }
            else {
                Log.e("", "Failed to save game");
                Toasty.info(this, "Error saving game", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void showOverlayFragment(Fragment overlayFragment) {
        removeActiveBottomPaneFragment();

        userInterfaceFT = fragmentManager.beginTransaction();
        userInterfaceFT.add(R.id.gameLayout, overlayFragment);
        userInterfaceFT.commit();

        activeOverlayFragment = overlayFragment;

        settingBtn.hide();
        gameInfoBtn.hide();
        closeOverlayBtn.show();
    }

    public void removeActiveOverlayFragment() {
        if (activeOverlayFragment != null) {
            userInterfaceFT = fragmentManager.beginTransaction();
            userInterfaceFT.remove(activeOverlayFragment);
            userInterfaceFT.commit();
            activeOverlayFragment = null;
        }
        closeOverlayBtn.hide();
        settingBtn.show();
        gameInfoBtn.show();
    }

     public void showBottomPaneFragment(Fragment bottomPaneFragment) {
        removeActiveBottomPaneFragment();

        userInterfaceFT = fragmentManager.beginTransaction();
        userInterfaceFT.add(R.id.gameLayout, bottomPaneFragment);
        userInterfaceFT.commit();

        activeBottomPaneFragment = bottomPaneFragment;
    }



     public void removeActiveBottomPaneFragment() {
        if (activeBottomPaneFragment != null) {
            userInterfaceFT = fragmentManager.beginTransaction();
            userInterfaceFT.remove(activeBottomPaneFragment);
            userInterfaceFT.commit();
            activeBottomPaneFragment = null;
        }
    }

    void showGameDevelopers() {
        Toasty.info(this, "OUR DEV TEAM:\n\nAustin Quick\nAndrew Exton\nGuraik Clair\nSydney Baroya\nSamantha Koski\nRyan\n\nThanks for playing!", Toast.LENGTH_LONG).show();
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
        Log.i(TAG, "SURFACE CREATED");
        disposables.add(RxView.touches(gameSurfaceView).subscribe(e -> EventBus.publish("WORLD_TOUCH_EVENT", e)));
        new Handler().postDelayed(() -> {
            removeActiveOverlayFragment();
        }, 4000);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

}