package csc309.geocracy.world;

import android.util.Log;

import csc309.geocracy.graphics.Shader;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

public class TerrainShader extends Shader {

    protected int viewMatUniformHandle;
    protected int projMatUniformHandle;
    protected int lightDirUniformHandle;
    protected int timeUniformHandle;
    protected int lowElevationUniformHandle;
    protected int highElevationUniformHandle;
    protected int maxCoastDistUniformHandle;
    protected int continentColorsUniformHandle;
    protected int selectedTerritoryHandle;
    protected int highlightedTerritoriesLowerHandle;
    protected int highlightedTerritoriesUpperHandle;

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

    public void setTime(float time) {
        uploadUniform(timeUniformHandle, time);
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

    public void setSelectedTerritory(int selected) {
        uploadUniform(selectedTerritoryHandle, selected);
    }

    public void setHighlightedTerritories(boolean[] highlighted) {
        int highlightedLower = 0, highlightedUpper = 0;
        if (highlighted != null) {
            for (int i = 0; i < 32 && i < highlighted.length; ++i) {
                highlightedLower |= (highlighted[i] ? 1 : 0) << i;
            }
            for (int i = 0; i < 32 && i + 32 < highlighted.length; ++i) {
                highlightedUpper |= (highlighted[i + 32] ? 1 : 0) << i;
            }
        }
        uploadUniform(highlightedTerritoriesLowerHandle, highlightedLower);
        uploadUniform(highlightedTerritoriesUpperHandle, highlightedUpper);
    }

    @Override
    protected boolean setupUniforms() {
        if ((viewMatUniformHandle = getUniformLocation("u_viewMat")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_viewMat");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_projMat");
        }
        if ((lightDirUniformHandle = getUniformLocation("u_lightDir")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_lightDir");
        }
        if ((timeUniformHandle = getUniformLocation("u_time")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_time");
        }
        if ((lowElevationUniformHandle = getUniformLocation("u_lowElevation")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_lowElevation");
        }
        if ((highElevationUniformHandle = getUniformLocation("u_highElevation")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_highElevation");
        }
        if ((maxCoastDistUniformHandle = getUniformLocation("u_maxCoastDist")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_maxCoastDist");
        }
        if ((continentColorsUniformHandle = getUniformLocation("u_continentColors")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_continentColors");
        }
        if ((selectedTerritoryHandle = getUniformLocation("u_selectedTerritory")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_selectedTerritory");
        }
        if ((highlightedTerritoriesLowerHandle = getUniformLocation("u_highlightedTerritoriesLower")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_highlightedTerritoriesLower");
        }
        if ((highlightedTerritoriesUpperHandle = getUniformLocation("u_highlightedTerritoriesUpper")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_highlightedTerritoriesUpper");
        }

        return true;
    }

}
