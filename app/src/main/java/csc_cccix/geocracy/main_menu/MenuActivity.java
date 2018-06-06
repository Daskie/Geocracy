package csc_cccix.geocracy.main_menu;

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

import csc_cccix.R;
import csc_cccix.geocracy.fragments.GameSetupFragment;
import csc_cccix.geocracy.fragments.MainMenuFragment;
import csc_cccix.geocracy.fragments.SettingsFragment;
import csc_cccix.geocracy.fragments.TutorialFragment;
import csc_cccix.geocracy.game.Game;

public class MenuActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private NonSwipeableViewPager mViewPager;

    public static Game game;

    public MediaPlayer mp;

    public Toolbar toolbar;

    public enum Pages {
        HOME,
        TUTORIAL,
        SETTINGS,
        GAME_SETUP
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
                navigateToPage(Pages.HOME);
            }
        });

        mViewPager = (NonSwipeableViewPager) findViewById(R.id.menuContainer);
        Log.d("MENU", mViewPager.toString());
        setupViewPager(mViewPager);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        toolbar.setVisibility(View.INVISIBLE);
        setViewPager(0);
    }

    private MainMenuFragment mainMenuFragment;

    private void setupViewPager(ViewPager vp) {
        MenuPagerAdapter adapter = new MenuPagerAdapter(getSupportFragmentManager());

        mainMenuFragment = new MainMenuFragment();

        adapter.addFragment(mainMenuFragment, "Main Menu");
        adapter.addFragment(new GameSetupFragment(), "Game Setup");
        adapter.addFragment(new TutorialFragment(), "Tutorial");
        adapter.addFragment(new SettingsFragment(), "Settings");
        vp.setAdapter(adapter);
        navigateToPage(Pages.HOME);
    }

    public void navigateToPage(Pages page) {
        switch (page) {

            case HOME:

                toolbar.setVisibility(View.INVISIBLE);
                setViewPager(0);
                break;

            case GAME_SETUP:
                toolbar.setVisibility(View.VISIBLE);
                setViewPager(1);
                break;

            case TUTORIAL:
                toolbar.setVisibility(View.VISIBLE);
                setViewPager(2);
                break;

            case SETTINGS:
                toolbar.setVisibility(View.VISIBLE);
                setViewPager(3);
                break;

            default:
                break;
        }
    }

    public void setViewPager(int fragmentNumber) {
        mViewPager.setCurrentItem(fragmentNumber);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {
        
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
