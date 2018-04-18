package csc309.geocracy;

import android.util.Log;

import csc309.geocracy.graphics.Camera;
import glm_.vec3.Vec3;

public class Background {
    private BackgroundShader shader;
    private BackgroundMesh mesh;

    public Background() {
        shader = new BackgroundShader();
        mesh = new BackgroundMesh();

    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("Background", "Failed to load shader");
            return false;
        }

        if (!mesh.load()) {
            Log.e("Background", "Failed to load mesh");
            return false;
        }

        shader.setActive();

        return true;
    }
    public void render(Camera camera) {
        shader.setActive();
//        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
//        shader.setLightDirection(lightDir);
        mesh.render();
    }

    public void unload() {
        shader.unload();
        mesh.unload();
    }
}
