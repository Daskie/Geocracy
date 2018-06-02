package csc_cccix.geocracy.graphics;

import glm_.mat3x3.Mat3;
import glm_.mat4x4.Mat4;
import glm_.quat.Quat;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public abstract class Camera {

    protected float fov;
    protected float near;
    protected float far;
    protected float aspectRatio;
    protected Mat4 viewMatrix;
    protected Mat4 projMatrix;

    protected Camera(float fov, float near, float far, float aspectRatio) {
        this.fov = fov;
        this.near = near;
        this.far = far;
        this.aspectRatio = aspectRatio;
    }

    public void setFov(float fov) {
        this.fov = fov;
        projMatrix = null;
    }

    public void setNear(float near) {
        this.near = near;
        projMatrix = null;
    }

    public void setFar(float far) {
        this.far = far;
        projMatrix = null;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        projMatrix = null;
    }

    public abstract Quat getOrientation();

    public abstract Mat3 getOrientMatrix();

    public abstract Vec3 getU();

    public abstract Vec3 getV();

    public abstract Vec3 getW();

    public abstract Vec3 getLocation();

    public Mat4 getViewMatrix() {
        if (viewMatrix == null) {
            Vec3 t = getLocation().negate();
            Vec3 u = getU();
            Vec3 v = getV();
            Vec3 w = getW();
            viewMatrix = new Mat4(
                u.x,      v.x,      w.x,      0.0f,
                u.y,      v.y,      w.y,      0.0f,
                u.z,      v.z,      w.z,      0.0f,
                u.dot(t), v.dot(t), w.dot(t), 1.0f
            );
        }
        return viewMatrix;
    }

    public Mat4 getProjectionMatrix() {
        if (projMatrix == null) {
            projMatrix = glm.perspective(fov, aspectRatio, near, far);
        }
        return projMatrix;
    }

}
