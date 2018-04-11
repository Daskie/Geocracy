package csc309.geocracy;

import android.util.Log;

import glm_.mat3x3.Mat3;
import glm_.mat4x4.Mat4;

public class BasicShader extends Shader {

    protected int modelMatUniformHandle;
    protected int normMatUniformHandle;
    protected int viewMatUniformHandle;
    protected int projMatUniformHandle;

    public BasicShader() {
        super("Basic", "shaders/Basic.vert", "shaders/Basic.frag");
    }

    // Shader program must be active when any glUniform call happens!
    public void setModelMatrix(Mat4 matrix) {
        uploadUniform(modelMatUniformHandle, matrix);
    }
    public void setNormalMatrix(Mat3 matrix) {
        uploadUniform(normMatUniformHandle, matrix);
    }
    public void setViewMatrix(Mat4 matrix) {
        uploadUniform(viewMatUniformHandle, matrix);
    }
    public void setProjectionMatrix(Mat4 matrix) {
        uploadUniform(projMatUniformHandle, matrix);
    }

    @Override
    protected boolean setupUniforms() {
        if ((modelMatUniformHandle = getUniformLocation("u_modelMat")) == -1) {
            Log.e("Basic Shader", "Failed to get color uniform location");
        }
        if ((normMatUniformHandle = getUniformLocation("u_normMat")) == -1) {
            Log.e("Basic Shader", "Failed to get color uniform location");
        }
        if ((viewMatUniformHandle = getUniformLocation("u_viewMat")) == -1) {
            Log.e("Basic Shader", "Failed to get color uniform location");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("Basic Shader", "Failed to get color uniform location");
        }

        return true;
    }

}
