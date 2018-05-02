package csc309.geocracy.world;

import android.util.Log;

import csc309.geocracy.graphics.Shader;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

public class OceanShader extends Shader {

    protected int viewMatUniformHandle;
    protected int projMatUniformHandle;
    protected int cameraLocUniformHandle;
    protected int lightDirUniformHandle;

    public OceanShader() {
        super("Terrain", "shaders/Ocean.vert", "shaders/Ocean.frag");
    }

    // Shader program must be active when any glUniform call happens!
    public void setViewMatrix(Mat4 matrix) {
        uploadUniform(viewMatUniformHandle, matrix);
    }

    public void setProjectionMatrix(Mat4 matrix) {
        uploadUniform(projMatUniformHandle, matrix);
    }

    public void setCameraLocation(Vec3 loc) {
        uploadUniform(cameraLocUniformHandle, loc);
    }

    public void setLightDirection(Vec3 dir) {
        uploadUniform(lightDirUniformHandle, dir);
    }

    @Override
    protected boolean setupUniforms() {
        if ((viewMatUniformHandle = getUniformLocation("u_viewMat")) == -1) {
            Log.e("OceanShader", "Failed to get location of u_viewMat");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("OceanShader", "Failed to get location of u_projMat");
        }
        if ((cameraLocUniformHandle = getUniformLocation("u_cameraLoc")) == -1) {
            Log.e("OceanShader", "Failed to get location of u_cameraLoc");
        }
        if ((lightDirUniformHandle = getUniformLocation("u_lightDir")) == -1) {
            Log.e("OceanShader", "Failed to get location of u_lightDir");
        }

        return true;
    }

}
