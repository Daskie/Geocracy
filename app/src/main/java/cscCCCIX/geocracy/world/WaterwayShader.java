package cscCCCIX.geocracy.world;

import android.util.Log;

import cscCCCIX.geocracy.graphics.Shader;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

public class WaterwayShader extends Shader {

    private int viewMatUniformHandle;
    private int projMatUniformHandle;
    private int timeUniformHandle;
    private int lightDirUniformHandle;
    private int continentColorsUniformHandle;
    private int selectedTerritoryHandle;

    public WaterwayShader() {
        super("Waterway", "shaders/Waterway.vert", "shaders/Waterway.frag");
    }

    // Shader program must be active when any glUniform call happens!
    public void setViewMatrix(Mat4 matrix) {
        uploadUniform(viewMatUniformHandle, matrix);
    }

    public void setProjectionMatrix(Mat4 matrix) {
        uploadUniform(projMatUniformHandle, matrix);
    }

    public void setTime(float time) {
        uploadUniform(timeUniformHandle, time);
    }

    public void setLightDirection(Vec3 dir) {
        uploadUniform(lightDirUniformHandle, dir);
    }

    public void setContinentColors(Vec3[] colors) {
        uploadUniform(continentColorsUniformHandle, colors);
    }

    public void setSelectedTerritory(int ti) {
        uploadUniform(selectedTerritoryHandle, ti, true);
    }

    @Override
    protected boolean setupUniforms() {
        if ((viewMatUniformHandle = getUniformLocation("u_viewMat")) == -1) {
            Log.e("WaterwayShader", "Failed to get location of u_viewMat");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("WaterwayShader", "Failed to get location of u_projMat");
        }
        if ((timeUniformHandle = getUniformLocation("u_time")) == -1) {
            Log.e("WaterwayShader", "Failed to get location of u_time");
        }
        if ((lightDirUniformHandle = getUniformLocation("u_lightDir")) == -1) {
            Log.e("WaterwayShader", "Failed to get location of u_lightDir");
        }
        if ((continentColorsUniformHandle = getUniformLocation("u_continentColors")) == -1) {
            Log.e("WaterwayShader", "Failed to get location of u_continentColors");
        }
        if ((selectedTerritoryHandle = getUniformLocation("u_selectedTerritory")) == -1) {
            Log.e("WaterwayShader", "Failed to get location of u_selectedTerritory");
        }

        return true;
    }

}
