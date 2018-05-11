package csc309.geocracy.game;

import csc309.geocracy.graphics.OrbitCamera;
import glm_.vec2.Vec2;

import static glm_.Java.glm;

public class CameraController {

    private static final float MIN_ELEVATION = 1.5f;
    private static final float MAX_ELEVATION = 5.0f;
    private static final float START_ELEVATION = 3.0f;
    private static final float MOVE_SPEED_FACTOR = 0.0025f;
    private static final float ZOOM_SPEED_FACTOR = 1.0f;
    private static final float FOV = glm.radians(60.0f);
    private static final float NEAR = 0.01f;
    private static final float FAR = MAX_ELEVATION + 1.0f;

    private OrbitCamera camera;

    public CameraController() {
        camera = new OrbitCamera(FOV, NEAR, FAR, 1.0f, MIN_ELEVATION, MAX_ELEVATION, START_ELEVATION);
    }

    public void move(Vec2 delta) {
        delta.timesAssign(MOVE_SPEED_FACTOR);
        camera.move(delta);
    }

    public void zoom(float factor) {
        camera.easeElevation(factor * ZOOM_SPEED_FACTOR);
    }

    public OrbitCamera getCamera() {
        return camera;
    }

}
