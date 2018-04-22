package csc309.geocracy;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import es.dmoral.toasty.Toasty;
import glm_.vec2.Vec2;
import io.reactivex.android.schedulers.AndroidSchedulers;

class MainSurfaceView extends GLSurfaceView {

    private static final String TAG = "MAIN_SURFACE_VIEW";

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

    public void initEventing() {
        Log.d(TAG, GameActivity.screenTapsObservable.toString());
        GameActivity.screenTapsObservable.subscribe(e -> onTouchEvent(e));
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
                    synchronized (GameActivity.game) { GameActivity.game.swipeDelta.plusAssign(delta); }
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

}
