package csc_cccix.geocracy.game;

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
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import csc_cccix.R;
import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.fragments.CurrentPlayerFragment;
import csc_cccix.geocracy.fragments.LoadingFragment;
import csc_cccix.geocracy.fragments.SettingsFragment;
import csc_cccix.geocracy.states.DefaultState;
import csc_cccix.geocracy.states.GameAction;
import csc_cccix.geocracy.states.GameEvent;
import csc_cccix.geocracy.states.SetUpInitTerritoriesState;
import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.CompositeDisposable;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "GAME_ACTIVITY";
    public static final transient String USER_ACTION = "USER_ACTION";

    public static final SettingsFragment settingsFragment = new SettingsFragment();

    public static Game game;
    private GameSurfaceView gameSurfaceView;

    private FragmentTransaction userInterfaceFT;
    private FragmentManager fragmentManager;

    private Fragment activeCurrentPlayerFragment = null;
    private Fragment activeBottomPaneFragment = null;
    private Fragment activeOverlayFragment = null;

    private CompositeDisposable disposables;

    private FloatingActionButton endTurnButton;
    private FloatingActionButton attackBtn;
    private FloatingActionButton addUnitBtn;
    private FloatingActionButton removeUnitBtn;
    private FloatingActionButton cancelBtn;
    private FloatingActionButton gameInfoBtn;
    private FloatingActionButton settingBtn;
    private FloatingActionButton closeOverlayBtn;
    private FloatingActionButton confirmButton;
    private FloatingActionButton fortifyButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean fromGameLoad;

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

        fragmentManager = getSupportFragmentManager();

        // Setup game
        fromGameLoad = false;
        Boolean load = (Boolean)getIntent().getSerializableExtra("GAME_LOAD");
        // Load game
        if (load != null && load) {
            game = Game.loadGame();
            if (game == null) {
                Log.e("", "Failed to load game");
                Util.exit();
            } else {
                fromGameLoad = true;
            }
        }
        // Start new game
        else {
            String playerName = (String)getIntent().getSerializableExtra("PLAYER_NAME");
            int numPlayers = (int)getIntent().getSerializableExtra("NUM_PLAYERS");
            int mainPlayerColor = (int)getIntent().getSerializableExtra("MAIN_PLAYER_COLOR");
            long seed = (long)getIntent().getSerializableExtra(("SEED"));
            game = new Game(playerName, numPlayers, Util.colorToVec3(mainPlayerColor), seed);
        }

        // Get Layout Frame +
        CoordinatorLayout frame = findViewById(R.id.gameLayout);
        LinearLayout uiLayout = new LinearLayout(this);
        uiLayout.setOrientation(LinearLayout.VERTICAL);

        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.hide();
        disposables.add(RxView.touches(cancelBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.CANCEL_TAPPED, null));
            }
        }));

        confirmButton = findViewById(R.id.confirmButton);
        confirmButton.hide();
        disposables.add(RxView.touches(confirmButton).subscribe(event -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.CONFIRM_TAPPED, null));
            }
        }));

        endTurnButton = findViewById(R.id.endTurnButton);
        endTurnButton.hide();
        disposables.add(RxView.touches(endTurnButton).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.END_TURN_TAPPED, null));
            }
        }));

        attackBtn = findViewById(R.id.attackBtn);
        attackBtn.hide();
        disposables.add(RxView.touches(attackBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.ATTACK_TAPPED, null));
            }
        }));

        fortifyButton = findViewById(R.id.fortifyButton);
        fortifyButton.hide();
        disposables.add(RxView.touches(fortifyButton).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.FORTIFY_TAPPED, null));
            }
        }));

        addUnitBtn = findViewById(R.id.addUnitBtn);
        addUnitBtn.hide();
        disposables.add(RxView.touches(addUnitBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.ADD_UNIT_TAPPED, null));
            }
        }));

        removeUnitBtn = findViewById(R.id.removeUnitBtn);
        removeUnitBtn.hide();
        disposables.add(RxView.touches(removeUnitBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.REMOVE_UNIT_TAPPED, null));
            }
        }));

        settingBtn = findViewById(R.id.inGameSettingsBtn);
        settingBtn.show();
        disposables.add(RxView.touches(settingBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.SETTINGS_TAPPED, null));
            }
        }));

        gameInfoBtn = findViewById(R.id.gameInfoBtn);
        gameInfoBtn.show();
        disposables.add(RxView.touches(gameInfoBtn).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                EventBus.publish(USER_ACTION, new GameEvent(GameAction.GAME_INFO_TAPPED, null));
            }
        }));

        closeOverlayBtn = findViewById(R.id.closeOverlayBtn);
        closeOverlayBtn.hide();
        disposables.add(RxView.touches(closeOverlayBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                removeActiveOverlayFragment();
            }
        }));

        frame.addView(uiLayout);
        showOverlayFragment(new LoadingFragment());

        EventBus.subscribe("SAVE_GAME_EVENT", this, event -> handleSaveEvent((GameEvent) event));

        if (fromGameLoad) {
            game.setActivityAndState(this, new DefaultState(game));
        } else {
            game.setActivityAndState(this, new SetUpInitTerritoriesState(game));
        }

    }

    private void handleSaveEvent(GameEvent event) {
        if (event.action == GameAction.SAVE_GAME_TAPPED) {
            if (Game.saveGame(game)) {
                runOnUiThread(() -> Toasty.info(this, "Game Saved!", Toast.LENGTH_LONG).show());
            }
            else {
                Log.e("", "Failed to save game");
                runOnUiThread(() -> Toasty.info(this, "Error saving game", Toast.LENGTH_LONG).show());
            }
        }
    }

    public void updateCurrentPlayerFragment() {
        CurrentPlayerFragment currentPlayerFragment = CurrentPlayerFragment.newInstance(game.getCurrentPlayer());
        userInterfaceFT = fragmentManager.beginTransaction();
        if (activeCurrentPlayerFragment != null) userInterfaceFT.remove(activeCurrentPlayerFragment);
        userInterfaceFT.commit();
        userInterfaceFT = fragmentManager.beginTransaction();
        userInterfaceFT.commit();
        userInterfaceFT.add(R.id.gameLayout, currentPlayerFragment);
        activeCurrentPlayerFragment = currentPlayerFragment;
    }

    public void showOverlayFragment(Fragment overlayFragment) {
        this.runOnUiThread(() -> {
            removeActiveBottomPaneFragment();

            userInterfaceFT = fragmentManager.beginTransaction();
            userInterfaceFT.add(R.id.gameLayout, overlayFragment);
            userInterfaceFT.commit();

            activeOverlayFragment = overlayFragment;

            if (overlayFragment.getClass() == CurrentPlayerFragment.class) {
                closeOverlayBtn.hide();
                settingBtn.show();
                gameInfoBtn.show();
            } else {
                closeOverlayBtn.show();
                settingBtn.hide();
                gameInfoBtn.hide();
            }
        });
    }

    public void removeActiveOverlayFragment() {
        this.runOnUiThread(() -> {
            if (activeOverlayFragment != null) {
                userInterfaceFT = fragmentManager.beginTransaction();
                userInterfaceFT.remove(activeOverlayFragment);
                userInterfaceFT.commit();
                activeOverlayFragment = null;
            }

            closeOverlayBtn.hide();
            settingBtn.show();
            gameInfoBtn.show();
        });
    }

     public void showBottomPaneFragment(Fragment bottomPaneFragment) {
        this.runOnUiThread(() -> {
            removeActiveBottomPaneFragment();

            userInterfaceFT = fragmentManager.beginTransaction();
            userInterfaceFT.add(R.id.gameLayout, bottomPaneFragment);
            userInterfaceFT.commit();

            activeBottomPaneFragment = bottomPaneFragment;
        });
    }

     public void removeActiveBottomPaneFragment() {
        this.runOnUiThread(() -> {
            if (activeBottomPaneFragment != null) {
                userInterfaceFT = fragmentManager.beginTransaction();
                userInterfaceFT.remove(activeBottomPaneFragment);
                userInterfaceFT.commit();
                activeBottomPaneFragment = null;
            }
        });
    }

    public void setAttackModeButtonVisibilityAndActiveState(boolean isVisible, boolean isActive) {
        setFABVisibilityAndActiveState(attackBtn, isVisible, isActive);
    }

    public void setConfirmButtonVisibilityAndActiveState(boolean isVisible, boolean isActive) {
        setFABVisibilityAndActiveState(confirmButton, isVisible, isActive);
    }

    public void setFortifyButtonVisibilityAndActiveState(boolean isVisible, boolean isActive) {
        setFABVisibilityAndActiveState(fortifyButton, isVisible, isActive);
    }

    private void setFABVisibilityAndActiveState(FloatingActionButton fab, boolean isVisible, boolean isActive) {
        this.runOnUiThread(() -> {
            AlphaAnimation alphaChange;
            if (isActive) { alphaChange = new AlphaAnimation(fab.getAlpha(), 1.0f); }
            else { alphaChange = new AlphaAnimation(fab.getAlpha(), 0.4f); }
            alphaChange.setFillAfter(true);
            fab.startAnimation(alphaChange);
            if (isVisible) { fab.show(); }
            else { fab.hide(); }
        });
    }

    public void setUpdateUnitCountButtonsVisibility(boolean isVisible) {
        this.runOnUiThread(() -> {
            if (isVisible) {
                addUnitBtn.show();
                removeUnitBtn.show();
            } else {
                addUnitBtn.hide();
                removeUnitBtn.hide();
            }
        });
    }

    public void hideAllGameInteractionButtons() {
        this.runOnUiThread(() -> {
            attackBtn.hide();
            cancelBtn.hide();
            addUnitBtn.hide();
            removeUnitBtn.hide();
            endTurnButton.hide();
            confirmButton.hide();
            fortifyButton.hide();
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
        Log.i(TAG, "SURFACE CREATED");
        disposables.add(RxView.touches(gameSurfaceView).subscribe(e -> {
            if (e.getAction() == MotionEvent.ACTION_DOWN ||
                e.getAction() == MotionEvent.ACTION_UP ||
                e.getAction() == MotionEvent.ACTION_MOVE) {
                EventBus.publish("WORLD_TOUCH_EVENT", e);
            }
        }));
        new Handler().postDelayed(this::removeActiveOverlayFragment, 4000);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {
        // do nothing for now
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // do nothing for now
    }

    public FloatingActionButton getAddUnitBtn() {
        return addUnitBtn;
    }

    public FloatingActionButton getAttackBtn() {
        return attackBtn;
    }

    public FloatingActionButton getCancelBtn() {
        return cancelBtn;
    }

    public FloatingActionButton getConfirmButton() { return confirmButton; }

    public FloatingActionButton getCloseOverlayBtn() {
        return closeOverlayBtn;
    }

    public FloatingActionButton getEndTurnButton() {
        return endTurnButton;
    }

    public FloatingActionButton getGameInfoBtn() {
        return gameInfoBtn;
    }

    public FloatingActionButton getRemoveUnitBtn() {
        return removeUnitBtn;
    }

    public FloatingActionButton getSettingBtn() {
        return settingBtn;
    }
}