package csc309.geocracy.world;

import android.opengl.GLES30;
import android.util.Log;
import android.util.LongSparseArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.HashSet;

import csc309.geocracy.MeshMaker;
import csc309.geocracy.Util;
import csc309.geocracy.VecArrayUtil;
import csc309.geocracy.graphics.Mesh;
import csc309.geocracy.noise.SimplexNoise;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class TerrainMesh {

    class Face {
        int[] adjacencies = new int[3];
        byte coastDist;
        byte territory;
    }

    private float[] locations;
    private int[] indices;
    private int vboHandle;
    private int vaoHandle;
    Face[] faces;
    int maxCoastDist;
    int[] territorySpawnFaces;

    public TerrainMesh(int tessellationDegree) {
        Mesh sphereMesh = MeshMaker.makeSphereIndexed("Terrain", tessellationDegree);
        locations = sphereMesh.getLocations();
        indices = sphereMesh.getIndices();
        faces = new Face[indices.length / 3];
        for (int fi = 0; fi < faces.length; ++fi) faces[fi] = new Face();
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
            Log.e("TerrainMesh", "Failed to generate vbo");
            return false;
        }
        // Upload vbo data
        ByteBuffer vertexData = genVertexBufferData();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexData.limit(), vertexData, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("TerrainMesh", "Failed to upload vbo");
            return false;
        }

        // Create VAO
        int[] vaoHandleArr = { 0 };
        GLES30.glGenVertexArrays(1, vaoHandleArr, 0);
        vaoHandle = vaoHandleArr[0];
        if (vaoHandle == 0) {
            Log.e("TerrainMesh", "Failed to generate vao");
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
        GLES30.glVertexAttribIPointer(2, 1, GLES30.GL_INT, vertexSize, 3 * 4 + 3 * 4); // info
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glDisableVertexAttribArray(2);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("TerrainMesh", "Failed to setup vao");
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
        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            Vec3 v1 = VecArrayUtil.get(locations, indices[ii + 0]);
            Vec3 v2 = VecArrayUtil.get(locations, indices[ii + 1]);
            Vec3 v3 = VecArrayUtil.get(locations, indices[ii + 2]);
            Vec3 n = (v2.minus(v1)).crossAssign(v3.minus(v1)).normalizeAssign();
            Face face = faces[fi];
            int info = Util.toInt(face.coastDist, face.territory, (byte)0, (byte)0);

            vertexData.putFloat(v1.x);
            vertexData.putFloat(v1.y);
            vertexData.putFloat(v1.z);
            vertexData.putFloat(n.x);
            vertexData.putFloat(n.y);
            vertexData.putFloat(n.z);
            vertexData.putInt(info);
            vertexData.putFloat(v2.x);
            vertexData.putFloat(v2.y);
            vertexData.putFloat(v2.z);
            vertexData.putFloat(n.x);
            vertexData.putFloat(n.y);
            vertexData.putFloat(n.z);
            vertexData.putInt(info);
            vertexData.putFloat(v3.x);
            vertexData.putFloat(v3.y);
            vertexData.putFloat(v3.z);
            vertexData.putFloat(n.x);
            vertexData.putFloat(n.y);
            vertexData.putFloat(n.z);
            vertexData.putInt(info);
        }

        vertexData.flip();
        return vertexData;
    }

    private void genFaceAdjacencies() {

        class Edge {
            int faceI, faceEdgeI;
            Edge(int faceI, int faceEdgeI) { this.faceI = faceI; this.faceEdgeI = faceEdgeI; }
        }

        LongSparseArray<Edge> edges = new LongSparseArray<>();
        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            int vi1 = indices[ii + 0];
            int vi2 = indices[ii + 1];
            int vi3 = indices[ii + 2];
            Face face = faces[fi];

            // Edge v1 -> v2
            long key = vi1 < vi2 ? Util.toLong(vi1, vi2) : Util.toLong(vi2, vi1);
            Edge edge = edges.get(key);
            if (edge == null) {
                edges.put(key, new Edge(fi, 0));
            }
            else {
                face.adjacencies[0] = edge.faceI;
                faces[edge.faceI].adjacencies[edge.faceEdgeI] = fi;
            }

            // Edge v2 -> v3
            key = vi2 < vi3 ? Util.toLong(vi2, vi3) : Util.toLong(vi3, vi2);
            edge = edges.get(key);
            if (edge == null) {
                edges.put(key, new Edge(fi, 1));
            }
            else {
                face.adjacencies[1] = edge.faceI;
                faces[edge.faceI].adjacencies[edge.faceEdgeI] = fi;
            }

            // Edge v3 -> v1
            key = vi3 < vi1 ? Util.toLong(vi3, vi1) : Util.toLong(vi1, vi3);
            edge = edges.get(key);
            if (edge == null) {
                edges.put(key, new Edge(fi, 2));
            }
            else {
                face.adjacencies[2] = edge.faceI;
                faces[edge.faceI].adjacencies[edge.faceEdgeI] = fi;
            }
        }
    }

    void terraform(long seed, float highElevation, float lowElevation) {
        final int N_OCTAVES = 5;
        final float INIT_FREQUENCY = 1.0f;
        final float PERSISTENCE = 0.5f;

        SimplexNoise simplex = new SimplexNoise(seed);
        float maxAmplitude = ((float)(1 << N_OCTAVES) - 1) / (float)(1 << (N_OCTAVES - 1));
        float adjustFactor = 1.0f / ((maxAmplitude + 1.0f) * 0.5f);
        float superRange = highElevation - 1.0f;
        float subRange = 1.0f - lowElevation;

        int nVertices = locations.length / 3;
        for (int i = 0; i < nVertices; ++i) {
            int ci = i * 3;

            float v = 0.0f;
            float frequency = INIT_FREQUENCY;
            float amplitude = 1.0f;
            for (int octave = 0; octave < N_OCTAVES; ++octave) {
                v += simplex.noise(
                    locations[ci + 0] * frequency,
                    locations[ci + 1] * frequency,
                    locations[ci + 2] * frequency
                ) * amplitude;
                frequency *= 2.0f;
                amplitude *= PERSISTENCE;
            }
            v *= adjustFactor; // in range [-1.0, 1.0]

            // Super transformation (make mountainous)
            if (v >= 0.0f) {
                v *= v * v; // [-1.0, 1.0]
            }
            // Sub transformation (make gradual drop-off)
            else {
                if (v >= -0.5f) {
                    v = -2.0f * v * v;
                }
                else {
                    v = 2.0f * (v + 1.0f) * (v + 1.0f) - 1.0f;
                }
            }

            if (v >= 0.0f) {
                v = 1.0f + v * superRange; // [lowElevation, highElevation]
            }
            else {
                v = 1.0f + v * subRange; // [lowElevation, highElevation]
            }
            locations[ci + 0] *= v;
            locations[ci + 1] *= v;
            locations[ci + 2] *= v;
        }
    }

    void calcCoastDistance() {

        HashSet<Integer> currFaces = new HashSet<>();
        HashSet<Integer> nextFaces = new HashSet<>();
        byte[] signs = new byte[faces.length];

        // Start with coast faces
        for (int fi = 0; fi < faces.length; ++fi) {
            faces[fi].coastDist = -1;
            int ii = fi * 3;
            float elev1 = VecArrayUtil.length2(locations, indices[ii + 0]);
            float elev2 = VecArrayUtil.length2(locations, indices[ii + 1]);
            float elev3 = VecArrayUtil.length2(locations, indices[ii + 2]);
            if (elev1 > 1.0f || elev2 > 1.0f || elev3 > 1.0f) signs[fi] += 1;
            if (elev1 < 1.0f || elev2 < 1.0f || elev3 < 1.0f) signs[fi] -= 1;
            if (signs[fi] == 0) {
                currFaces.add(fi);
            }
        }

        int currDist = 0;
        while (true) {
            for (Integer fi : currFaces) {
                faces[fi].coastDist = (byte)currDist;

                int[] adjacencies = faces[fi].adjacencies;
                if (faces[adjacencies[0]].coastDist == -1) nextFaces.add(adjacencies[0]);
                if (faces[adjacencies[1]].coastDist == -1) nextFaces.add(adjacencies[1]);
                if (faces[adjacencies[2]].coastDist == -1) nextFaces.add(adjacencies[2]);
            }

            if (nextFaces.isEmpty()) {
                break;
            }

            HashSet<Integer> temp = currFaces;
            currFaces = nextFaces;
            nextFaces = temp;
            nextFaces.clear();
            ++currDist;
        }

        for (int fi = 0; fi < faces.length; ++fi) {
            faces[fi].coastDist *= signs[fi];
        }

        maxCoastDist = currDist;
    }

    void calcTerritorySpawns(int nTerritories) {
        territorySpawnFaces = new int[nTerritories];
        for (int ti = 0; ti < nTerritories; ++ti) {
            territorySpawnFaces[ti] = faceNearestTo(Util.pointOnSphereFibonacci(ti, nTerritories));
        }
    }

    void spreadTerritories() {
        for (Face face : faces) face.territory = (byte)-1;
        int nTerritories = territorySpawnFaces.length;
        HashSet<Integer> fringes = new HashSet<>();
        ArrayDeque<Integer> fringeQueue = new ArrayDeque<>();

        for (int ti = 0; ti < nTerritories; ++ti) {
            int fi = territorySpawnFaces[ti];
            if (!fringes.contains(fi)) {
                faces[fi].territory = (byte)ti;
                fringes.add(fi);
                fringeQueue.addLast(fi);
            }
        }

        while (!fringeQueue.isEmpty()) {
            int origFI = fringeQueue.getFirst();
            byte territory = faces[origFI].territory;
            int[] adjacencies = faces[origFI].adjacencies;
            for (int i = 0; i < 3; ++i) {
                int fi = adjacencies[i];
                if (!fringes.contains(fi) && faces[fi].territory == -1) {
                    faces[fi].territory = territory;
                    fringes.add(fi);
                    fringeQueue.addLast(fi);
                }
            }
            fringeQueue.removeFirst();
            fringes.remove(origFI);
        }
    }

    private Vec3 centerOfFace(int fi) {
        int ii = fi * 3;
        Vec3 v = VecArrayUtil.get(locations, indices[ii + 0]);
        v.plusAssign(VecArrayUtil.get(locations, indices[ii + 1]));
        v.plusAssign(VecArrayUtil.get(locations, indices[ii + 2]));
        v.timesAssign(1.0f / 3.0f);
        return v;
    }

    private int faceNearestTo(Vec3 p) {
        int currFI = 0, prevFI = currFI, nextFI = currFI;
        float minDist2 = glm.length2(p.minus(centerOfFace(currFI)));

        while (true) {
            prevFI = currFI;
            currFI = nextFI;
            for (int i = 0; i < 3; ++i) {
                int fi = faces[currFI].adjacencies[i];
                if (fi != prevFI) {
                    float dist2 = glm.length2(p.minus(centerOfFace(fi)));
                    if (dist2 < minDist2) {
                        nextFI = fi;
                        minDist2 = dist2;
                    }
                }
            }

            if (nextFI == currFI) {
                break;
            }
        }

        return currFI;
    }

}
