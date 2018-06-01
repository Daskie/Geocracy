package cscCCCIX.geocracy.world;

import android.util.Log;

import cscCCCIX.geocracy.graphics.Shader;
import glm_.mat4x4.Mat4;

public class IdentityShader extends Shader {

    private int viewMatUniformHandle;
    private int projMatUniformHandle;

    public IdentityShader() {
        super("Terrain", "shaders/Identity.vert", "shaders/Identity.frag");
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
            Log.e("TerrainShader", "Failed to get location of u_viewMat");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_projMat");
        }

        return true;
    }

}
