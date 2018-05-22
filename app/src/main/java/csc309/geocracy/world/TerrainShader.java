package csc309.geocracy.world;

import android.util.Log;

import csc309.geocracy.game.Player;
import csc309.geocracy.graphics.Shader;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

public class TerrainShader extends Shader {

    private int viewMatUniformHandle;
    private int projMatUniformHandle;
    private int lightDirUniformHandle;
    private int timeUniformHandle;
    private int lowElevationFactorUniformHandle;
    private int highElevationFactorUniformHandle;
    private int continentColorsUniformHandle;
    private int selectedTerritoryHandle;
    private int highlightedTerritoriesLowerHandle;
    private int highlightedTerritoriesUpperHandle;
    private int playerColorsHandle;
    private int territoryPlayersHandle;

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

    public void setLowElevationFactor(float factor) {
        uploadUniform(lowElevationFactorUniformHandle, factor);
    }

    public void setHighElevationFactor(float factor) {
        uploadUniform(highElevationFactorUniformHandle, factor);
    }
    public void setContinentColors(Vec3[] colors) {
        uploadUniform(continentColorsUniformHandle, colors);
    }

    public void setSelectedTerritory(int selected) {
        uploadUniform(selectedTerritoryHandle, selected, true);
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
        uploadUniform(highlightedTerritoriesLowerHandle, highlightedLower, true);
        uploadUniform(highlightedTerritoriesUpperHandle, highlightedUpper, true);
    }

    public void setPlayerColors(Player[] players) {
        Vec3[] colors = new Vec3[players.length + 1];
        colors[0] = new Vec3();
        for (int i = 0; i < players.length; ++i) {
            colors[i + 1] = players[i].getColor();
        }
        uploadUniform(playerColorsHandle, colors);
    }

    public void setTerritoryPlayers(Territory[] territories) {
        int[] terrPlayers = new int[territories.length + 1];
        for (int i = 0; i < territories.length; ++i) {
            terrPlayers[i + 1] = territories[i].getOwner().getId();
        }
        uploadUniform(territoryPlayersHandle, terrPlayers, true);
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
        if ((lowElevationFactorUniformHandle = getUniformLocation("u_lowElevationFactor")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_lowElevationFactor");
        }
        if ((highElevationFactorUniformHandle = getUniformLocation("u_highElevationFactor")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_highElevationFactor");
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
        if ((playerColorsHandle = getUniformLocation("u_playerColors")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_playerColors");
        }
        if ((territoryPlayersHandle = getUniformLocation("u_territoryPlayers")) == -1) {
            Log.e("TerrainShader", "Failed to get location of u_territoryPlayers");
        }

        return true;
    }

}
