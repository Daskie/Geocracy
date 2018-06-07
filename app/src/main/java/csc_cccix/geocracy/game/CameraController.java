package csc_cccix.geocracy.game;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.graphics.OrbitCamera;
import csc_cccix.geocracy.world.Territory;
import glm_.quat.Quat;
import glm_.vec2.Vec2;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class CameraController {

    private static final float MIN_ELEVATION = 1.5f;
    private static final float MAX_ELEVATION = 5.0f;
    private static final float START_ELEVATION = 3.0f;
    private static final float MOVE_SPEED_FACTOR = 0.05f;
    private static final float ZOOM_SPEED_FACTOR = 20.0f;
    private static final float SLOWDOWN_BASE = 10;
    private static final float FOV = glm.radians(60.0f);
    private static final float NEAR = 0.01f;
    private static final float FAR = MAX_ELEVATION + 1.0f;
    private static final float TARGET_TIME = 0.5f;
    private static final float TARGET_FREQ = 1.0f / TARGET_TIME;

    private OrbitCamera camera;
    private Quat originOrient;
    private Quat targetOrient;
    private float originElev;
    private float targetElev;
    private float interpT;
    private boolean targetting;
    private Vec3 rotVelAxis;
    private float rotVelAngle;
    private float elevPVel;

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
        else if (rotVelAngle != 0.0f || elevPVel != 0.0f) {
            float slowFactor = (float)Math.pow(SLOWDOWN_BASE, -dt);
            if (rotVelAngle != 0.0f) {
                camera.rotate(glm.angleAxis(rotVelAngle * dt, rotVelAxis));
                rotVelAngle *= slowFactor;
                if (Util.isZero(rotVelAngle)) rotVelAngle = 0.0f;
            }
            if (elevPVel != 0.0f) {
                float elevP = (float)Math.sqrt((camera.getElevation() - MIN_ELEVATION) / (MAX_ELEVATION - MIN_ELEVATION));
                elevP += elevPVel * dt;
                if (elevP <= 0.0f) {
                    elevP = 0.0f;
                    elevPVel = 0.0f;
                }
                else if (elevP >= 1.0f) {
                    elevP = 1.0f;
                    elevPVel = 0.0f;
                }
                camera.setElevation(elevP * elevP * (MAX_ELEVATION - MIN_ELEVATION) + MIN_ELEVATION);
                elevPVel *= slowFactor;
                if (Util.isZero(elevPVel)) elevPVel = 0.0f;
            }
        }
    }

    // Distance decreases linearly nearer to the surface
    public void move(Vec2 delta) {
        delta.timesAssign(MOVE_SPEED_FACTOR * glm.clamp((camera.getElevation() - 1.0f) / (START_ELEVATION - 1.0f), 0.0f, 1.0f));

        rotVelAngle = delta.getLength();
        rotVelAxis = new Vec3(Util.orthogonal(delta.div(rotVelAngle)), 0.0f);
        rotVelAxis = camera.getOrientMatrix().times(rotVelAxis); // convert to world space

        targetting = false;
    }

    // Zooms on a parabola rather than a line. Zoom "slows" closer to surface
    public void zoom(float factor) {
        elevPVel = ZOOM_SPEED_FACTOR * factor;

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
        rotVelAngle = 0.0f;
    }

    public void targetTerritory(Territory territory) {
        //setTarget(territory.getCenter().times(camera.getElevation()));
    }

    public OrbitCamera getCamera() {
        return camera;
    }

}
