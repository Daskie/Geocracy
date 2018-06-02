package csc_cccix.geocracy.world;

import android.opengl.GLES30;
import android.util.Log;

import csc_cccix.geocracy.graphics.Camera;
import csc_cccix.geocracy.graphics.Mesh;
import glm_.vec3.Vec3;

public class OceanRenderer {

    private OceanShader shader;
    private Mesh mesh;

    public OceanRenderer(Mesh sphereMesh) {
        shader = new OceanShader();
        mesh = sphereMesh;
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("", "Failed to load shader");
            return false;
        }
        if (!mesh.load()) {
            Log.e("", "Failed to load mesh");
            return false;
        }

        return true;
    }

    public void render(Camera camera, Vec3 lightDir, int cubemapHandle) {
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        shader.setCameraLocation(camera.getLocation());
        shader.setLightDirection(lightDir);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, cubemapHandle);
        mesh.render();
    }

    public void unload() {
        shader.unload();
        mesh.unload();
    }

}
