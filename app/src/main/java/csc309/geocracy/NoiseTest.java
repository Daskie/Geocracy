package csc309.geocracy;

import android.util.Log;

import glm_.vec2.Vec2i;

public class NoiseTest {

    private ScreenTextureRenderer renderer;
    private Texture texture;
    private byte[] image;
    private int width, height;

    public NoiseTest() {
        width = 1024; height = 1024;
        renderer = new ScreenTextureRenderer();
        texture = new Texture("Noise", new Vec2i(width, height), Texture.Wrap.EDGE, Texture.Filter.LINEAR, false);
        image = new byte[width * height * 3];
        generateNoise();
    }

    public boolean load() {
        unload();

        if (!renderer.load()) {
            Log.e("NoiseTest", "Failed to load renderer");
            return false;
        }
        if (!texture.load()) {
            Log.e("NoiseTest", "Failed to load texture");
            return false;
        }
        if (!texture.upload(image)) {
            Log.e("NoiseText", "Failed to upload texture");
            return false;
        }

        return true;
    }

    public void render() {
        renderer.render(texture);
    }

    public void unload() {
        renderer.unload();
        texture.unload();
    }

    private void generateNoise() {
        SimplexNoise simplex = new SimplexNoise(0);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                double px = (double)x / width;
                double py = (double)y / height;
                double v = 0.0;
                for (int i = 0; i < 1; ++i) {
                    v += simplex.noise(px * 2 * (1 << i), py * 2 * (1 << i)) / (1 << i);
                }
                v = v * 0.25 + 0.5;
                //v = 1.0 - Math.abs(v) * 0.5;
                byte bv = (byte)(v * 255.0);
                int i = (y * width + x) * 3;
                image[i + 0] = bv;
                image[i + 1] = bv;
                image[i + 2] = bv;
            }
        }
    }

}
