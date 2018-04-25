package csc309.geocracy.world;

import android.util.Log;

import csc309.geocracy.graphics.Shader;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

public class TerrainShader extends Shader {

    protected int viewMatUniformHandle;
    protected int projMatUniformHandle;
    protected int lightDirUniformHandle;
    protected int lowElevationUniformHandle;
    protected int highElevationUniformHandle;
    protected int maxCoastDistUniformHandle;
    protected int continentColorsUniformHandle;

    public TerrainShader() {
        super("Terrain", "shaders/Terrain.vert", "shaders/Terrain.frag");
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

    public void setLowElevation(float elevation) {
        uploadUniform(lowElevationUniformHandle, elevation);
    }

    public void setHighElevation(float elevation) {
        uploadUniform(highElevationUniformHandle, elevation);
    }

    public void setMaxCoastDist(int dist) {
        uploadUniform(maxCoastDistUniformHandle, dist);
    }

    public void setContinentColors(Vec3[] colors) {
        uploadUniform(continentColorsUniformHandle, colors);
    }

    @Override
    protected boolean setupUniforms() {
        if ((viewMatUniformHandle = getUniformLocation("u_viewMat")) == -1) {
            Log.e("TerrainShader", "Failed to get view matrix uniform location");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("TerrainShader", "Failed to get projection matrix uniform location");
        }
        if ((lightDirUniformHandle = getUniformLocation("u_lightDir")) == -1) {
            Log.e("TerrainShader", "Failed to get light uniform location");
        }
        if ((lowElevationUniformHandle = getUniformLocation("u_lowElevation")) == -1) {
            Log.e("TerrainShader", "Failed to get low elevation uniform location");
        }
        if ((highElevationUniformHandle = getUniformLocation("u_highElevation")) == -1) {
            Log.e("TerrainShader", "Failed to get high elevation uniform location");
        }
        if ((maxCoastDistUniformHandle = getUniformLocation("u_maxCoastDist")) == -1) {
            Log.e("TerrainShader", "Failed to get max coast distance uniform location");
        }
        if ((continentColorsUniformHandle = getUniformLocation("u_continentColors")) == -1) {
            Log.e("TerrainShader", "Failed to get continent colors uniform location");
        }

        return true;
    }

}
