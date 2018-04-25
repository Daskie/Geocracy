package csc309.geocracy.world;

import android.opengl.GLES30;
import android.util.Log;
import android.util.LongSparseArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Random;

import csc309.geocracy.MeshMaker;
import csc309.geocracy.Util;
import csc309.geocracy.VecArrayUtil;
import csc309.geocracy.graphics.Camera;
import csc309.geocracy.graphics.Mesh;
import csc309.geocracy.noise.SimplexNoise;
import glm_.mat3x3.Mat3;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class Terrain {

    private class Face {
        int[] adjacencies = new int[3];
        byte coastDist;
        byte territory;
    }

    private TerrainShader shader;
    private float highElevation, lowElevation;
    private float[] locations;
    private boolean[] borders;
    private int[] indices;
    private int vboHandle;
    private int vaoHandle;
    private Face[] faces;
    private int maxCoastDist;
    private int[] territorySpawnFaces;

    public Terrain(int tessellationDegree, Random rand) {
        shader = new TerrainShader();
        highElevation = 1.1f;
        lowElevation = 0.95f;
        Mesh sphereMesh = MeshMaker.makeSphereIndexed("Terrain", tessellationDegree);
        locations = sphereMesh.getLocations();
        indices = sphereMesh.getIndices();
        faces = new Face[indices.length / 3];
        for (int fi = 0; fi < faces.length; ++fi) faces[fi] = new Face();

        genFaceAdjacencies();
        calcTerritorySpawns(40, rand);
        //mesh.terraform(seed, highElevation, lowElevation);
        calcCoastDistance();
        spreadTerritories();
        detBorders();
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("Terrain", "Failed to load shader");
            return false;
        }

        // Create VBO
        int[] vboHandleArr = { 0 };
        GLES30.glGenBuffers(1, vboHandleArr, 0);
        vboHandle = vboHandleArr[0];
        if (vboHandle == 0) {
            Log.e("Terrain", "Failed to generate vbo");
            return false;
        }
        // Upload vbo data
        ByteBuffer vertexData = genVertexBufferData();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexData.limit(), vertexData, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("Terrain", "Failed to upload vbo");
            return false;
        }

        // Create VAO
        int[] vaoHandleArr = { 0 };
        GLES30.glGenVertexArrays(1, vaoHandleArr, 0);
        vaoHandle = vaoHandleArr[0];
        if (vaoHandle == 0) {
            Log.e("Terrain", "Failed to generate vao");
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
            Log.e("Terrain", "Failed to setup vao");
            return false;
        }

        shader.setActive();
        shader.setLowElevation(lowElevation);
        shader.setHighElevation(highElevation);
        shader.setMaxCoastDist(maxCoastDist);
        Vec3[] continentColors = genContinentColors(40);
        Vec3[] allColors = new Vec3[continentColors.length + 1];
        System.arraycopy(continentColors, 0, allColors, 1, continentColors.length);
        allColors[0] = new Vec3(0.0f);
        shader.setContinentColors(allColors);

        return true;
    }

    public void render(Camera camera, Vec3 lightDir) {
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        shader.setLightDirection(lightDir);

        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        GLES30.glBindVertexArray(0);
    }

    public void unload() {
        shader.unload();

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

    private ByteBuffer genVertexBufferData() {
        int vertexSize = 3 * 4 + 3 * 4 + 4;
        ByteBuffer vertexData = ByteBuffer.allocateDirect(indices.length * vertexSize);
        vertexData.order(ByteOrder.nativeOrder());

        // Interlace vertex data
        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            int vi1 = indices[ii + 0];
            int vi2 = indices[ii + 1];
            int vi3 = indices[ii + 2];
            Vec3 v1 = VecArrayUtil.get(locations, vi1);
            Vec3 v2 = VecArrayUtil.get(locations, vi2);
            Vec3 v3 = VecArrayUtil.get(locations, vi3);
            Vec3 n = (v2.minus(v1)).crossAssign(v3.minus(v1)).normalizeAssign();

            Face face = faces[fi];
            boolean e12 = (borders[vi1] || borders[vi2]) && face.territory != faces[face.adjacencies[0]].territory;
            boolean e23 = (borders[vi2] || borders[vi3]) && face.territory != faces[face.adjacencies[1]].territory;
            boolean e31 = (borders[vi3] || borders[vi1]) && face.territory != faces[face.adjacencies[2]].territory;

            int info = Util.toInt(face.coastDist, face.territory, (byte)0, (byte)0);
            int info1 = info | (((borders[vi1] ? 1 : 0) | (e12 ? 2 : 0)                 | (e31 ? 8 : 0)) << 16);
            int info2 = info | (((borders[vi2] ? 1 : 0) | (e12 ? 2 : 0) | (e23 ? 4 : 0)                ) << 16);
            int info3 = info | (((borders[vi3] ? 1 : 0)                 | (e23 ? 4 : 0) | (e31 ? 8 : 0)) << 16);

            vertexData.putFloat(v1.x);
            vertexData.putFloat(v1.y);
            vertexData.putFloat(v1.z);
            vertexData.putFloat(n.x);
            vertexData.putFloat(n.y);
            vertexData.putFloat(n.z);
            vertexData.putInt(info1);
            vertexData.putFloat(v2.x);
            vertexData.putFloat(v2.y);
            vertexData.putFloat(v2.z);
            vertexData.putFloat(n.x);
            vertexData.putFloat(n.y);
            vertexData.putFloat(n.z);
            vertexData.putInt(info2);
            vertexData.putFloat(v3.x);
            vertexData.putFloat(v3.y);
            vertexData.putFloat(v3.z);
            vertexData.putFloat(n.x);
            vertexData.putFloat(n.y);
            vertexData.putFloat(n.z);
            vertexData.putInt(info3);
        }

        vertexData.flip();
        return vertexData;
    }

    private Vec3[] genContinentColors(int nContinents) {
        Vec3[] colors = new Vec3[nContinents];
        for(int i = 0; i < nContinents; ++i) {
            colors[i] = Util.hsv2rgb((float)i / nContinents, 1.0f, 1.0f);
        }
        return colors;
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

    private void terraform(long seed, float highElevation, float lowElevation) {
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

    private void calcCoastDistance() {

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

    private void calcTerritorySpawns(int nTerritories, Random rand) {
        territorySpawnFaces = new int[nTerritories];
        Vec3 w = Util.pointOnSphereRandom(rand);
        Vec3 v = Util.ortho(w);
        Vec3 u = v.cross(w);
        Mat3 basis = new Mat3(u, v, w);
        for (int ti = 0; ti < nTerritories; ++ti) {
            territorySpawnFaces[ti] = faceNearestTo(basis.times(Util.pointOnSphereFibonacci(ti, nTerritories)));
        }
    }

    private void spreadTerritories() {
        int nTerritories = territorySpawnFaces.length;
        HashSet<Integer> fringes = new HashSet<>();
        ArrayDeque<Integer> fringeQueue = new ArrayDeque<>();

        for (int ti = 0; ti < nTerritories; ++ti) {
            int fi = territorySpawnFaces[ti];
            if (!fringes.contains(fi)) {
                faces[fi].territory = (byte)(ti + 1);
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
                if (faces[fi].territory == 0 && !fringes.contains(fi)) {
                    faces[fi].territory = territory;
                    fringes.add(fi);
                    fringeQueue.addLast(fi);
                }
            }
            fringeQueue.removeFirst();
            fringes.remove(origFI);
        }
    }

    private void detBorders() {
        borders = new boolean[locations.length];
        byte[] territories = new byte[borders.length];
        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            Face face = faces[fi];
            for (int i = 0; i < 3; ++i) {
                int vi = indices[ii + i];
                if (territories[vi] == 0) {
                    territories[vi] = face.territory;
                }
                else if (territories[vi] != face.territory) {
                    borders[vi] = true;
                }
            }
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
