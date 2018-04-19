package csc309.geocracy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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

import es.dmoral.toasty.Toasty;


public class MenuActivity extends Activity implements SurfaceHolder.Callback {

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

        setContentView(R.layout.menu);

        // Setup game
//        game = new Game();
//
//        mainSurfaceView = new MainSurfaceView(this);
////        setContentView(mainSurfaceView);
//        mainSurfaceView.getHolder().addCallback(this);

        CoordinatorLayout frame = findViewById(R.id.menuLayout);
//        frame.addView(mainSurfaceView);

        LinearLayout uiLayout = new LinearLayout(frame.getContext());
        uiLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        uiLayout.setOrientation(LinearLayout.VERTICAL);
        uiLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
        uiLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

        TextView header = new TextView(this);
        header.setText("Geocracy Main Menu (v0.0.1)");

        Button continueGameButton = new Button(this);
        continueGameButton.setText("Continue");
        continueGameButton.setWidth(100);

        continueGameButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        // Toasty Library for displaying notifications.
                        startActivity(new Intent(MenuActivity.this, GameActivity.class));
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        Button startGameButton = new Button(this);
        startGameButton.setText("Start");
        startGameButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        // Toasty Library for displaying notifications.
                        startActivity(new Intent(MenuActivity.this, GameActivity.class));
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        Button tutorialButton = new Button(this);
        tutorialButton.setText("Tutorial");
        tutorialButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        // Toasty Library for displaying notifications.
//                        startActivity(new Intent(MenuActivity.this, GameActivity.class));
                        Toasty.warning(MenuActivity.this, "Need to Launch Tutorial!", Toast.LENGTH_SHORT, true).show();
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        Button settingsButton = new Button(this);
        settingsButton.setText("Settings");
        settingsButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        // Toasty Library for displaying notifications.
//                        startActivity(new Intent(MenuActivity.this, GameActivity.class));
                        Toasty.warning(MenuActivity.this, "Need to Launch Settings!", Toast.LENGTH_SHORT, true).show();
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        Button exitButton = new Button(this);
        exitButton.setText("Exit");
        exitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        // Toasty Library for displaying notifications.
//                        startActivity(new Intent(MenuActivity.this, GameActivity.class));
                        Toasty.warning(MenuActivity.this, "Need to Exit Application!", Toast.LENGTH_SHORT, true).show();
                        return true; // if you want to handle the touch event
                }
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
