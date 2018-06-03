package csc_cccix.geocracy.world;

import android.util.Log;

import csc_cccix.geocracy.graphics.Shader;
import glm_.mat3x3.Mat3;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

public class ArrowShader extends Shader {

    private int viewMatUniformHandle;
    private int projMatUniformHandle;
    private int angleUniformHandle;
    private int basisUniformHandle;
    private int timeUniformHandle;
    private int colorUniformHandle;

    public ArrowShader() {
        super("Arrow", "shaders/Arrow.vert", "shaders/Arrow.frag");
    }

    // Shader program must be active when any glUniform call happens!
    public void setViewMatrix(Mat4 matrix) {
        uploadUniform(viewMatUniformHandle, matrix);
    }

    public void setProjectionMatrix(Mat4 matrix) {
        uploadUniform(projMatUniformHandle, matrix);
    }

    public void setAngle(float angle) {
        uploadUniform(angleUniformHandle, angle);
    }

    public void setBasis(Mat3 basis) {
        uploadUniform(basisUniformHandle, basis);
    }

    public void setTime(float time) {
        uploadUniform(timeUniformHandle, time);
    }

    public void setColor(Vec3 color) {
        uploadUniform(colorUniformHandle, color);
    }

    @Override
    protected boolean setupUniforms() {
        if ((viewMatUniformHandle = getUniformLocation("u_viewMat")) == -1) {
            Log.e("", "Failed to get location of u_viewMat");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("", "Failed to get location of u_projMat");
        }
        if ((angleUniformHandle = getUniformLocation("u_angle")) == -1) {
            Log.e("", "Failed to get location of u_angle");
        }
        if ((basisUniformHandle = getUniformLocation("u_basis")) == -1) {
            Log.e("", "Failed to get location of u_basis");
        }
        if ((timeUniformHandle = getUniformLocation("u_time")) == -1) {
            Log.e("", "Failed to get location of u_time");
        }
        if ((colorUniformHandle = getUniformLocation("u_color")) == -1) {
            Log.e("", "Failed to get location of u_color");
        }

        return true;
    }

}
