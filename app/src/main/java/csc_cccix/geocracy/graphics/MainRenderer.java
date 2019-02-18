package csc_cccix.geocracy.graphics;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.GameActivity;
import glm_.vec2.Vec2i;

public class MainRenderer implements GLSurfaceView.Renderer {

    Vec2i size;
    GameActivity activity;

    public MainRenderer(GameActivity activity) {
        this.activity = activity;
        size = new Vec2i();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // May be called more than once during app execution (waking from sleep, for instance)
        // In this method we need to create/recreate any GPU resources
        Log.i("TEST", "BREAK HERE");
        if (!activity.game.loadOpenGL()) {
            Util.exit();
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Called ~60x a second from a render thread which we'll use as main game thread
        activity.game.step();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        size.x = width; size.y = height;
        activity.game.screenResized(size);
    }

}