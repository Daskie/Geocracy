package csc_cccix.geocracy.graphics;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.VecArrayUtil;
import glm_.vec3.Vec3;

public class Mesh {

    private static final int LOCATION_COMPONENTS = 3;
    private static final int LOCATION_SIZE = LOCATION_COMPONENTS * 4;
    private static final int NORMAL_COMPONENTS = 3;
    private static final int NORMAL_SIZE = NORMAL_COMPONENTS * 4;
    private static final int VERTEX_SIZE = LOCATION_SIZE + NORMAL_SIZE;

    private String name;
    private int nVertices;
    private float[] locations;
    private float[] normals;
    private int[] indices;
    private int vboHandle;
    private int iboHandle;
    private int vaoHandle;

    public Mesh(String name, float[] locations, float[] normals, int[] indices) {
        this.name = name;
        nVertices = locations.length / 3;
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
            Log.e("", "Failed to generate vbo");
            return false;
        }
        // Upload vbo data
        ByteBuffer vertexData = genVertexBufferData();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexData.limit(), vertexData, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "Failed to upload vbo");
            return false;
        }

        // Create IBO
        if (indices != null) {
            int[] iboHandleArr = {0};
            GLES30.glGenBuffers(1, iboHandleArr, 0);
            iboHandle = iboHandleArr[0];
            if (iboHandle == 0) {
                Log.e("", "Failed to generate ibo");
                return false;
            }
            // Upload ibo data
            ByteBuffer indexData = genIndexBufferData();
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, iboHandle);
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexData.limit(), indexData, GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
            // Check for OpenGL errors
            if (Util.isGLError()) {
                Log.e("", "Failed to upload ibo");
                return false;
            }
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
            Log.e("", "Failed to setup vao");
            return false;
        }

        return true;
    }

    // Expects a shader to be active
    public void render() {
        GLES30.glBindVertexArray(vaoHandle);
        if (indices == null) {
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, nVertices);
        }
        else {
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, indices.length, GLES30.GL_UNSIGNED_INT, 0);
        }
        GLES30.glBindVertexArray(0);
    }

    public void unload() {
        if (vaoHandle != 0) {
            GLES30.glDeleteVertexArrays(1, new int[]{ vaoHandle }, 0);
            vaoHandle = 0;
        }
        if (vboHandle != 0) {
            GLES30.glDeleteBuffers(1, new int[]{ vboHandle }, 0);
            vboHandle = 0;
        }
        if (iboHandle != 0) {
            GLES30.glDeleteBuffers(1, new int[]{ iboHandle }, 0);
            iboHandle = 0;
        }
    }

    // Unindexes mesh such that each face has its own vertices
    public void unindex() {
        float[] newLocations = new float[indices.length * 3];
        for (int i = 0; i < indices.length; ++i) {
            int ci = i * 3;
            int ci0 = indices[i] * 3;
            newLocations[ci + 0] = locations[ci0 + 0];
            newLocations[ci + 1] = locations[ci0 + 1];
            newLocations[ci + 2] = locations[ci0 + 2];
        }
        nVertices = indices.length;
        locations = newLocations;
        normals = new float[nVertices * 3];
        indices = null;
        calcFaceNormals();
    }

    public void scale(float factor) {
        for (int ci = 0; ci < locations.length; ++ci) {
            locations[ci] *= factor;
        }
    }

    public void scale(Vec3 factor, boolean scaleNorms) {
        for (int vi = 0; vi < nVertices; ++vi) {
            VecArrayUtil.multiply(locations, vi, factor);
        }
        if (scaleNorms) {
            Vec3 invFactor = new Vec3(1.0f / factor.x, 1.0f / factor.y, 1.0f / factor.z);
            for (int vi = 0; vi < nVertices; ++vi) {
                VecArrayUtil.multiply(normals, vi, invFactor);
                VecArrayUtil.normalize(normals, vi);
            }
        }
    }

    public void translate(Vec3 delta) {
        for (int vi = 0; vi < nVertices; ++vi) {
            int ci = vi * 3;
            locations[ci + 0] += delta.x;
            locations[ci + 1] += delta.y;
            locations[ci + 2] += delta.z;
        }
    }

    // Sets each vertex normal to that of last face it belongs to
    public void calcFaceNormals() {
        if (indices != null) {
            for (int i = 0; i < indices.length; i += 3) {
                Vec3 v1 = VecArrayUtil.get(locations, indices[i + 0]);
                Vec3 v2 = VecArrayUtil.get(locations, indices[i + 1]);
                Vec3 v3 = VecArrayUtil.get(locations, indices[i + 2]);
                Vec3 n = (v2.minus(v1)).crossAssign(v3.minus(v1)).normalizeAssign();
                VecArrayUtil.set(normals, i + 0, n);
                VecArrayUtil.set(normals, i + 1, n);
                VecArrayUtil.set(normals, i + 2, n);
            }
        }
        else {
            for (int i = 0; i < nVertices; i += 3) {
                Vec3 v1 = VecArrayUtil.get(locations, i + 0);
                Vec3 v2 = VecArrayUtil.get(locations, i + 1);
                Vec3 v3 = VecArrayUtil.get(locations, i + 2);
                Vec3 n = (v2.minus(v1)).crossAssign(v3.minus(v1)).normalizeAssign();
                VecArrayUtil.set(normals, i + 0, n);
                VecArrayUtil.set(normals, i + 1, n);
                VecArrayUtil.set(normals, i + 2, n);
            }
        }
    }

    public String getName() { return name; }

    public float[] getLocations() { return locations; }

    public float[] getNormals() { return normals; }

    public int[] getIndices() { return indices; }

    public int getNumVertices() { return nVertices; }

    public int getNumIndices() { return indices == null ? 0 : indices.length; }

    public boolean isIndexed() { return indices != null; }

    public int getVBOHandle() { return vboHandle; }

    public int getIBOHandle() { return iboHandle; }

    public int getVAOHandle() { return vaoHandle; }

    private ByteBuffer genVertexBufferData() {
        ByteBuffer vertexData = ByteBuffer.allocateDirect(nVertices * VERTEX_SIZE);
        vertexData.order(ByteOrder.nativeOrder());
        // Interlace vertex data
        for (int i = 0; i < nVertices; ++i) {
            int ci = i * 3;
            vertexData.putFloat(locations[ci + 0]);
            vertexData.putFloat(locations[ci + 1]);
            vertexData.putFloat(locations[ci + 2]);
            vertexData.putFloat(normals[ci + 0]);
            vertexData.putFloat(normals[ci + 1]);
            vertexData.putFloat(normals[ci + 2]);
        }
        vertexData.flip();
        return vertexData;
    }

    private ByteBuffer genIndexBufferData() {
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
