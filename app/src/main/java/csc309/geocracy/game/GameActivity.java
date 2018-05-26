package csc309.geocracy.game;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.Random;
import java.util.concurrent.Callable;
import csc309.geocracy.EventBus;
import csc309.geocracy.R;
import csc309.geocracy.fragments.SettingsFragment;
import csc309.geocracy.fragments.TerritoryDetailFragment;
import csc309.geocracy.fragments.TroopSelectionFragment;
import csc309.geocracy.states.GameAction;
import csc309.geocracy.states.GameEvent;
import csc309.geocracy.states.GameState;
import es.dmoral.toasty.Toasty;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "GAME_ACTIVITY";

    static public Game game;
    static public GameSurfaceView gameSurfaceView;

    private final CompositeDisposable disposables = new CompositeDisposable();

    static private FragmentTransaction userInterfaceFT;
    static private FragmentManager fragmentManager;

    static private Fragment activeBottomPaneFragment = null;

    static public SettingsFragment settingsFragment = new SettingsFragment();

    static private boolean settingsVisible = false;


    public FloatingActionButton attackBtn;
    public FloatingActionButton cancelBtn;

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
        fragmentManager = getSupportFragmentManager();

        // Setup game
        game = new Game(this);

        setContentView(R.layout.gameplay);

        // Initialize Surface View and Add World Touch Handling
        gameSurfaceView = findViewById(R.id.gameplaySurfaceView);
        gameSurfaceView.getHolder().addCallback(this);
        disposables.add(RxView.touches(gameSurfaceView).subscribe(e -> {
            EventBus.publish("WORLD_TOUCH_EVENT", e);
        }));


        // Get Layout Frame +
        CoordinatorLayout frame = findViewById(R.id.gameLayout);
        LinearLayout uiLayout = new LinearLayout(this);
        uiLayout.setOrientation(LinearLayout.VERTICAL);

        TextView geocracyHeader = new TextView(this);
        geocracyHeader.setTextColor(Color.argb(240, 255, 255, 255));
        geocracyHeader.setText("Geocracy (v0.1)");
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
                EventBus.publish("USER_ACTION", new GameEvent(GameAction.CANCEL_ACTION, null));
            }
        }));

        attackBtn = findViewById(R.id.attackBtn);
        attackBtn.hide();
        disposables.add(RxView.touches(attackBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish("USER_ACTION", new GameEvent(GameAction.ATTACK_TAPPED, null));
            }
        }));

        FloatingActionButton settingBtn = findViewById(R.id.inGameSettingsBtn);
        settingBtn.show();
        disposables.add(RxView.touches(settingBtn).subscribe(e -> {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                EventBus.publish("USER_ACTION", new GameEvent(GameAction.TOGGLE_SETTINGS_VISIBILITY, null));
            }
        }));

        uiLayout.addView(geocracyHeader);
        frame.addView(uiLayout);

        disposables.add(EventBus.subscribe("UI_EVENT", this)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> {
                    UIEvent event = (UIEvent) e;
                    System.out.println("UI EVENT: " + event);

                    switch (event) {

                        case HIDE_CANCEL_BUTTON:
                            this.cancelBtn.hide();
                            break;

                        case SHOW_CANCEL_BUTTON:
                            this.cancelBtn.show();
                            break;

                        case HIDE_ATTACK_MODE_BUTTON:
                            this.attackBtn.hide();
                            break;

                        case SHOW_ATTACK_MODE_BUTTON:
                            this.attackBtn.show();
                            break;

                        case SET_ATTACK_MODE_ACTIVE:
                            this.attackBtn.setAlpha(1.0f);
                            break;

                        case SET_ATTACK_MODE_INACTIVE:
                            this.attackBtn.setAlpha(0.4f);
                            break;

                        default:
                            break;

                    }
                })
        );

    }

     public void toggleSettingsFragment() {
        if (settingsVisible) {
            userInterfaceFT = fragmentManager.beginTransaction();
            userInterfaceFT.remove(settingsFragment);
            userInterfaceFT.commit();
        } else {
            userInterfaceFT = fragmentManager.beginTransaction();
            userInterfaceFT.add(R.id.gameLayout, settingsFragment);
            userInterfaceFT.commit();
        }
        settingsVisible = !settingsVisible;
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
        System.out.println("SURFACE CREATED");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

}