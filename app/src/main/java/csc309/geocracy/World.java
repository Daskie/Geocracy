package csc309.geocracy;

import android.opengl.GLES30;
import android.util.Log;

import glm_.mat3x3.Mat3;
import glm_.mat4x4.Mat4;

public class World {

    private BasicShader shader;
    private Mesh mesh;

    public World() {
        shader = new BasicShader();
        mesh = MeshMaker.makeIcosahedron("World", true);
    }

    public boolean load() {
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
        // Render icosahedron
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

}
