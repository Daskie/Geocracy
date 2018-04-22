package csc309.geocracy;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;


public class MenuActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private MenuPagerAdapter mMenuPagerAdapter;
    private NonSwipeableViewPager mViewPager;

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

        setContentView(R.layout.menu_main);

        mMenuPagerAdapter = new MenuPagerAdapter(getSupportFragmentManager());
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.menuContainer);
        Log.d("MENU", mViewPager.toString());
        setupViewPager(mViewPager);

        // Setup game
//        game = new Game();
//
//        mainSurfaceView = new MainSurfaceView(this);
////        setContentView(mainSurfaceView);
//        mainSurfaceView.getHolder().addCallback(this);

        CoordinatorLayout frame = findViewById(R.id.menuLayout);
//        frame.setBackgroundColor(Color.BLUE);
//        frame.addView(mainSurfaceView);

//        LinearLayout uiLayout = new LinearLayout(frame.getContext());
//        uiLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//
//        uiLayout.setOrientation(LinearLayout.VERTICAL);
//        uiLayout.setVerticalGravity(Gravity.CENTER_VERTICAL);
//        uiLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
//
//        TextView header = new TextView(this);
//        header.setText("Geocracy Main Menu (v0.0.1)");
//        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        header.setTextSize(28);
//        header.setPadding(10, 0, 10, 40);
//        header.setTextColor(Color.WHITE);
//
//        MenuButton continueGameButton = new MenuButton(MenuActivity.this, "Continue", new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                startActivity(new Intent(MenuActivity.this, GameActivity.class));
//                return false;
//            }
//        });
//
//        MenuButton startGameButton = new MenuButton(MenuActivity.this, "Start", new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                startActivity(new Intent(MenuActivity.this, GameActivity.class));
//                return false;
//            }
//        });
//
//        MenuButton tutorialButton = new MenuButton(MenuActivity.this, "Tutorial", new View.OnTouchListener() {
//            @Override
//                public boolean onTouch(View v, MotionEvent event) { Toasty.warning(MenuActivity.this, "Need to Launch Tutorial!", Toast.LENGTH_SHORT, true).show();
//                return false;
//            }
//        });
//
//        MenuButton settingsButton = new MenuButton(MenuActivity.this, "Settings", new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                Toasty.warning(MenuActivity.this, "Need to Launch Settings!", Toast.LENGTH_SHORT, true).show();
//                setViewPager(1);
//                return false;
//            }
//        });
//
//        MenuButton exitButton = new MenuButton(MenuActivity.this, "Exit", new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Toasty.warning(MenuActivity.this, "Need to Exit Application!", Toast.LENGTH_SHORT, true).show();
//                return false;
//            }
//        });
//
//        uiLayout.addView(header);
//        uiLayout.addView(continueGameButton);
//        uiLayout.addView(startGameButton);
//        uiLayout.addView(tutorialButton);
//        uiLayout.addView(settingsButton);
//        uiLayout.addView(exitButton);
//
//        frame.addView(uiLayout);
    }

    private void setupViewPager(ViewPager vp) {
        MenuPagerAdapter adapter = new MenuPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainMenuFragment(), "Main Menu");
        adapter.addFragment(new TutorialFragment(), "Tutorial");
        adapter.addFragment(new SettingsFragment(), "Settings");
        vp.setAdapter(adapter);
        setViewPager(0);
    }

    public void setViewPager(int fragmentNumber) {
        mViewPager.setCurrentItem(fragmentNumber);
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
