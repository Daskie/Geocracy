package csc309.geocracy.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import csc309.geocracy.EventBus;
import csc309.geocracy.graphics.MainRenderer;
import glm_.vec2.Vec2;

import static android.support.v4.math.MathUtils.clamp;

public class GameSurfaceView extends GLSurfaceView implements ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "MAIN_SURFACE_VIEW";
    private static final double ROTATION_DAMPER = 4.0;

    MainRenderer renderer;

    private Double zoom = new Double(1.0);
    private ScaleGestureDetector scaler;
    private boolean scaleMode;

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
        scaler = new ScaleGestureDetector(getContext(), this);
        EventBus.subscribe("TEST_EVENT", this, e -> Log.d(TAG, e.toString()));
        EventBus.subscribe("CAMERA_EVENT", this, e -> handleTouchEvent((MotionEvent) e));
     }


    private boolean didPanCamera = false;

    // TODO: implement a proper input system that works between threads
    public boolean handleTouchEvent(MotionEvent event) {

        scaler.onTouchEvent(event);

        int action = event.getActionMasked();
        switch (action) {

            case MotionEvent.ACTION_UP:
//                Log.d(TAG, "Tap released: " + event.toString());
                if (!didPanCamera && event.getPointerCount() == 1) Log.d(TAG, "No camera pan, so check for territory selection");
                didPanCamera = false;
                
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) didPanCamera = false;
                return true; // just here so we get the move action

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) return true;
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

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        double zoom = -(detector.getScaleFactor() - 1.0) / 1.25;
        EventBus.publish("CAMERA_ZOOM_EVENT", new Double(zoom));
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        scaleMode = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        scaleMode = false;
    }

}
