package csc309.geocracy.world;

import android.util.Log;

import csc309.geocracy.MeshMaker;
import csc309.geocracy.graphics.Camera;
import csc309.geocracy.graphics.Mesh;
import glm_.vec3.Vec3;

public class OceanRenderer {

    private OceanShader shader;
    private Mesh mesh;

    public OceanRenderer(int tessellationDegree) {
        shader = new OceanShader();
        mesh = MeshMaker.makeSphereIndexed("Ocean", tessellationDegree);
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("OceanRenderer", "Failed to load shader");
            return false;
        }
        if (!mesh.load()) {
            Log.e("OceanRenderer", "Failed to load mesh");
            return false;
        }

        return true;
    }

    public void render(Camera camera, Vec3 lightDir) {
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        shader.setCameraLocation(camera.getLocation());
        shader.setLightDirection(lightDir);
        mesh.render();
    }

    public void unload() {
        shader.unload();
        mesh.unload();
    }

}
