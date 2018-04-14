package csc309.geocracy;

import android.util.Log;

import csc309.geocracy.graphics.Shader;

// Simply draws texture over screen
public class ScreenTextureShader extends Shader {

    protected int textureUniformHandle;

    public ScreenTextureShader() {
        super("Noise", "shaders/ScreenTexture.vert", "shaders/ScreenTexture.frag");
    }

    @Override
    protected boolean setupUniforms() {
        boolean success = true;

        if ((textureUniformHandle = getUniformLocation("u_texture")) == -1) {
            Log.e("ScreenTextureShader", "Failed to get texture uniform location");
            success = false;
        }

        if (!success) {
            return false;
        }

        setActive();
        uploadUniform(textureUniformHandle, 0);

        return true;
    }

}
