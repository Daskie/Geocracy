package csc309.geocracy;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

import csc309.geocracy.fragments.MainMenuFragment;
import csc309.geocracy.fragments.SettingsFragment;
import csc309.geocracy.fragments.TutorialFragment;
import csc309.geocracy.game.Game;
import csc309.geocracy.main_menu.MenuPagerAdapter;
import csc309.geocracy.main_menu.NonSwipeableViewPager;


public class MenuActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private MenuPagerAdapter mMenuPagerAdapter;
    private NonSwipeableViewPager mViewPager;

    private static final String TAG = "MENU";

    static public GameSurfaceView gameSurfaceView;
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
