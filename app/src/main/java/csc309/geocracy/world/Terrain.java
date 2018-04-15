package csc309.geocracy.world;

import android.util.Log;

import csc309.geocracy.graphics.Camera;
import csc309.geocracy.noise.SimplexNoise;
import glm_.vec3.Vec3;

public class Terrain {

    private TerrainShader shader;
    private TerrainMesh mesh;
    private float highElevation, lowElevation;

    public Terrain(int tessellationDegree, long seed) {
        shader = new TerrainShader();
        mesh = new TerrainMesh(tessellationDegree);
        highElevation = 1.1f;
        lowElevation = 0.95f;

        terraform(seed);
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("Terrain", "Failed to load shader");
            return false;
        }

        if (!mesh.load()) {
            Log.e("Terrain", "Failed to load mesh");
            return false;
        }

        shader.setActive();
        shader.setLowElevation(lowElevation);
        shader.setHighElevation(highElevation);

        return true;
    }

    public void render(Camera camera, Vec3 lightDir) {
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        shader.setLightDirection(lightDir);
        mesh.render();
    }

    public void unload() {
        shader.unload();
        mesh.unload();
    }

    private void terraform(long seed) {
        final int N_OCTAVES = 5;
        final float INIT_FREQUENCY = 1.0f;
        final float PERSISTENCE = 0.5f;

        SimplexNoise simplex = new SimplexNoise(seed);
        float maxAmplitude = ((float)(1 << N_OCTAVES) - 1) / (float)(1 << (N_OCTAVES - 1));
        float adjustFactor = 1.0f / ((maxAmplitude + 1.0f) * 0.5f);
        float superRange = highElevation - 1.0f;
        float subRange = 1.0f - lowElevation;

        float[] locations = mesh.getLocations();
        int nVertices = locations.length / 3;
        for (int i = 0; i < nVertices; ++i) {
            int ci = i * 3;

            float v = 0.0f;
            float frequency = INIT_FREQUENCY;
            float amplitude = 1.0f;
            for (int octave = 0; octave < N_OCTAVES; ++octave) {
                v += simplex.noise(
                    locations[ci + 0] * frequency,
                    locations[ci + 1] * frequency,
                    locations[ci + 2] * frequency
                ) * amplitude;
                frequency *= 2.0f;
                amplitude *= PERSISTENCE;
            }
            v *= adjustFactor; // in range [-1.0, 1.0]

            // Super transformation (make mountainous)
            if (v >= 0.0f) {
                v *= v * v; // [-1.0, 1.0]
            }
            // Sub transformation (make gradual drop-off)
            else {
                if (v >= -0.5f) {
                    v = -2.0f * v * v;
                }
                else {
                    v = 2.0f * (v + 1.0f) * (v + 1.0f) - 1.0f;
                }
            }

            if (v >= 0.0f) {
                v = 1.0f + v * superRange; // [lowElevation, highElevation]
            }
            else {
                v = 1.0f + v * subRange; // [lowElevation, highElevation]
            }
            locations[ci + 0] *= v;
            locations[ci + 1] *= v;
            locations[ci + 2] *= v;
        }
    }

}
