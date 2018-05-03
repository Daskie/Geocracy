package csc309.geocracy.graphics;

import android.util.Log;

// Simply draws texture over screen
public class ScreenTextureShader extends Shader {

    private int textureUniformHandle;

    public ScreenTextureShader() {
        super("Noise", "shaders/ScreenTexture.vert", "shaders/ScreenTexture.frag");
    }

    @Override
    protected boolean setupUniforms() {
        boolean success = true;

        if ((textureUniformHandle = getUniformLocation("u_texture")) == -1) {
            Log.e("ScreenTextureShader", "Failed to get location of u_texture");
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
