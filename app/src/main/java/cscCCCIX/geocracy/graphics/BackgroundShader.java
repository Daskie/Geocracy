package cscCCCIX.geocracy.graphics;
import android.util.Log;

import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

public class BackgroundShader extends Shader {
    protected int viewMatUniformHandle;
    protected int projMatUniformHandle;
    protected int lightDirUniformHandle;
    protected int timeUniformHandle;


    public BackgroundShader() {
        super("Background", "shaders/Background.vert", "shaders/Background.frag");
    }

    // Shader program must be active when any glUniform call happens!
    public void setViewMatrix(Mat4 matrix) {
        uploadUniform(viewMatUniformHandle, matrix);
    }

    public void setProjectionMatrix(Mat4 matrix) {
        uploadUniform(projMatUniformHandle, matrix);
    }

    public void setLightDirection(Vec3 dir) {
        uploadUniform(lightDirUniformHandle, dir);
    }

    public void setTimeFloat(float time) { uploadUniform(timeUniformHandle, time); }


    @Override
    protected boolean setupUniforms() {
        if ((viewMatUniformHandle = getUniformLocation("u_viewMat")) == -1) {
            Log.e("", "Failed to get view matrix uniform location");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("", "Failed to get projection matrix uniform location");
        }
        if ((lightDirUniformHandle = getUniformLocation("u_lightDir")) == -1) {
            Log.e("", "Failed to get light uniform location");
        }
        if ((timeUniformHandle = getUniformLocation("u_time")) == -1) {
            Log.e("", "Failed to get light uniform location");
        }

        return true;
    }
}
