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
        camera = new OrbitCamera(FOV, NEAR, FAR, 1.0f, START_ELEVATION);
    }

    // Distance decreases linearly nearer to the surface
    public void move(Vec2 delta) {
        delta.timesAssign(MOVE_SPEED_FACTOR * glm.clamp((camera.getElevation() - 1.0f) / (START_ELEVATION - 1.0f), 0.0f, 1.0f));
        camera.move(delta);
    }

    // Zooms on a parabola rather than a line. Zoom "slows" closer to surface
    public void zoom(float factor) {
        float actualP = (camera.getElevation() - MIN_ELEVATION) / (MAX_ELEVATION - MIN_ELEVATION);
        float easeP = (float)Math.sqrt(actualP);
        easeP += ZOOM_SPEED_FACTOR * factor;
        actualP = glm.clamp(easeP * easeP, 0.0f, 1.0f);
        camera.setElevation(actualP * (MAX_ELEVATION - MIN_ELEVATION) + MIN_ELEVATION);
    }

    public OrbitCamera getCamera() {
        return camera;
    }

}
