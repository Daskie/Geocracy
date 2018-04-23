package csc309.geocracy.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import csc309.geocracy.graphics.MainRenderer;
import glm_.vec2.Vec2;

public class GameSurfaceView extends GLSurfaceView {

    private static final String TAG = "MAIN_SURFACE_VIEW";

    MainRenderer renderer;

    public GameSurfaceView(Context context){
        super(context);
        init();
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(3);
        renderer = new MainRenderer();
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    public void initEventing() {
        Log.d(TAG, GameActivity.screenTapsObservable.toString());
        GameActivity.screenTapsObservable.subscribe(e -> handleTouchEvent(e));
    }

    // TODO: implement a proper input system that works between threads
    public boolean handleTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return true; // just here so we get the move action
            case MotionEvent.ACTION_MOVE:
                // Rotate camera
                if (event.getHistorySize() >= 1) {
                    Vec2 delta = new Vec2(event.getX() - event.getHistoricalX(0), -(event.getY() - event.getHistoricalY(0)));
                    synchronized (GameActivity.game) { GameActivity.game.swipeDelta.plusAssign(delta); }
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

}
