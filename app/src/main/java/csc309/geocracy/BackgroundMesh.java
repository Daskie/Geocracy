package csc309.geocracy;

import android.opengl.GLES30;
import android.util.Log;
import android.util.SparseArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import csc309.geocracy.graphics.Mesh;
import glm_.vec3.Vec3;

public class BackgroundMesh {
    private float[] locations;
    private int[] indices;
    private int[] identities;
    private int vboHandle;
    private int vaoHandle;
    private SparseArray<int[]> faceAdjacencies;

    public BackgroundMesh(int tessellationDegree) {
        Mesh sphereMesh = MeshMaker.makeSphereIndexed("Background", tessellationDegree);
        locations = sphereMesh.getLocations();
        indices = sphereMesh.getIndices();
        int nFaces = indices.length / 3;
        identities = new int[nFaces];
        genFaceAdjacencies();
    }

    public boolean load() {
        // If already on GPU, delete old resources
        unload();

        // Create VBO
        int[] vboHandleArr = { 0 };
        GLES30.glGenBuffers(1, vboHandleArr, 0);
        vboHandle = vboHandleArr[0];
        if (vboHandle == 0) {
            Log.e("BackgroundMesh", "Failed to generate vbo");
            return false;
        }
        // Upload vbo data
        ByteBuffer vertexData = genVertexBufferData();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexData.limit(), vertexData, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("BackgroundMesh", "Failed to upload vbo");
            return false;
        }

        // Create VAO
        int[] vaoHandleArr = { 0 };
        GLES30.glGenVertexArrays(1, vaoHandleArr, 0);
        vaoHandle = vaoHandleArr[0];
        if (vaoHandle == 0) {
            Log.e("BackgroundMesh", "Failed to generate vao");
        }
        // Setup vao attributes and bindings
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glEnableVertexAttribArray(2);
        int vertexSize = 3 * 4 + 3 * 4 + 4;
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, vertexSize, 0); // locations
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, vertexSize, 3 * 4); // normals
        GLES30.glVertexAttribPointer(2, 1, GLES30.GL_INT, false, vertexSize, 3 * 4 + 3 * 4); // identities
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glDisableVertexAttribArray(2);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("BackgroundMesh", "Failed to setup vao");
            return false;
        }

        return true;
    }

    // Expects a shader to be active
    public void render() {
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        GLES30.glBindVertexArray(0);
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
    }

    public float[] getLocations() { return locations; }

    public int[] getIndices() { return indices; }

    private ByteBuffer genVertexBufferData() {
        int vertexSize = 3 * 4 + 3 * 4 + 4;
        ByteBuffer vertexData = ByteBuffer.allocateDirect(indices.length * vertexSize);
        vertexData.order(ByteOrder.nativeOrder());

        // Interlace vertex data
        int nFaces = indices.length / 3;
        for (int fi = 0; fi < nFaces; ++fi) {
            int ii = fi * 3;
            Vec3 v1 = Util.getVec3(locations, indices[ii + 0]);
            Vec3 v2 = Util.getVec3(locations, indices[ii + 1]);
            Vec3 v3 = Util.getVec3(locations, indices[ii + 2]);
            Vec3 n = (v2.minus(v1)).crossAssign(v3.minus(v1)).normalizeAssign();
            int identity = identities[fi];

            vertexData.putFloat(v1.x);
            vertexData.putFloat(v1.y);
            vertexData.putFloat(v1.z);
            vertexData.putFloat(n.x);
            vertexData.putFloat(n.y);
            vertexData.putFloat(n.z);
            vertexData.putInt(identity);
            vertexData.putFloat(v2.x);
            vertexData.putFloat(v2.y);
            vertexData.putFloat(v2.z);
            vertexData.putFloat(n.x);
            vertexData.putFloat(n.y);
            vertexData.putFloat(n.z);
            vertexData.putInt(identity);
            vertexData.putFloat(v3.x);
            vertexData.putFloat(v3.y);
            vertexData.putFloat(v3.z);
            vertexData.putFloat(n.x);
            vertexData.putFloat(n.y);
            vertexData.putFloat(n.z);
            vertexData.putInt(identity);
        }

        vertexData.flip();
        return vertexData;
    }

    private void genFaceAdjacencies() {

    }
}
