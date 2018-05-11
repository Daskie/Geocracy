package csc309.geocracy.game;

import csc309.geocracy.Util;
import csc309.geocracy.graphics.OrbitCamera;
import glm_.quat.Quat;
import glm_.vec2.Vec2;
import glm_.vec3.Vec3;

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
    private static final float TARGET_TIME = 0.5f;
    private static final float TARGET_FREQ = 1.0f / TARGET_TIME;

    private OrbitCamera camera;
    private Quat originOrient, targetOrient;
    private float originElev, targetElev;
    private float interpT;
    private boolean targetting;

    public CameraController() {
        camera = new OrbitCamera(FOV, NEAR, FAR, 1.0f, START_ELEVATION);
    }

    public void update(float dt) {
        if (targetting) {
            interpT += dt;
            float interpP = interpT * TARGET_FREQ;
            if (interpP > 1.0f) {
                interpP = 1.0f;
                targetting = false;
            }
            interpP = Util.smoothstep(interpP);
            camera.setOrientation(glm.mix(originOrient, targetOrient, interpP).normalizeAssign());
            camera.setElevation(glm.mix(originElev, targetElev, interpP));
        }
    }

    // Distance decreases linearly nearer to the surface
    public void move(Vec2 delta) {
        delta.timesAssign(MOVE_SPEED_FACTOR * glm.clamp((camera.getElevation() - 1.0f) / (START_ELEVATION - 1.0f), 0.0f, 1.0f));
        camera.move(delta);

        targetting = false;
    }

    // Zooms on a parabola rather than a line. Zoom "slows" closer to surface
    public void zoom(float factor) {
        float actualP = (camera.getElevation() - MIN_ELEVATION) / (MAX_ELEVATION - MIN_ELEVATION);
        float easeP = (float)Math.sqrt(actualP);
        easeP += ZOOM_SPEED_FACTOR * factor;
        actualP = glm.clamp(easeP * easeP, 0.0f, 1.0f);
        camera.setElevation(actualP * (MAX_ELEVATION - MIN_ELEVATION) + MIN_ELEVATION);

        targetting = false;
    }

    public void setTarget(Vec3 targetLoc) {
        Vec3 originLoc = camera.getLocation();
        originOrient = new Quat(camera.getOrientation());
        originElev = camera.getElevation();

        camera.setLocation(targetLoc);

        targetOrient = new Quat(camera.getOrientation());
        targetElev = camera.getElevation();

        camera.setLocation(originLoc);

        interpT = 0.0f;
        targetting = true;
    }

    public OrbitCamera getCamera() {
        return camera;
    }

}
