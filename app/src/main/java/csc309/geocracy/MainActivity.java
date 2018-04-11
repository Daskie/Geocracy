package csc309.geocracy;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;

public class MainActivity extends Activity {

    private MainSurfaceView surfaceView;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if device supports OpenGL ES 3.0
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
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

    Vec2i getRenderSize() {
        return surfaceView.renderer.size;
    }

    float getAspectRatio() {
        Vec2i size = getRenderSize();
        return (float)size.x / (float)size.y;
    }

    class MainSurfaceView extends GLSurfaceView {

        final MainRenderer renderer;

        public MainSurfaceView(Context context){
            super(context);

            setEGLContextClientVersion(3);
            renderer = new MainRenderer();
            setRenderer(renderer);
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        // TODO: implement a proper input system that works between threads
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    return true; // just here so we get the move action
                case MotionEvent.ACTION_MOVE:
                    // Rotate camera
                    if (event.getHistorySize() >= 1) {
                        Vec2 delta = new Vec2(event.getX() - event.getHistoricalX(0), -(event.getY() - event.getHistoricalY(0)));
                        synchronized (game) { game.swipeDelta.plusAssign(delta); }
                    }
                    return true;
                default:
                    return super.onTouchEvent(event);
            }
        }
    }

    public class MainRenderer implements GLSurfaceView.Renderer {

        Vec2i size;

        public MainRenderer() {
            size = new Vec2i();
        }

        @Override
        public void onSurfaceCreated(GL10 unused, EGLConfig config) {
            // May be called more than once during app execution (waking from sleep, for instance)
            // In this method we need to create/recreate any GPU resources
            if (!game.loadOpenGL()) {
                Util.exit();
            }
        }

        @Override
        public void onDrawFrame(GL10 unused) {
            // Called ~60x a second from a render thread which we'll use as main game thread
            game.step();
        }

        @Override
        public void onSurfaceChanged(GL10 unused, int width, int height) {
            size.x = width; size.y = height;

            GLES30.glViewport(0, 0, size.x, size.y);
        }
    }

}
