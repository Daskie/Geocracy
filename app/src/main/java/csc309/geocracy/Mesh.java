package csc309.geocracy;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import glm_.vec3.Vec3;

public class Mesh {

    private static final int LOCATION_COMPONENTS = 3, LOCATION_SIZE = LOCATION_COMPONENTS * 4;
    private static final int NORMAL_COMPONENTS = 3, NORMAL_SIZE = NORMAL_COMPONENTS * 4;
    private static final int VERTEX_SIZE = LOCATION_SIZE + NORMAL_SIZE;

    private String name;
    private Vec3[] locations;
    private Vec3[] normals;
    private int[] indices;
    private int vboHandle;
    private int iboHandle;
    private int vaoHandle;

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
        int[] vboHandleArr = { 0 };
        GLES30.glGenBuffers(1, vboHandleArr, 0);
        vboHandle = vboHandleArr[0];
        if (vboHandle == 0) {
            Log.e("Mesh", "Failed to generate vbo");
            return false;
        }
        // Upload vbo data
        ByteBuffer vertexData = getVertexBufferData();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexData.limit(), vertexData, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("Mesh", "Failed to upload vbo");
            return false;
        }

        // Create IBO
        if (indices != null) {
            int[] iboHandleArr = {0};
            GLES30.glGenBuffers(1, iboHandleArr, 0);
            iboHandle = iboHandleArr[0];
            if (iboHandle == 0) {
                Log.e("Mesh", "Failed to generate ibo");
                return false;
            }
            // Upload ibo data
            ByteBuffer indexData = getIndexBufferData();
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, iboHandle);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexData.limit(), indexData, GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
            // Check for OpenGL errors
            if (Util.isGLError()) {
                Log.e("Mesh", "Failed to upload ibo");
                return false;
            }
        }

        // Create VAO
        int[] vaoHandleArr = { 0 };
        GLES30.glGenVertexArrays(1, vaoHandleArr, 0);
        vaoHandle = vaoHandleArr[0];
        if (vaoHandle == 0) {
            Log.e("Mesh", "Failed to generate vao");
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

    // Unindexes mesh such that each face has its own vertices
    public void unindex() {
        Vec3[] newLocations = new Vec3[indices.length];
        for (int i = 0; i < indices.length; ++i) {
            newLocations[i] = new Vec3(locations[indices[i]]);
        }
        locations = newLocations;
        normals = new Vec3[locations.length];
        indices = null;
        calcFaceNormals();
    }

    // Sets each vertex normal to that of last face it belongs to
    public void calcFaceNormals() {
        if (indices != null) {
            for (int i = 0; i < indices.length; i += 3) {
                Vec3 v1 = locations[indices[i + 0]];
                Vec3 v2 = locations[indices[i + 1]];
                Vec3 v3 = locations[indices[i + 2]];
                Vec3 n = (v2.minus(v1)).cross(v3.minus(v1)).normalize();
                normals[i + 0] = new Vec3(n);
                normals[i + 1] = new Vec3(n);
                normals[i + 2] = new Vec3(n);
            }
        }
        else {
            for (int i = 0; i < locations.length; i += 3) {
                Vec3 v1 = locations[i + 0];
                Vec3 v2 = locations[i + 1];
                Vec3 v3 = locations[i + 2];
                Vec3 n = (v2.minus(v1)).cross(v3.minus(v1)).normalize();
                normals[i + 0] = new Vec3(n);
                normals[i + 1] = new Vec3(n);
                normals[i + 2] = new Vec3(n);
            }
        }
    }

    public String getName() { return name; }

    public Vec3[] getLocations() { return locations; }

    public Vec3[] getNormals() { return normals; }

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
