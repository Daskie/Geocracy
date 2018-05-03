package csc309.geocracy.graphics;

import csc309.geocracy.Util;
import glm_.mat3x3.Mat3;
import glm_.quat.Quat;
import glm_.vec2.Vec2;
import glm_.vec3.Vec3;

import static android.support.v4.math.MathUtils.clamp;
import static glm_.Java.glm;

// Camera that resides at a certain radius from the origin and always looks at the origin
public class OrbitCamera extends Camera {

    private float minElevation;
    private float maxElevation;
    private float elevation;
    private Quat orientation;
    private Mat3 orientMatrix;

    public OrbitCamera(float fov, float near, float far, float aspectRatio, float minElevation, float maxElevation, float elevation) {
        super(fov, near, far, aspectRatio);
        this.minElevation = minElevation;
        this.maxElevation = maxElevation;
        this.elevation = elevation;
        orientation = new Quat();
        orientMatrix = new Mat3();
    }

    // Rotates the camera relatively in world space by the given amount
    public void rotate(Quat rotation) {
        orientation = rotation.times(orientation);
        orientation.normalizeAssign();
        orientMatrix = orientation.toMat3();
        viewMatrix = null;
    }

    public void setElevation(float elevation) {
        this.elevation = clamp(elevation, minElevation, maxElevation);
        viewMatrix = null;
    }

    public void changeElevation(float delta) {
        setElevation(elevation + delta);
    }

    // Zooms on a parabola rather than a line. Zoom "slows" closer to surface
    public void easeElevation(float delta) {
        float actualP = (elevation - minElevation) / (maxElevation - minElevation);
        float easeP = (float)Math.sqrt(actualP);
        easeP += delta;
        actualP = easeP * easeP;
        actualP = glm.clamp(actualP, 0.0f, 1.0f);
        setElevation(actualP * (maxElevation - minElevation) + minElevation);
    }

    public void setLocation(Vec3 location) {
        Vec3 a = getW();
        Vec3 b = location.normalize();
        float angle = (float)Math.acos(a.dot(b));
        Vec3 axis = a.cross(b);
        rotate(glm.angleAxis(angle, axis));
    }

    // Moves the camera in orbit where delta corresponds to the camera's u and v vectors
    public void move(Vec2 delta) {
        float angle = delta.getLength();
        Vec3 axis = new Vec3(Util.orthogonal(delta.div(angle)), 0.0f);
        axis = orientMatrix.times(axis); // convert to world space
        rotate(glm.angleAxis(angle, axis));
    }

    public float getElevation() { return elevation; }

    @Override
    public Quat getOrientation() { return orientation; }

    @Override
    public Mat3 getOrientMatrix() { return orientMatrix; }

    @Override
    public Vec3 getU() {
        return orientMatrix.get(0);
    }

    @Override
    public Vec3 getV() {
        return orientMatrix.get(1);
    }

    @Override
    public Vec3 getW() {
        return orientMatrix.get(2);
    }

    @Override
    public Vec3 getLocation() {
        return getW().times(elevation);
    }

}
