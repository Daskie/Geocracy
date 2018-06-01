package cscCCCIX.geocracy.world;

import android.util.Log;

import cscCCCIX.geocracy.game.Player;
import cscCCCIX.geocracy.graphics.Shader;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

public class ArmyShader extends Shader {

    private int viewMatUniformHandle;
    private int projMatUniformHandle;
    private int cameraLocUniformHandle;
    private int lightDirUniformHandle;
    private int playerColorsHandle;

    public ArmyShader() {
        super("Army", "shaders/Army.vert", "shaders/Army.frag");
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

    public void setPlayerColors(Player[] players) {
        Vec3[] colors = new Vec3[players.length + 1];
        colors[0] = new Vec3();
        for (int i = 0; i < players.length; ++i) {
            colors[i + 1] = players[i].getColor();
        }
        uploadUniform(playerColorsHandle, colors);
    }

    @Override
    protected boolean setupUniforms() {
        if ((viewMatUniformHandle = getUniformLocation("u_viewMat")) == -1) {
            Log.e("ArmyShader", "Failed to get location of u_viewMat");
        }
        if ((projMatUniformHandle = getUniformLocation("u_projMat")) == -1) {
            Log.e("ArmyShader", "Failed to get location of u_projMat");
        }
        if ((cameraLocUniformHandle = getUniformLocation("u_cameraLoc")) == -1) {
            Log.e("ArmyShader", "Failed to get location of u_cameraLoc");
        }
        if ((lightDirUniformHandle = getUniformLocation("u_lightDir")) == -1) {
            Log.e("ArmyShader", "Failed to get location of u_lightDir");
        }
        if ((playerColorsHandle = getUniformLocation("u_playerColors")) == -1) {
            Log.e("ArmyShader", "Failed to get location of u_playerColors");
        }

        return true;
    }

}
