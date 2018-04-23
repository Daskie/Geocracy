package csc309.geocracy.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import csc309.geocracy.EventBus;
import csc309.geocracy.graphics.MainRenderer;
import glm_.vec2.Vec2;

public class GameSurfaceView extends GLSurfaceView {

    private static final String TAG = "MAIN_SURFACE_VIEW";
    private static final double ROTATION_DAMPER = 4.0;

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
        EventBus.subscribe("TEST_EVENT", this, e -> Log.d(TAG, e.toString()));
        EventBus.subscribe("CAMERA_EVENT", this, e -> handleTouchEvent((MotionEvent) e));
    }

//    public void initEventing() {
////        EventBus.subscribe("TEST_EVENT", this, e -> Log.d(TAG, e.toString()));
////        EventBus.subscribe("CAMERA_EVENT", this, e -> handleTouchEvent((MotionEvent) e));
//    }

    private boolean didPanCamera = false;

    // TODO: implement a proper input system that works between threads
    public boolean handleTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {

            case MotionEvent.ACTION_UP:
//                Log.d(TAG, "Tap released: " + event.toString());
                if (!didPanCamera) Log.d(TAG, "No camera pan, so check for territory selection");
                didPanCamera = false;
                
            case MotionEvent.ACTION_DOWN:
                didPanCamera = false;
                return true; // just here so we get the move action

            case MotionEvent.ACTION_MOVE:
                didPanCamera = true;
//                Log.d(TAG, "Touch moved: " + event.toString());

                // Rotate camera
                if (event.getHistorySize() >= 1) {
                    Vec2 delta = new Vec2(event.getX() - event.getHistoricalX(0), -(event.getY() - event.getHistoricalY(0)));
                    synchronized (GameActivity.game) { GameActivity.game.swipeDelta.plusAssign(delta.div(ROTATION_DAMPER)); }
                }
                return true;
            default:
//                Log.d(TAG, "event default: " + event.toString());

                return super.onTouchEvent(event);
        }


    }

}
