package csc_cccix.geocracy.space;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.graphics.Camera;
import csc_cccix.geocracy.graphics.Image;
import csc_cccix.geocracy.graphics.Mesh;
import csc_cccix.geocracy.graphics.MeshMaker;

public class SpaceRenderer {

    private static final String DIRECTORY = "space_cubemap/";
    private static final String[] FILES = {
        DIRECTORY + "right.png",
        DIRECTORY + "left.png",
        DIRECTORY + "top.png",
        DIRECTORY + "bottom.png",
        DIRECTORY + "front.png",
        DIRECTORY + "back.png",
    };

    private SpaceShader shader;
    private int vboHandle;
    private int vaoHandle;
    private int cubemapHandle;

    public SpaceRenderer() {
        shader = new SpaceShader();
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("", "Failed to load shader");
            return false;
        }

        if (!loadVertices()) {
            Log.e("", "Failed to load vertices");
            return false;
        }

        if (!loadCubemap()) {
            Log.e("", "Failed to load cubemap");
            return false;
        }

        return true;
    }

    public void render(Camera camera) {
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());

        GLES30.glCullFace(GLES30.GL_FRONT);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, cubemapHandle);
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 36, GLES30.GL_UNSIGNED_INT, 0);
        GLES30.glBindVertexArray(0);

        GLES30.glCullFace(GLES30.GL_BACK);
    }

    public void unload() {
        shader.unload();
        if (vaoHandle != 0) {
            int[] vaoHandleArr = { vaoHandle };
            GLES30.glDeleteVertexArrays(1, vaoHandleArr, 0);
        }
        if (vboHandle != 0) {
            int[] vboHandleArr = { vboHandle};
            GLES30.glDeleteBuffers(1, vboHandleArr, 0);
        }
        if (cubemapHandle != 0) {
            GLES30.glDeleteTextures(1, new int[]{cubemapHandle}, 0);
        }
    }

    public int getCubemapHandle() {
        return cubemapHandle;
    }

    private boolean loadVertices() {
        Mesh cubeMesh = MeshMaker.makeCube("Cube");
        ByteBuffer vertexBB = ByteBuffer.allocateDirect(8 * 3 * 4);
        vertexBB.order(ByteOrder.nativeOrder());
        vertexBB.asFloatBuffer().put(cubeMesh.getLocations());
        ByteBuffer indexBB = ByteBuffer.allocateDirect(36 * 4);
        indexBB.order(ByteOrder.nativeOrder());
        indexBB.asIntBuffer().put(cubeMesh.getIndices());

        // Create VBO
        int[] vboHandleArr = { 0 };
        GLES30.glGenBuffers(1, vboHandleArr, 0);
        vboHandle = vboHandleArr[0];
        if (vboHandle == 0) {
            Log.e("", "Failed to generate vbo");
            return false;
        }
        // Upload vbo data
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexBB.limit(), vertexBB, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "Failed to upload vbo");
            return false;
        }

        // Create IBO
        int[] iboHandleArr = { 0 };
        GLES30.glGenBuffers(1, iboHandleArr, 0);
        int iboHandle = iboHandleArr[0];
        if (iboHandle == 0) {
            Log.e("", "Failed to generate ibo");
            return false;
        }
        // Upload ibo data
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, iboHandle);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBB.limit(), indexBB, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "Failed to upload ibo");
            return false;
        }

        // Create VAO
        int[] vaoHandleArr = { 0 };
        GLES30.glGenVertexArrays(1, vaoHandleArr, 0);
        vaoHandle = vaoHandleArr[0];
        if (vaoHandle == 0) {
            Log.e("", "Failed to generate vao");
        }
        // Setup vao attributes and bindings
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, iboHandle);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES30.glDisableVertexAttribArray(0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "Failed to setup vao");
            return false;
        }

        return true;
    }

    private boolean loadCubemap() {
        int[] cubemapHandleArr = { 0 };
        GLES30.glGenTextures(1, cubemapHandleArr, 0);
        if ((cubemapHandle = cubemapHandleArr[0]) == 0) {
            Log.e("", "Failed to generate cubemap");
            return false;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, cubemapHandle);

        for (int fi = 0; fi < 6; ++fi) {
            Image image = new Image(FILES[fi]);
            if (!image.load()) {
                Log.e("",  "Failed to load face image " + fi);
                return false;
            }
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + fi, 0, GLES30.GL_RGBA, image.getWidth(), image.getHeight(), 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, image.getData());
        }

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_CUBE_MAP, GLES30.GL_TEXTURE_WRAP_R, GLES30.GL_CLAMP_TO_EDGE);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_CUBE_MAP, 0);

        return !Util.isGLError();
    }

}
