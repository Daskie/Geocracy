package csc309.geocracy;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import glm_.vec3.Vec3;

public class Mesh {

    protected static final int LOCATION_COMPONENTS = 3, LOCATION_SIZE = LOCATION_COMPONENTS * 4;
    protected static final int NORMAL_COMPONENTS = 3, NORMAL_SIZE = NORMAL_COMPONENTS * 4;
    protected static final int VERTEX_SIZE = LOCATION_SIZE + NORMAL_SIZE;

    protected String name;
    protected Vec3[] locations;
    protected Vec3[] normals;
    protected int[] indices;
    protected int vboHandle;
    protected int iboHandle;
    protected int vaoHandle;

    public Mesh(String name, Vec3[] locations, Vec3[] normals, int[] indices) {
        this.name = name;
        this.locations = locations;
        this.normals = normals;
        this.indices = indices;
    }

    public boolean load() {
        // If already on GPU, delete old resources
        unload();

        // Create VBO
        int[] vboArr = { 0 };
        GLES30.glGenBuffers(1, vboArr, 0);
        vboHandle = vboArr[0];
        if (vboHandle == 0) {
            Log.e("Mesh", "Failed to create vbo");
            return false;
        }
        // Upload vbo data
        ByteBuffer vertexData = getVertexBufferData();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexData.capacity(), vertexData, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("Mesh", "Failed to upload vbo data");
            return false;
        }

        // Create IBO
        if (indices != null) {
            int[] iboArr = {0};
            GLES30.glGenBuffers(1, iboArr, 0);
            iboHandle = iboArr[0];
            if (iboHandle == 0) {
                Log.e("Mesh", "Failed to create ibo");
                return false;
            }
            // Upload ibo data
            ByteBuffer indexData = getIndexBufferData();
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, iboHandle);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexData.capacity(), indexData, GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
            // Check for OpenGL errors
            if (Util.isGLError()) {
                Log.e("Mesh", "Failed to upload ibo data");
                return false;
            }
        }

        // Create VAO
        int[] vaoArr = { 0 };
        GLES30.glGenVertexArrays(1, vaoArr, 0);
        vaoHandle = vaoArr[0];
        if (vaoHandle == 0) {
            Log.e("Mesh", "Failed to create vao");
        }
        // Setup vao attributes and bindings
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        if (indices != null) GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, iboHandle);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, VERTEX_SIZE, 0); // locations
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, VERTEX_SIZE, LOCATION_SIZE); // normals
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        if (indices != null) GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("Mesh", "Failed to setup vao");
            return false;
        }

        return true;
    }

    public void unload() {
        if (vaoHandle != 0) {
            int[] vaoArr = { vaoHandle };
            GLES30.glDeleteVertexArrays(1, vaoArr, 0);
            vaoHandle = 0;
        }
        if (vboHandle != 0) {
            int[] vboArr = { vboHandle };
            GLES30.glDeleteBuffers(1, vboArr, 0);
            vboHandle = 0;
        }
        if (iboHandle != 0) {
            int[] iboArr = { iboHandle };
            GLES30.glDeleteBuffers(1, iboArr, 0);
            iboHandle = 0;
        }
    }

    public String getName() { return name; }

    public int getVAOHandle() { return vaoHandle; }
    public int getIBOHandle() { return iboHandle; }
    public int getVBOHandle() { return vboHandle; }

    public int getNumVertices() { return locations.length; }

    public int getNumIndices() { return indices == null ? 0 : indices.length; }

    private ByteBuffer getVertexBufferData() {
        int nVertices = locations.length;
        ByteBuffer vertexData = ByteBuffer.allocateDirect(nVertices * VERTEX_SIZE);
        vertexData.order(ByteOrder.nativeOrder());
        // Interlace vertex data
        for (int i = 0; i < nVertices; ++i) {
            vertexData.putFloat(locations[i].x);
            vertexData.putFloat(locations[i].y);
            vertexData.putFloat(locations[i].z);
            vertexData.putFloat(normals[i].x);
            vertexData.putFloat(normals[i].y);
            vertexData.putFloat(normals[i].z);
        }
        vertexData.flip();
        return vertexData;
    }

    private ByteBuffer getIndexBufferData() {
        if (indices == null) {
            return null;
        }

        int nIndices = indices.length;
        ByteBuffer indexData = ByteBuffer.allocateDirect(nIndices * 4);
        indexData.order(ByteOrder.nativeOrder());
        indexData.asIntBuffer().put(indices);
        return indexData;
    }

}
