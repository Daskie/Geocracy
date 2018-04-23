package csc309.geocracy.game;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
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
import csc309.geocracy.EventBus;
import csc309.geocracy.R;
import es.dmoral.toasty.Toasty;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class GameActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "GAME";

    static public GameSurfaceView gameSurfaceView;
    static public Game game;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private static final Random random = new Random();


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

        gameSurfaceView = new GameSurfaceView(this);
//        setContentView(mainSurfaceView);
        gameSurfaceView.getHolder().addCallback(this);

        disposables.add(RxView.touches(gameSurfaceView).subscribe(e -> EventBus.publish("CAMERA_EVENT", e)));


        CoordinatorLayout frame = findViewById(R.id.gameLayout);
        frame.addView(gameSurfaceView);

        LinearLayout uiLayout = new LinearLayout(this);

        Button testButton = new  Button(this);
        testButton.setText("Geocracy (v0.0.1)");
        disposables.add(RxView.touches(testButton).subscribe(e -> EventBus.publish("TEST_EVENT", e)));


        Button rollDice = new Button(this);
        rollDice.setText("Roll dice!");
        disposables.add(RxView.touches(rollDice).subscribe(e -> {
            if (e.getAction() != MotionEvent.ACTION_UP) return;
            Toasty.warning(GameActivity.this, "Rolling dice...", Toast.LENGTH_SHORT, true).show();
            disposables.add(rollDice()
                // Run on a background thread
                .subscribeOn(Schedulers.computation())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Integer>() {
                    @Override public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }
                    @Override public void onSuccess(Integer number) {
                        Log.d(TAG, "onNext(" + number + ")");
                        Toasty.success(GameActivity.this, "You rolled a: " + number, Toast.LENGTH_SHORT, true).show();
                    }
                }));
        }));

        uiLayout.addView(testButton);
        uiLayout.addView(rollDice);
        frame.addView(uiLayout);
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
