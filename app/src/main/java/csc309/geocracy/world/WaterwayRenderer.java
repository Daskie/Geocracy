package csc309.geocracy.world;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import csc309.geocracy.Util;
import csc309.geocracy.graphics.Camera;
import glm_.vec3.Vec3;

public class WaterwayRenderer {

    private WaterwayShader shader;
    private int nWaterways;
    private float[] positions;
    private float[] angles;
    private float[] bases;
    private int vboHandle;
    private int vaoHandle;

    public WaterwayRenderer(int nSegments, Vec3[] startPoints, Vec3[] endPoints) {
        shader = new WaterwayShader();
        genMesh(nSegments);
        nWaterways = startPoints.length;
        calcAnglesAndBases(startPoints, endPoints);
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("WaterwayRenderer", "Failed to load shader");
            return false;
        }

        // Create VBO
        int[] vboHandleArr = { 0 };
        GLES30.glGenBuffers(1, vboHandleArr, 0);
        vboHandle = vboHandleArr[0];
        if (vboHandle == 0) {
            Log.e("WaterwayRenderer", "Failed to generate vbo");
            return false;
        }
        // Upload vbo data
        ByteBuffer vertexData = genVertexBufferData();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexData.limit(), vertexData, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("WaterwayRenderer", "Failed to upload vbo");
            return false;
        }

        // Create VAO
        int[] vaoHandleArr = { 0 };
        GLES30.glGenVertexArrays(1, vaoHandleArr, 0);
        vaoHandle = vaoHandleArr[0];
        if (vaoHandle == 0) {
            Log.e("WaterwayRenderer", "Failed to generate vao");
        }
        // Setup vao attributes and bindings
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glEnableVertexAttribArray(2);
        GLES30.glEnableVertexAttribArray(3);
        GLES30.glEnableVertexAttribArray(4);
        int positionSize = 8;
        int positionsOffset = 0;
        int angleSize = 4;
        int anglesOffset = positionsOffset + positionSize * (positions.length / 2);
        int basisSize = 9 * 4;
        int basesOffset = anglesOffset + angleSize * angles.length;
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, positionSize, positionsOffset);
        GLES30.glVertexAttribPointer(1, 1, GLES30.GL_FLOAT, false, angleSize, anglesOffset);
        GLES30.glVertexAttribPointer(2, 4, GLES30.GL_FLOAT, false, basisSize, basesOffset + 0 * 3 * 4);
        GLES30.glVertexAttribPointer(3, 4, GLES30.GL_FLOAT, false, basisSize, basesOffset + 1 * 3 * 4);
        GLES30.glVertexAttribPointer(4, 4, GLES30.GL_FLOAT, false, basisSize, basesOffset + 2 * 3 * 4);
        GLES30.glVertexAttribDivisor(1, 1);
        GLES30.glVertexAttribDivisor(2, 1);
        GLES30.glVertexAttribDivisor(3, 1);
        GLES30.glVertexAttribDivisor(4, 1);
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glVertexAttribDivisor(1, 0);
        GLES30.glVertexAttribDivisor(2, 0);
        GLES30.glVertexAttribDivisor(3, 0);
        GLES30.glVertexAttribDivisor(4, 0);
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glDisableVertexAttribArray(2);
        GLES30.glDisableVertexAttribArray(3);
        GLES30.glDisableVertexAttribArray(4);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("WaterwayRenderer", "Failed to setup vao");
            return false;
        }

        return true;
    }

    // Expects a shader to be active
    public void render(Camera camera, Vec3 lightDir) {
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        shader.setLightDirection(lightDir);
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLE_STRIP, 0, positions.length / 2, nWaterways);
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

    private void genMesh(int nSegments) {
        int nVerts = 2 * (nSegments + 1);
        positions = new float[nVerts * 2];
        for (int i = 0; i < nSegments + 1; ++i) {
            float x = (float)i / (float)nSegments;
            int vi = 2 * i;
            int ci = 2 * vi;
            positions[ci + 0] = x;
            positions[ci + 1] = 1.0f;
            ++vi;
            ci = 2 * vi;
            positions[ci + 0] = x;
            positions[ci + 1] = -1.0f;
        }
    }

    private ByteBuffer genVertexBufferData() {
        ByteBuffer bb = ByteBuffer.allocateDirect(positions.length * 4 + angles.length * 4 + bases.length * 9 * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(positions);
        fb.put(angles);
        fb.put(bases);

        return bb;
    }

    private void calcAnglesAndBases(Vec3[] starts, Vec3[] ends) {
        angles = new float[nWaterways];
        bases = new float[nWaterways * 9];

        for (int i = 0; i < nWaterways; ++i) {
            Vec3 u = starts[i].normalize();
            Vec3 v = ends[i].normalize();
            Vec3 w = u.cross(v).normalizeAssign();
            angles[i] = (float)Math.acos(u.dot(v));
            v = w.cross(u);

            bases[i * 9 + 0] = u.x;
            bases[i * 9 + 1] = u.y;
            bases[i * 9 + 2] = u.z;
            bases[i * 9 + 3] = v.x;
            bases[i * 9 + 4] = v.y;
            bases[i * 9 + 5] = v.z;
            bases[i * 9 + 6] = w.x;
            bases[i * 9 + 7] = w.y;
            bases[i * 9 + 8] = w.z;
        }
    }

}
