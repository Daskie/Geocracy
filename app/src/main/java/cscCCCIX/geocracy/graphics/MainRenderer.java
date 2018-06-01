package cscCCCIX.geocracy.graphics;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cscCCCIX.geocracy.Util;
import cscCCCIX.geocracy.game.GameActivity;
import glm_.vec2.Vec2i;

public class MainRenderer implements GLSurfaceView.Renderer {

    Vec2i size;

    public MainRenderer() {
        size = new Vec2i();
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // May be called more than once during app execution (waking from sleep, for instance)
        // In this method we need to create/recreate any GPU resources
        if (!GameActivity.game.loadOpenGL()) {
            Util.exit();
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Called ~60x a second from a render thread which we'll use as main game thread
        GameActivity.game.step();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        size.x = width; size.y = height;
        GameActivity.game.screenResized(size);
    }

}