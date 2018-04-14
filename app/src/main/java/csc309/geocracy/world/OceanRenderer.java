package csc309.geocracy.world;

import android.util.Log;

import csc309.geocracy.BasicShader;
import csc309.geocracy.MeshMaker;
import csc309.geocracy.graphics.Camera;
import csc309.geocracy.graphics.Mesh;
import glm_.mat3x3.Mat3;
import glm_.mat4x4.Mat4;

public class OceanRenderer {

    private BasicShader shader;
    private Mesh mesh;

    public OceanRenderer() {
        shader = new BasicShader();
        mesh = MeshMaker.makeSphereIndexed("Ocean", 5);
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

    public void render(Camera camera) {
        shader.setActive();
        shader.setModelMatrix(new Mat4());
        shader.setNormalMatrix(new Mat3());
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        mesh.render();
    }

    public void unload() {
        shader.unload();
        mesh.unload();
    }

}
