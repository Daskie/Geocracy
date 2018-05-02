package csc309.geocracy.world;

import android.util.Log;
import android.util.Pair;

import csc309.geocracy.MeshMaker;
import csc309.geocracy.graphics.Camera;
import csc309.geocracy.graphics.Mesh;
import glm_.vec3.Vec3;

public class World {

    private final int TESSELLATION_DEGREE = 5; // Should really not change
    private final int MAX_N_TERRITORIES = 40; // Cannot be greater than 63
    private final int MAX_N_CONTINENTS = 15; // Cannot be greater than 15

    private long seed;
    private Terrain terrain;
    private Territory[] territories;
    private Continent[] continents;
    private OceanRenderer oceanRenderer;

    public World(long seed) {
        this.seed = seed;
        Mesh sphereMesh = MeshMaker.makeSphereIndexed("World", TESSELLATION_DEGREE);
        territories = new Territory[MAX_N_TERRITORIES];
        continents = new Continent[MAX_N_CONTINENTS];
        terrain = new Terrain(this, sphereMesh, seed, MAX_N_TERRITORIES, MAX_N_CONTINENTS);
        Pair<Territory[], Continent[]> pair = terrain.retrieveTerrsConts();
        territories = pair.first;
        continents = pair.second;
        oceanRenderer = new OceanRenderer(sphereMesh);
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

    public void render(long t, Camera camera, Vec3 lightDir) {
        terrain.render(t, camera, lightDir);
        oceanRenderer.render(camera, lightDir);
    }

    public void unload() {
        terrain.unload();
        oceanRenderer.unload();
    }

    public void deselectAllTerritories() {
        for (Territory terr : territories) {
            terr.deselect();
        }
    }

    public Territory[] getTerritories() {
        return territories;
    }

    public Continent[] getContinents() {
        return continents;
    }

    Terrain getTerrain() {
        return terrain;
    }
}
