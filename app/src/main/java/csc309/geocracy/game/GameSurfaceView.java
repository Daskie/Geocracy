package csc309.geocracy.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.util.concurrent.TimeUnit;

import csc309.geocracy.EventBus;
import csc309.geocracy.graphics.MainRenderer;
import glm_.vec2.Vec2i;
import io.reactivex.disposables.Disposable;

public class GameSurfaceView extends GLSurfaceView implements ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "MAIN_SURFACE_VIEW";

    MainRenderer renderer;

    Disposable touchEventSubscription;

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
        setEGLConfigChooser(8, 8, 8, 8, 24, 0);
        renderer = new MainRenderer();
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        scaler = new ScaleGestureDetector(getContext(), this);
        this.touchEventSubscription = EventBus.subscribe("WORLD_TOUCH_EVENT", this)
            .subscribe(e -> handleTouchEvent((MotionEvent) e));

    }


    // TODO: implement a proper input system that works between threads
    public boolean handleTouchEvent(MotionEvent event) {

        scaler.onTouchEvent(event);

        int action = event.getActionMasked();

        System.out.println(action);
        switch (action) {


            case MotionEvent.ACTION_UP:
                if (event.getPointerCount() == 1) GameActivity.game.wasTap(new Vec2i(event.getX(), event.getY()));
                return true; // just here so we get the move action

            case MotionEvent.ACTION_MOVE:
                // Rotate camera
                if (event.getHistorySize() >= 1) {
                    GameActivity.game.wasSwipe(new Vec2i(event.getX() - event.getHistoricalX(0), -(event.getY() - event.getHistoricalY(0))));
                }
                return true;

            default:

                return super.onTouchEvent(event);
        }

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        EventBus.publish("CAMERA_ZOOM_EVENT", -(detector.getScaleFactor() - 1.0f));
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
