package csc309.geocracy;

import glm_.mat3x3.Mat3;
import glm_.mat4x4.Mat4;
import glm_.quat.Quat;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public abstract class Camera {

    protected static final float DEF_FOV = glm.radians(90.0f);
    protected static final float DEF_NEAR = 0.01f;
    protected static final float DEF_FAR = 10.0f;

    protected float fov;
    protected float near;
    protected float far;

    protected Camera() {
        fov = DEF_FOV;
        near = DEF_NEAR;
        far = DEF_FAR;
    }

    public abstract Quat getOrientation();

    public abstract Mat3 getOrientMatrix();

    public abstract Vec3 getU();

    public abstract Vec3 getV();

    public abstract Vec3 getW();

    public abstract Vec3 getLocation();

    public Mat4 getViewMatrix() {
        Vec3 t = getLocation().negate();
        Vec3 u = getU();
        Vec3 v = getV();
        Vec3 w = getW();
        return new Mat4(
            u.x,      v.x,      w.x,      0.0f,
            u.y,      v.y,      w.y,      0.0f,
            u.z,      v.z,      w.z,      0.0f,
            u.dot(t), v.dot(t), w.dot(t), 1.0f
        );
    }

    public Mat4 getProjectionMatrix(float aspectRatio) {
        return glm.perspective(fov, aspectRatio, near, far);
    }

}
