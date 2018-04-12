package csc309.geocracy;

import android.opengl.GLES30;
import android.util.Log;

import glm_.mat3x3.Mat3;
import glm_.mat4x4.Mat4;
import glm_.vec3.Vec3;

public class World {

    private final int TESSELLATION_DEGREE = 6;

    private BasicShader shader;
    private Mesh mesh;
    private long seed;

    public World(long seed) {
        shader = new BasicShader();
        mesh = MeshMaker.makeSphereUnindexed("World", TESSELLATION_DEGREE);
        terraform();
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("Game", "Failed to load shader");
            return false;
        }

        if (!mesh.load()) {
            Log.e("Game", "Failed to load mesh");
            return false;
        }

        return true;
    }

    public void render(Camera camera, float aspectRatio) {
        // Render terrain
        shader.setActive();
        shader.setModelMatrix(new Mat4());
        shader.setNormalMatrix(new Mat3());
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix(aspectRatio));
        GLES30.glBindVertexArray(mesh.getVAOHandle());
        if (mesh.getNumIndices() == 0) {
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mesh.getNumVertices());
        }
        else {
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, mesh.getNumIndices(), GLES30.GL_UNSIGNED_INT, 0);
        }
        GLES30.glBindVertexArray(0);
    }

    public void unload() {
        shader.unload();
        mesh.unload();
    }

    private void terraform() {
        final int N_OCTAVES = 8;
        final double INIT_FREQUENCY = 2.0;
        final double PERSISTENCE = 0.5;
        final double LOW = 0.75, HIGH = 1.25;

        SimplexNoise simplex = new SimplexNoise(seed);
        double maxAmplitude = ((double)(1 << N_OCTAVES) - 1) / (double)(1 << (N_OCTAVES - 1));
        double invMaxAmplitude = 1.0 / maxAmplitude;

        Vec3[] locations = mesh.getLocations();
        for (int i = 0; i < locations.length; ++i) {
            Vec3 location = locations[i];

            double v = 0.0;
            double frequency = INIT_FREQUENCY;
            double amplitude = 1.0f;
            for (int octave = 0; octave < N_OCTAVES; ++octave) {
                v += simplex.noise(location.x * frequency, location.y * frequency, location.z * frequency) * amplitude;
                frequency *= 2.0;
                amplitude *= PERSISTENCE;
            }
            v *= invMaxAmplitude; // in range [-1.0, 1.0]
            v = 1.0 - Math.abs(v); // in range [0.0, 1.0]
            v = v * (HIGH - LOW) + LOW; // in range [LOW, HIGH]

            if (!Util.areEqual(locations[i].getLength2(), 1.0f)) {
                int x = 0;
            }
            locations[i].timesAssign((float)v);
        }

        mesh.calcFaceNormals();
    }

}
