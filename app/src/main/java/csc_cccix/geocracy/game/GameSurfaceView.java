package csc_cccix.geocracy.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import csc_cccix.geocracy.EventBus;
import csc_cccix.geocracy.graphics.MainRenderer;
import glm_.vec2.Vec2i;
import io.reactivex.disposables.Disposable;

public class GameSurfaceView extends GLSurfaceView implements ScaleGestureDetector.OnScaleGestureListener {

    MainRenderer renderer;
    GameActivity activity;

    Disposable touchEventSubscription;

    private ScaleGestureDetector scaler;

    public GameSurfaceView(Context context){
        super(context);
        init();
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        activity = (GameActivity) getContext();
        setEGLContextClientVersion(3);
        setEGLConfigChooser(8, 8, 8, 8, 24, 0);
        renderer = new MainRenderer(activity);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        scaler = new ScaleGestureDetector(getContext(), this);
        this.touchEventSubscription = EventBus.subscribe("WORLD_TOUCH_EVENT")
            .subscribe(e -> handleTouchEvent((MotionEvent) e));

    }

    public boolean handleTouchEvent(MotionEvent event) {

        scaler.onTouchEvent(event);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) activity.game.wasTapDown(new Vec2i(event.getX(), event.getY()));
                return true;

            case MotionEvent.ACTION_UP:
                // just here so we get the move action
                if (event.getPointerCount() == 1) activity.game.wasTapUp(new Vec2i(event.getX(), event.getY()));
                return true;

            case MotionEvent.ACTION_MOVE:
                // Rotate camera
                if (event.getHistorySize() >= 1) {
                    activity.game.wasSwipe(new Vec2i(event.getX() - event.getHistoricalX(0), -(event.getY() - event.getHistoricalY(0))));
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
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // do nothing
    }

    public void setActivity(GameActivity activity) {
        this.activity = activity;
    }

}
