package csc309.geocracy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Callable;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class MenuActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "MENU";

    static public MainSurfaceView mainSurfaceView;
    static public Game game;

    private final CompositeDisposable disposables = new CompositeDisposable();


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 8 bit color format
        getWindow().setFormat(PixelFormat.RGBA_8888);
        // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // No title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.menu);

        // Setup game
//        game = new Game();
//
//        mainSurfaceView = new MainSurfaceView(this);
////        setContentView(mainSurfaceView);
//        mainSurfaceView.getHolder().addCallback(this);

        CoordinatorLayout frame = findViewById(R.id.menuLayout);
        frame.setBackgroundColor(Color.BLUE);
//        frame.addView(mainSurfaceView);

        LinearLayout uiLayout = new LinearLayout(frame.getContext());
        uiLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        uiLayout.setOrientation(LinearLayout.VERTICAL);
        uiLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
        uiLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

        TextView header = new TextView(this);
        header.setText("Geocracy Main Menu (v0.0.1)");
        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header.setTextSize(28);
        header.setPadding(10, 0, 10, 40);
        header.setTextColor(Color.WHITE);

        MenuButton continueGameButton = new MenuButton(MenuActivity.this, "Continue", new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(MenuActivity.this, GameActivity.class));
                return false;
            }
        });

        MenuButton startGameButton = new MenuButton(MenuActivity.this, "Start", new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startActivity(new Intent(MenuActivity.this, GameActivity.class));
                return false;
            }
        });

        MenuButton tutorialButton = new MenuButton(MenuActivity.this, "Tutorial", new View.OnTouchListener() {
            @Override
                public boolean onTouch(View v, MotionEvent event) { Toasty.warning(MenuActivity.this, "Need to Launch Tutorial!", Toast.LENGTH_SHORT, true).show();
                return false;
            }
        });

        MenuButton settingsButton = new MenuButton(MenuActivity.this, "Settings", new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toasty.warning(MenuActivity.this, "Need to Launch Settings!", Toast.LENGTH_SHORT, true).show();

                disposables.add(sampleObservable()
                        // Run on a background thread
                        .subscribeOn(Schedulers.io())
                        // Be notified on the main thread
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<String>() {
                            @Override public void onComplete() {
                                Log.d(TAG, "onComplete()");
                            }

                            @Override public void onError(Throwable e) {
                                Log.e(TAG, "onError()", e);
                            }

                            @Override public void onNext(String string) {
                                Log.d(TAG, "onNext(" + string + ")");
                            }
                        }));


                return false;
            }
        });

        MenuButton exitButton = new MenuButton(MenuActivity.this, "Exit", new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toasty.warning(MenuActivity.this, "Need to Exit Application!", Toast.LENGTH_SHORT, true).show();
                return false;
            }
        });

        uiLayout.addView(header);
        uiLayout.addView(continueGameButton);
        uiLayout.addView(startGameButton);
        uiLayout.addView(tutorialButton);
        uiLayout.addView(settingsButton);
        uiLayout.addView(exitButton);

        frame.addView(uiLayout);
    }

    static Observable<String> sampleObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override public ObservableSource<? extends String> call() throws Exception {
                // Do some long running operation
                SystemClock.sleep(5000);
                return Observable.just("one", "two", "three", "four", "five");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mainSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mainSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
