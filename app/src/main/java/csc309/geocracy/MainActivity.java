package csc309.geocracy;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import glm_.vec2.Vec2i;

public class MainActivity extends Activity {

    static public MainSurfaceView surfaceView;
    static public Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if device supports OpenGL ES 3.0
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        if (info.reqGlEsVersion < 0x30000) {
            Log.e("MainActivity", "Device does not support OpenGL ES 3.0. Supported version: " + Integer.toHexString(info.reqGlEsVersion));
        }

        // 8 bit color format
        getWindow().setFormat(PixelFormat.RGBA_8888);
        // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // No title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        surfaceView = new MainSurfaceView(this);
        setContentView(surfaceView);

        // Setup game
        game = new Game(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    Vec2i getRenderSize() {
//        return surfaceView.renderer.size;
//    }
}
