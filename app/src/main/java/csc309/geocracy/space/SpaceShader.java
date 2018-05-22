package csc309.geocracy.space;

import android.util.Log;

import csc309.geocracy.graphics.Shader;
import glm_.mat4x4.Mat4;

public class SpaceShader extends Shader {

    private int viewMatUniformHandle;
    private int projMatUniformHandle;
    private int cubemapUniformHandle;

    public SpaceShader() {
        super("Space", "shaders/Space.vert", "shaders/Space.frag");
    }

    // Shader program must be active when any glUniform call happens!
    public void setViewMatrix(Mat4 matrix) {
        uploadUniform(viewMatUniformHandle, matrix);
    }

    public void setProjectionMatrix(Mat4 matrix) {
        uploadUniform(projMatUniformHandle, matrix);
    }

    @Override
    protected boolean setupUniforms() {
        if ((viewMatUniformHandle = getUniformLocation("u_viewMat")) == -1) {
            Log.e("SpaceShader", "Failed to get location of u_viewMat");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("SpaceShader", "Failed to get location of u_projMat");
        }
        if ((cubemapUniformHandle = getUniformLocation("u_cubemap")) == -1) {
            Log.e("SpaceShader", "Failed to get location of u_cubemap");
        }

        if (cubemapUniformHandle != -1) {
            setActive();
            uploadUniform(cubemapUniformHandle, 0, true);
        }

        return true;
    }

}
