package csc309.geocracy.world;

import android.util.Log;

import java.util.Random;

import csc309.geocracy.graphics.Camera;
import glm_.vec3.Vec3;

public class World {

    private final int TESSELLATION_DEGREE = 5;

    private long seed;
    private Random rand;
    private Terrain terrain;
    private OceanRenderer oceanRenderer;

    public World(long seed) {
        this.seed = seed;
        rand = new Random(seed);
        terrain = new Terrain(TESSELLATION_DEGREE, rand);
        oceanRenderer = new OceanRenderer(TESSELLATION_DEGREE);
    }

    public boolean load() {
        unload();

        if (!terrain.load()) {
            Log.e("Game", "Failed to load terrain");
            return false;
        }
        if (!oceanRenderer.load()) {
            Log.e("Game", "Failed to load ocean renderer");
            return false;
        }

        return true;
    }

    public void render(Camera camera, Vec3 lightDir) {
        terrain.render(camera, lightDir);
        //oceanRenderer.render(camera, lightDir);
    }

    public void unload() {
        terrain.unload();
        oceanRenderer.unload();
    }

}
