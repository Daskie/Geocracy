package csc_cccix.geocracy.backend.graphics;

import android.util.Log;

// Simply draws texture over screen
public class ScreenTextureShader extends Shader {

    private int textureUniformHandle;

    public ScreenTextureShader() {
        super("Noise", "shaders/ScreenTexture.vert", "shaders/ScreenTexture.frag");
    }

    @Override
    protected boolean setupUniforms() {
        if ((textureUniformHandle = getUniformLocation("u_texture")) == -1) {
            Log.e("", "Failed to get location of u_texture");
        }

        if (textureUniformHandle != -1) {
            setActive();
            uploadUniform(textureUniformHandle, 0, true);
        }

        return true;
    }

}
