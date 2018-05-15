package csc309.geocracy.world;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import csc309.geocracy.Util;
import csc309.geocracy.graphics.Camera;
import glm_.mat3x3.Mat3;
import glm_.vec3.Vec3;

public class Waterways {

    private World world;
    private WaterwayShader shader;
    private int nWaterways;
    private float[] positions;
    private int[] infos;
    private float[] angles;
    private Mat3[] bases;
    private int vboHandle;
    private int vaoHandle;

    public Waterways(World world, int nSegments, Vec3[] startPoints, Vec3[] endPoints, int[] originTerrs, int[] originConts) {
        this.world = world;
        shader = new WaterwayShader();
        genMesh(nSegments);
        nWaterways = startPoints.length;
        calcAnglesAndBases(startPoints, endPoints);
        createInfos(originTerrs, originConts);
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("Waterways", "Failed to load shader");
            return false;
        }
        shader.setActive();
        Vec3[] contColors = new Vec3[world.getContinents().length + 1];
        contColors[0] = new Vec3();
        for (int i = 0; i < world.getContinents().length; ++i) {
            contColors[i + 1] = world.getContinents()[i].getColor();
        }
        shader.setContinentColors(contColors);
        shader.setSelectedTerritory(0);

        // Create VBO
        int[] vboHandleArr = { 0 };
        GLES30.glGenBuffers(1, vboHandleArr, 0);
        vboHandle = vboHandleArr[0];
        if (vboHandle == 0) {
            Log.e("Waterways", "Failed to generate vbo");
            return false;
        }
        // Upload vbo data
        ByteBuffer vertexData = genVertexBufferData();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexData.limit(), vertexData, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("Waterways", "Failed to upload vbo");
            return false;
        }

        // Create VAO
        int[] vaoHandleArr = { 0 };
        GLES30.glGenVertexArrays(1, vaoHandleArr, 0);
        vaoHandle = vaoHandleArr[0];
        if (vaoHandle == 0) {
            Log.e("Waterways", "Failed to generate vao");
        }
        // Setup vao attributes and bindings
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glEnableVertexAttribArray(2);
        GLES30.glEnableVertexAttribArray(3);
        GLES30.glEnableVertexAttribArray(4);
        GLES30.glEnableVertexAttribArray(5);
        int positionSize = 8;
        int positionsOffset = 0;
        int infoOffset = positionsOffset + positions.length * 4;
        int anglesOffset = infoOffset + 4;
        int basesOffset = anglesOffset + 4;
        int instanceSize = 4 + 4 + 9 * 4;
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, positionSize, positionsOffset);
        GLES30.glVertexAttribIPointer(1, 1, GLES30.GL_INT, instanceSize, infoOffset);
        GLES30.glVertexAttribPointer(2, 1, GLES30.GL_FLOAT, false, instanceSize, anglesOffset);
        GLES30.glVertexAttribPointer(3, 4, GLES30.GL_FLOAT, false, instanceSize, basesOffset + 0 * 3 * 4);
        GLES30.glVertexAttribPointer(4, 4, GLES30.GL_FLOAT, false, instanceSize, basesOffset + 1 * 3 * 4);
        GLES30.glVertexAttribPointer(5, 4, GLES30.GL_FLOAT, false, instanceSize, basesOffset + 2 * 3 * 4);
        GLES30.glVertexAttribDivisor(1, 1);
        GLES30.glVertexAttribDivisor(2, 1);
        GLES30.glVertexAttribDivisor(3, 1);
        GLES30.glVertexAttribDivisor(4, 1);
        GLES30.glVertexAttribDivisor(5, 1);
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glVertexAttribDivisor(1, 0);
        GLES30.glVertexAttribDivisor(2, 0);
        GLES30.glVertexAttribDivisor(3, 0);
        GLES30.glVertexAttribDivisor(4, 0);
        GLES30.glVertexAttribDivisor(5, 0);
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glDisableVertexAttribArray(2);
        GLES30.glDisableVertexAttribArray(3);
        GLES30.glDisableVertexAttribArray(4);
        GLES30.glDisableVertexAttribArray(5);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("Waterways", "Failed to setup vao");
            return false;
        }

        return true;
    }

    // Expects a shader to be active
    public void render(long t, Camera camera, Vec3 lightDir, boolean selectionChange) {
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        shader.setTime((float)((double)t * 1.0e-9 % 2.0));
        shader.setLightDirection(lightDir);
        if (selectionChange) {
            if (world.getSelectedTerritory() != null) {
                shader.setSelectedTerritory(world.getSelectedTerritory().getId());
            }
            else {
                shader.setSelectedTerritory(0);
            }
        }
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

    private void createInfos(int[] originTerrs, int[] originConts) {
        infos = new int[nWaterways];
        for (int i = 0; i < nWaterways; ++i) {
            infos[i] = Util.toInt((short)originTerrs[i], (short)originConts[i]);
        }
    }

    private ByteBuffer genVertexBufferData() {
        ByteBuffer bb = ByteBuffer.allocateDirect(positions.length * 4 + nWaterways * (4 + 4 + 9 * 4));
        bb.order(ByteOrder.nativeOrder());
        for (float pos : positions) bb.putFloat(pos);
        for (int i = 0; i < nWaterways; ++i) {
            bb.putInt(infos[i]);
            bb.putFloat(angles[i]);
            bb.putFloat(bases[i].v00());
            bb.putFloat(bases[i].v01());
            bb.putFloat(bases[i].v02());
            bb.putFloat(bases[i].v10());
            bb.putFloat(bases[i].v11());
            bb.putFloat(bases[i].v12());
            bb.putFloat(bases[i].v20());
            bb.putFloat(bases[i].v21());
            bb.putFloat(bases[i].v22());
        }

        bb.flip();
        return bb;
    }

    private void calcAnglesAndBases(Vec3[] starts, Vec3[] ends) {
        angles = new float[nWaterways];
        bases = new Mat3[nWaterways];

        for (int i = 0; i < nWaterways; ++i) {
            Vec3 u = starts[i];
            Vec3 v = ends[i];
            Vec3 w = u.cross(v).normalizeAssign();
            angles[i] = (float)Math.acos(u.dot(v));
            v = w.cross(u);
            bases[i] = new Mat3(u, v, w);
        }
    }

}
