package cscCCCIX.geocracy.main_menu;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cscCCCIX.R;
import cscCCCIX.geocracy.fragments.MainMenuFragment;
import cscCCCIX.geocracy.fragments.SettingsFragment;
import cscCCCIX.geocracy.fragments.TutorialFragment;
import cscCCCIX.geocracy.game.Game;
import cscCCCIX.geocracy.game.GameSurfaceView;

public class MenuActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private MenuPagerAdapter mMenuPagerAdapter;
    private NonSwipeableViewPager mViewPager;

    private static final String TAG = "MENU";

    static public GameSurfaceView gameSurfaceView;
    static public Game game;

    public MediaPlayer mp;

    public Toolbar toolbar;

    public enum Pages {
        Home,
        Tutorial,
        Settings
    }

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPage(Pages.Home);
            }
        });

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

    }

    private void setupViewPager(ViewPager vp) {
        MenuPagerAdapter adapter = new MenuPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainMenuFragment(), "Main Menu");
        adapter.addFragment(new TutorialFragment(), "Tutorial");
        adapter.addFragment(new SettingsFragment(), "Settings");
        vp.setAdapter(adapter);
        navigateToPage(Pages.Home);
    }

    public void navigateToPage(Pages page) {
        switch (page) {

            case Home:
                toolbar.setVisibility(View.INVISIBLE);
                setViewPager(0);
                break;

            case Tutorial:
                toolbar.setVisibility(View.VISIBLE);
                setViewPager(1);
                break;

            case Settings:
                toolbar.setVisibility(View.VISIBLE);
                setViewPager(2);

            default:
                break;
        }
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