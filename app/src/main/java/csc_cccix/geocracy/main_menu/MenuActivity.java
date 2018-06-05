package csc_cccix.geocracy.main_menu;

import android.app.FragmentManager;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import csc_cccix.geocracy.game.GameSurfaceView;

public class MenuActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private NonSwipeableViewPager mViewPager;

    private static final String TAG = "MENU";

    public static Game game;

    public MediaPlayer mp;

    public Toolbar toolbar;

    public enum Pages {
        Home,
        Tutorial,
        Settings,
        GameSetup
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

        mViewPager = (NonSwipeableViewPager) findViewById(R.id.menuContainer);
        Log.d("MENU", mViewPager.toString());
        setupViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager vp) {
        MenuPagerAdapter adapter = new MenuPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MainMenuFragment(), "Main Menu");
        adapter.addFragment(new GameSetupFragment(), "Game Setup");
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

            case GameSetup:
                toolbar.setVisibility(View.VISIBLE);
                setViewPager(1);
                break;

            case Tutorial:
                toolbar.setVisibility(View.VISIBLE);
                setViewPager(2);
                break;

            case Settings:
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
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

}
