package csc309.geocracy.world;

import android.util.Log;

import csc309.geocracy.Util;
import csc309.geocracy.graphics.Camera;
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

        mesh.calcTerritorySpawns(40);
        //mesh.terraform(seed, highElevation, lowElevation);
        mesh.calcCoastDistance();
        mesh.spreadTerritories();
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
        shader.setMaxCoastDist(mesh.maxCoastDist);
        shader.setContinentColors(genContinentColors(40));

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

    private Vec3[] genContinentColors(int nContinents) {
        Vec3[] colors = new Vec3[nContinents];
        for(int i = 0; i < nContinents; ++i) {
            colors[i] = Util.hsv2rgb((float)i / nContinents, 1.0f, 1.0f);
        }
        return colors;
    }

}
