package csc_cccix.geocracy.world;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.graphics.Camera;
import glm_.mat3x3.Mat3;
import glm_.vec3.Vec3;

public class ArrowRenderer {

    private World world;
    private ArrowShader shader;
    private float[] positions;
    private int vboHandle;
    private int vaoHandle;
    private float angle;
    private Mat3 basis;
    private boolean isChange;

    public ArrowRenderer(World world, int nSegments) {
        this.world = world;
        shader = new ArrowShader();
        genMesh(nSegments);
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("", "Failed to load shader");
            return false;
        }

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
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glDisableVertexAttribArray(0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "Failed to setup vao");
            return false;
        }

        return true;
    }

    public void set(Vec3 start, Vec3 end) {
        Vec3 u = start;
        Vec3 v = end;
        Vec3 w = u.cross(v).normalizeAssign();
        angle = (float)Math.acos(u.dot(v));
        v = w.cross(u);
        basis = new Mat3(u, v, w);

        isChange = true;
    }

    // Expects a shader to be active
    public void render(long t, Camera camera) {
        GLES30.glDisable(GLES20.GL_DEPTH_TEST);

        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        if (isChange) {
            shader.setAngle(angle);
            shader.setBasis(basis);
            shader.setColor(world.getSelectedTerritory().getOwner().getColor());
        }
        shader.setTime((float)((double)t * 1.0e-9 % 2.0));
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, positions.length / 2);
        GLES30.glBindVertexArray(0);

        GLES30.glEnable(GLES20.GL_DEPTH_TEST);
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
        ByteBuffer bb = ByteBuffer.allocateDirect(positions.length * 4);
        bb.order(ByteOrder.nativeOrder());
        bb.asFloatBuffer().put(positions);
        return bb;
    }

}
