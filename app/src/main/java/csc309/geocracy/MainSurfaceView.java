package csc309.geocracy;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import glm_.vec2.Vec2;

class MainSurfaceView extends GLSurfaceView {

    MainRenderer renderer;

    public MainSurfaceView(Context context){
        super(context);
        init();

    }

    public MainSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
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
                    synchronized (MainActivity.game) { MainActivity.game.swipeDelta.plusAssign(delta); }
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

}
