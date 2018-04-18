package csc309.geocracy;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class GameActivity extends Activity implements SurfaceHolder.Callback {

    static public MainSurfaceView mainSurfaceView;
    static public Game game;

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

        setContentView(R.layout.gameplay);

        // Setup game
        game = new Game();

        mainSurfaceView = new MainSurfaceView(this);
//        setContentView(mainSurfaceView);
        mainSurfaceView.getHolder().addCallback(this);

        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
        frame.addView(mainSurfaceView);

        LinearLayout uiLayout = new LinearLayout(this);

        Button testButton = new  Button(this);
        testButton.setText("Geocracy (v0.0.1)");

        uiLayout.addView(testButton);
        frame.addView(uiLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainSurfaceView.onPause();
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
