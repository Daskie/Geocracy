package csc309.geocracy.world;

import android.opengl.GLES30;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseIntArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import csc309.geocracy.MeshMaker;
import csc309.geocracy.Util;
import csc309.geocracy.VecArrayUtil;
import csc309.geocracy.graphics.Camera;
import csc309.geocracy.graphics.Mesh;
import csc309.geocracy.noise.SimplexNoise;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class Terrain {

    private class Face {
        int[] adjacencies = new int[3];
        int coastDist;
        int territory;
    }

    private class TerritorySpec {
        HashSet<Integer> landFaces = new HashSet<>();
        HashSet<Integer> oceanFaces = new HashSet<>();
        HashSet<Integer> coastFaces = new HashSet<>();
        HashSet<Integer> borderFaces = new HashSet<>();
        SparseIntArray adjacentLandTerrs = new SparseIntArray();
        SparseIntArray adjacentOceanTerrs = new SparseIntArray();
        //int landAndCoastCount() { return landFaces.size() + coastFaces.size(); }
        //int totalCount() { return landAndCoastCount() + oceanFaces.size(); }
    }

    private TerrainShader shader;
    private float highElevation, lowElevation;
    private float[] locations;
    private boolean[] borders;
    private int[] indices;
    private int vboHandle;
    private int vaoHandle;
    private Face[] faces;
    private int[] landFaces;
    private int[] oceanFaces;
    private int[] coastFaces;
    private int maxCoastDist;
    private int[] territorySpawnFaces;
    private TerritorySpec[] territorySpecs;

    public Terrain(int tessellationDegree, Random rand) {
        shader = new TerrainShader();
        highElevation = 1.05f;
        lowElevation = 0.975f;
        Mesh sphereMesh = MeshMaker.makeSphereIndexed("Terrain", tessellationDegree);
        locations = sphereMesh.getLocations();
        indices = sphereMesh.getIndices();
        faces = new Face[indices.length / 3];
        for (int fi = 0; fi < faces.length; ++fi) faces[fi] = new Face();

        genFaceAdjacencies();
        terraform(highElevation, lowElevation, rand);
        processFaces();
        createTerritories(40, rand);
        smoothTerritories();
        //detBorders();
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
        Vec3[] colors = new Vec3[41];
        System.arraycopy(genContinentColors(40), 0, colors, 1, 40);
        colors[0] = new Vec3();

        shader.setContinentColors(colors);

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

            /*
            Face face = faces[fi];
            Face af1 = faces[face.adjacencies[0]];
            Face af2 = faces[face.adjacencies[1]];
            Face af3 = faces[face.adjacencies[2]];
            boolean isLand = face.coastDist >= 0;
            boolean e12 = (face.territory != af1.territory || (af1.coastDist >= 0) != isLand);
            boolean e23 = (face.territory != af2.territory || (af2.coastDist >= 0) != isLand);
            boolean e31 = (face.territory != af3.territory || (af3.coastDist >= 0) != isLand);

            int info = Util.toInt((byte)face.coastDist, (byte)face.territory, (byte)0, (byte)0);
            int info1 = info | (((borders[vi1] ? 1 : 0) | (e12 ? 2 : 0)                 | (e31 ? 8 : 0)) << 16);
            int info2 = info | (((borders[vi2] ? 1 : 0) | (e12 ? 2 : 0) | (e23 ? 4 : 0)                ) << 16);
            int info3 = info | (((borders[vi3] ? 1 : 0)                 | (e23 ? 4 : 0) | (e31 ? 8 : 0)) << 16);
            */
            int info = Util.toInt((byte)faces[fi].coastDist, (byte)faces[fi].territory, (byte)0, (byte)0);
            int info1 = info, info2 = info, info3 = info;

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
            long key = Util.toLong(glm.min(vi1, vi2), glm.max(vi1, vi2));
            Edge edge = edges.get(key);
            if (edge == null) {
                edges.put(key, new Edge(fi, 0));
            }
            else {
                face.adjacencies[0] = edge.faceI;
                faces[edge.faceI].adjacencies[edge.faceEdgeI] = fi;
            }

            // Edge v2 -> v3
            key = Util.toLong(glm.min(vi2, vi3), glm.max(vi2, vi3));
            edge = edges.get(key);
            if (edge == null) {
                edges.put(key, new Edge(fi, 1));
            }
            else {
                face.adjacencies[1] = edge.faceI;
                faces[edge.faceI].adjacencies[edge.faceEdgeI] = fi;
            }

            // Edge v3 -> v1
            key = Util.toLong(glm.min(vi3, vi1), glm.max(vi3, vi1));
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

    private void terraform(float highElevation, float lowElevation, Random rand) {
        final int k_nOctaves = 5;
        final float k_initFrequency = 0.75f;
        final float k_persistence = 0.5f;
        final float k_dropoffHeight = 0.01f;

        SimplexNoise simplex = new SimplexNoise(rand);
        float maxAmplitude = ((float)(1 << k_nOctaves) - 1) / (float)(1 << (k_nOctaves - 1));
        //float adjustFactor = 1.0f / maxAmplitude;
        float superRange = highElevation - 1.0f;
        float subRange = 1.0f - lowElevation;
        float minV = 0.0f, maxV = 0.0f;

        int nVertices = locations.length / 3;
        float[] vs = new float[nVertices];
        for (int i = 0; i < nVertices; ++i) {
            int ci = i * 3;
            float v = 0.0f;
            float frequency = k_initFrequency;
            float amplitude = 1.0f;
            for (int octave = 0; octave < k_nOctaves; ++octave) {
                v += simplex.noise(
                    locations[ci + 0] * frequency,
                    locations[ci + 1] * frequency,
                    locations[ci + 2] * frequency
                ) * amplitude;
                frequency *= 2.0f;
                amplitude *= k_persistence;
            }
            if (v < minV) minV = v;
            else if (v > maxV) maxV = v;
            vs[i] = v;
        }

        float adjustFactor = 2.0f / (maxV - minV);
        final float sinkExp = 1.25f;
        final float sinkFactor = (float)Math.pow(2.0f, 1.0f - sinkExp);
        for (int i = 0; i < nVertices; ++i) {
            int ci = i * 3;
            float v = vs[i];
            v = (v - minV) * adjustFactor - 1.0f; // in range [-1.0, 1.0]

            v = sinkFactor * (float)Math.pow(v + 1.0f, sinkExp) - 1.0f;

            // Super transformation (make mountainous)
            if (v >= 0.0f) {
                v *= v * v;
                //v *= v; // [-1.0, 1.0]
            }
            // Sub transformation (make gradual drop-off)
            else {
                v += 1;
                v *= v;
                v -= 1;
                /*if (v >= -0.5f) {
                    v = -2.0f * v * v;
                }
                else {
                    v = 2.0f * (v + 1.0f) * (v + 1.0f) - 1.0f;
                }*/
            }
            //v *= (4.0f / (1.0f + 2500.0f * v * v)) + 1.0f;
            //if (v > 0.0f && v < 0.01f) v = 0.01f;
            //else if (v < 0.0f && v > -0.01f) v = -0.01f;

            if (v > 0.0f) {
                v = (1.0f - k_dropoffHeight) * v + k_dropoffHeight;
                //if (v < k_dropoffHeight) v = k_dropoffHeight; // small offset from sea level to help with z-fighting
                v = 1.0f + v * superRange; // [lowElevation, highElevation]
            }
            else if (v < 0.0f) {
                v = (1.0f - k_dropoffHeight) * v - k_dropoffHeight;
                //if (v > -k_dropoffHeight) v = -k_dropoffHeight; // small offset from sea level to help with z-fighting
                v = 1.0f + v * subRange; // [lowElevation, highElevation]
            }

            locations[ci + 0] *= v;
            locations[ci + 1] *= v;
            locations[ci + 2] *= v;
        }
    }

    private void processFaces() {
        ArrayList<Integer> tempLandFaces = new ArrayList<>();
        ArrayList<Integer> tempOceanFaces = new ArrayList<>();
        ArrayList<Integer> tempCoastFaces = new ArrayList<>();
        byte[] faceSigns = new byte[faces.length];

        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            float elev1 = VecArrayUtil.length2(locations, indices[ii + 0]);
            float elev2 = VecArrayUtil.length2(locations, indices[ii + 1]);
            float elev3 = VecArrayUtil.length2(locations, indices[ii + 2]);
            if (elev1 > 1.0f || elev2 > 1.0f || elev3 > 1.0f) ++faceSigns[fi];
            if (elev1 < 1.0f || elev2 < 1.0f || elev3 < 1.0f) --faceSigns[fi];
            if (faceSigns[fi] > 0) tempLandFaces.add(fi);
            else if (faceSigns[fi] < 0) tempOceanFaces.add(fi);
            else tempCoastFaces.add(fi);
        }

        landFaces = new int[tempLandFaces.size()];
        oceanFaces = new int[tempOceanFaces.size()];
        coastFaces = new int[tempCoastFaces.size()];
        for (int i = 0; i < landFaces.length; ++i) landFaces[i] = tempLandFaces.get(i);
        for (int i = 0; i < oceanFaces.length; ++i) oceanFaces[i] = tempOceanFaces.get(i);
        for (int i = 0; i < coastFaces.length; ++i) coastFaces[i] = tempCoastFaces.get(i);

        calcCoastDistances(faceSigns);
    }

    private void calcCoastDistances(byte[] faceSigns) {
        HashSet<Integer> currFaces = new HashSet<>();
        HashSet<Integer> nextFaces = new HashSet<>();
        boolean[] checked = new boolean[faces.length];

        // Start with coast faces
        for (int i = 0; i < coastFaces.length; ++i) currFaces.add(coastFaces[i]);

        int currDist = 0;
        while (true) {
            for (Integer fi : currFaces) {
                faces[fi].coastDist = currDist * faceSigns[fi];
                checked[fi] = true;

                int[] adjacencies = faces[fi].adjacencies;
                if (!checked[adjacencies[0]]) nextFaces.add(adjacencies[0]);
                if (!checked[adjacencies[1]]) nextFaces.add(adjacencies[1]);
                if (!checked[adjacencies[2]]) nextFaces.add(adjacencies[2]);
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
        maxCoastDist = currDist;
    }

    /*private void calcTerritorySpawns(int nTerritories, Random rand) {
        territorySpawnFaces = new int[nTerritories];
        Vec3 w = Util.pointOnSphereRandom(rand);
        Vec3 v = Util.ortho(w);
        Vec3 u = v.cross(w);
        Mat3 basis = new Mat3(u, v, w);
        for (int ti = 0; ti < nTerritories; ++ti) {
            territorySpawnFaces[ti] = faceNearestTo(basis.times(Util.pointOnSphereFibonacci(ti, nTerritories)));
        }
    }*/

    private void createTerritories(int targetNTerritories, Random rand) {
        ArrayList<Integer> spawnFaces = new ArrayList<>();

        // Spawn on random land faces
        for (int ti = 0; ti < targetNTerritories; ++ti) spawnFaces.add(landFaces[rand.nextInt(landFaces.length)]);

        ArrayList<TerritorySpec> tempTerritorySpecs = new ArrayList<>();
        tempTerritorySpecs.add(null);
        HashSet<Integer> landLeft = new HashSet<>();
        HashSet<Integer> superFringes = new HashSet<>();
        ArrayDeque<Integer> superFringeQueue = new ArrayDeque<>();
        HashSet<Integer> subFringes = new HashSet<>();
        ArrayDeque<Integer> subFringeQueue = new ArrayDeque<>();

        for (int fi : landFaces) landLeft.add(fi);

        // Spread across land
        while (!spawnFaces.isEmpty()) {
            for (int fi : spawnFaces) {
                if (!landLeft.contains(fi)) {
                    continue;
                }
                faces[fi].territory = tempTerritorySpecs.size();
                landLeft.remove(fi);
                TerritorySpec terr = new TerritorySpec();
                terr.landFaces.add(fi);
                tempTerritorySpecs.add(terr);
                superFringes.add(fi);
                superFringeQueue.addLast(fi);
            }
            spawnFaces.clear();

            while (!superFringeQueue.isEmpty()) {
                int origFI = superFringeQueue.getFirst();
                int territory = faces[origFI].territory;
                TerritorySpec terr = tempTerritorySpecs.get(territory);
                int[] adjacencies = faces[origFI].adjacencies;
                for (int i = 0; i < 3; ++i) {
                    int fi = adjacencies[i];
                    Face face = faces[fi];
                    if (face.territory == 0) {
                        if (!superFringes.contains(fi)) {
                            face.territory = territory;
                            landLeft.remove(fi);
                            if (face.coastDist >= 0) {
                                if (face.coastDist > 0) terr.landFaces.add(fi);
                                else terr.coastFaces.add(fi);
                                superFringes.add(fi);
                                superFringeQueue.addLast(fi);
                            }
                            else {
                                terr.oceanFaces.add(fi);
                                subFringes.add(fi);
                                subFringeQueue.addLast(fi);
                            }
                        }
                    }
                    else if (face.territory != territory) {
                        terr.adjacentLandTerrs.put(face.territory, face.territory);
                        tempTerritorySpecs.get(face.territory).adjacentLandTerrs.put(territory, territory);
                    }
                }
                superFringeQueue.removeFirst();
                superFringes.remove(origFI);
            }

            if (!landLeft.isEmpty()) {
                spawnFaces.add(landLeft.iterator().next());
            }
        }

        // Spread through ocean
        while (!subFringeQueue.isEmpty()) {
            int origFI = subFringeQueue.getFirst();
            int territory = faces[origFI].territory;
            TerritorySpec terr = tempTerritorySpecs.get(territory);
            int[] adjacencies = faces[origFI].adjacencies;
            for (int i = 0; i < 3; ++i) {
                int fi = adjacencies[i];
                Face face = faces[fi];
                if (face.territory == 0) {
                    if (face.coastDist < 0 && !subFringes.contains(fi)) {
                        face.territory = territory;
                        terr.oceanFaces.add(fi);
                        subFringes.add(fi);
                        subFringeQueue.addLast(fi);
                    }
                }
                else if (face.territory != territory) {
                    terr.adjacentOceanTerrs.put(face.territory, face.territory);
                    tempTerritorySpecs.get(face.territory).adjacentOceanTerrs.put(territory, territory);
                }
            }
            subFringeQueue.removeFirst();
            subFringes.remove(origFI);
        }

        TerritorySpec t = tempTerritorySpecs.get(31);

        consolidateSmallTerritories(tempTerritorySpecs, targetNTerritories);

        territorySpecs = new TerritorySpec[tempTerritorySpecs.size()];
        for (int i = 0; i < territorySpecs.length; ++i) territorySpecs[i] = tempTerritorySpecs.get(i);

        detTerritoryBorders();
    }

    private void consolidateSmallTerritories(ArrayList<TerritorySpec> tempTerritorySpecs, int targetNTerritories) {
        int targetNLandFaces = Math.round((float)landFaces.length / (float)targetNTerritories);

        while (true) {
            boolean wasChange = false;
            for (int ti = 1; ti < tempTerritorySpecs.size(); ++ti) {
                TerritorySpec terr = tempTerritorySpecs.get(ti);
                if (terr == null) {
                    continue;
                }

                int currN = terr.landFaces.size();
                int bestDif = Math.abs(targetNLandFaces - currN);
                int bestAI = -1;
                for (int ai = 0; ai < terr.adjacentLandTerrs.size(); ++ai) {
                    TerritorySpec adjTerr = tempTerritorySpecs.get(terr.adjacentLandTerrs.keyAt(ai));
                    int potN = currN + adjTerr.landFaces.size();
                    int potDif = glm.abs(targetNLandFaces - potN);
                    if (potDif < bestDif) {
                        bestDif = potDif;
                        bestAI = ai;
                    }
                }

                if (bestAI != -1) {
                    // Combine current territory into other territory
                    int otherTI = terr.adjacentLandTerrs.keyAt(bestAI);
                    TerritorySpec otherTerr = tempTerritorySpecs.get(otherTI);
                    for (int fi : terr.landFaces) faces[fi].territory = otherTI;
                    for (int fi : terr.oceanFaces) faces[fi].territory = otherTI;
                    for (int fi : terr.coastFaces) faces[fi].territory = otherTI;
                    otherTerr.landFaces.addAll(terr.landFaces);
                    otherTerr.oceanFaces.addAll(terr.oceanFaces);
                    otherTerr.coastFaces.addAll(terr.coastFaces);
                    for (int ai = 0; ai < terr.adjacentLandTerrs.size(); ++ai) {
                        int adjTI = terr.adjacentLandTerrs.keyAt(ai);
                        TerritorySpec adjTerr = tempTerritorySpecs.get(adjTI);
                        adjTerr.adjacentLandTerrs.delete(ti);
                        adjTerr.adjacentLandTerrs.put(otherTI, otherTI);
                        otherTerr.adjacentLandTerrs.put(adjTI, adjTI);
                    }
                    for (int ai = 0; ai < terr.adjacentOceanTerrs.size(); ++ai) {
                        int adjTI = terr.adjacentOceanTerrs.keyAt(ai);
                        TerritorySpec adjTerr = tempTerritorySpecs.get(adjTI);
                        adjTerr.adjacentOceanTerrs.delete(ti);
                        adjTerr.adjacentOceanTerrs.put(otherTI, otherTI);
                        otherTerr.adjacentOceanTerrs.put(adjTI, adjTI);
                    }
                    otherTerr.adjacentLandTerrs.delete(otherTI);
                    otherTerr.adjacentOceanTerrs.delete(otherTI);
                    tempTerritorySpecs.set(ti, null);
                    wasChange = true;
                    break;
                }
            }
            if (!wasChange) {
                break;
            }
        }

        // Remove gaps
        SparseIntArray map = new SparseIntArray();
        for (int srcTI = 1, dstTI = 1; srcTI < tempTerritorySpecs.size(); ++srcTI) {
            if (tempTerritorySpecs.get(srcTI) != null) {
                map.put(srcTI, dstTI);
                ++dstTI;
            }
        }
        for (int i = 1; i < tempTerritorySpecs.size(); ++i) {
            if (tempTerritorySpecs.get(i) == null) {
                tempTerritorySpecs.remove(i);
                --i;
            }
        }
        for (int ti = 1; ti < tempTerritorySpecs.size(); ++ti) {
            TerritorySpec terr = tempTerritorySpecs.get(ti);
            SparseIntArray newLandAdjacents = new SparseIntArray();
            for (int ai = 0; ai < terr.adjacentLandTerrs.size(); ++ ai) {
                int ati = map.get(terr.adjacentLandTerrs.keyAt(ai));
                newLandAdjacents.put(ati, ati);
            }
            terr.adjacentLandTerrs = newLandAdjacents;
            SparseIntArray newOceanAdjacents = new SparseIntArray();
            for (int ai = 0; ai < terr.adjacentOceanTerrs.size(); ++ ai) {
                int ati = map.get(terr.adjacentOceanTerrs.keyAt(ai));
                newOceanAdjacents.put(ati, ati);
            }
            terr.adjacentOceanTerrs = newOceanAdjacents;
        }
        for (Face face : faces) {
            face.territory = map.get(face.territory);
        }
    }

    private void detTerritoryBorders() {
        for (int fi = 0; fi < faces.length; ++fi) {
            Face face = faces[fi];
            if (face.territory == 0) {
                continue;
            }
            for (int ai = 0; ai < 3; ++ai) {
                if (faces[face.adjacencies[ai]].territory != face.territory) {
                    territorySpecs[face.territory].borderFaces.add(fi);
                    break;
                }
            }
        }
    }

    private void smoothTerritories() {
        HashSet<Integer> toRefresh = new HashSet<>();

        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            for (int fi : terr.borderFaces) {
                Face face = faces[fi];
                int f1i = -1, f2i = -1, f3i = -1;
                for (int ai = 0; ai < 3; ++ai) {
                    int afi = face.adjacencies[ai];
                    if (faces[afi].territory != face.territory) {
                        if (f1i == -1) f1i = afi;
                        else if (f2i == -1) f2i = afi;
                        else f3i = afi;
                    }
                }

                if (f2i == -1 || f3i != -1) {
                    continue;
                }
                Face f1 = faces[f1i], f2 = faces[f2i];
                if (f1.territory != f2.territory || f1.territory == 0) {
                    continue;
                }
                int sign = glm.sign(face.coastDist);
                int sign1 = glm.sign(f1.coastDist);
                int sign2 = glm.sign(f2.coastDist);
                if (sign1 != sign || sign2 != sign || sign == 0) {
                    continue;
                }

                TerritorySpec terrOther = territorySpecs[f1.territory];
                boolean did = false;
                if (sign > 0) {
                    if (terrOther.landFaces.size() <= terr.landFaces.size()) {
                        terr.landFaces.remove(fi);
                        terrOther.landFaces.add(fi);
                        did = true;
                    }
                }
                else {
                    if (terrOther.oceanFaces.size() <= terr.oceanFaces.size()) {
                        terr.oceanFaces.remove(fi);
                        terrOther.oceanFaces.add(fi);
                        did = true;
                    }
                }
                if (did) {
                    face.territory = f1.territory;
                    toRefresh.add(fi);
                    for (int ai = 0; ai < 3; ++ai) toRefresh.add(face.adjacencies[ai]);
                }
            }

            for (int fi : toRefresh) refreshBorderStatus(fi);
            toRefresh.clear();
        }
    }

    private void refreshBorderStatus(int fi) {
        Face face = faces[fi];
        TerritorySpec terr = territorySpecs[face.territory];
        for (int ai = 0; ai < 3; ++ai) {
            if (faces[face.adjacencies[ai]].territory != face.territory) {
                terr.borderFaces.add(fi);
                return;
            }
        }
        terr.borderFaces.remove(fi);
    }

    /*private void subtractTerritory(int fi) {
        Face face = faces[fi];
        int territory = face.territory;

        int borderCount = 0;
        int dfi1 = -1, dfi2 = -1, dfi3 = -1;
        for (int ai = 0; ai < 3; ++ai) {
            int afi = face.adjacencies[ai];
            if (faces[afi].territory != face.territory) {
                ++borderCount;
                if (dfi1 == -1) dfi1 = afi;
                else if (dfi2 == -1) dfi2 = afi;
                else dfi3 = afi;
            }
        }

        if (borderCount == 1) {
            face.territory = faces[dfi1].territory;
        }
        else if (borderCount == 2) {
            Face f1 = faces[dfi1];
            Face f2 = faces[dfi2];
            int nt1 = territorySpecs[f1.territory].totalCount();
            int nt2 = territorySpecs[f2.territory].totalCount();
            face.territory = nt1 >= nt2 ? f1.territory : f2.territory;
        }
        else if (borderCount == 3) {
            Face f1 = faces[dfi1];
            Face f2 = faces[dfi2];
            Face f3 = faces[dfi3];
            int nt1 = territorySpecs[f1.territory].totalCount();
            int nt2 = territorySpecs[f2.territory].totalCount();
            int nt3 = territorySpecs[f3.territory].totalCount();
            face.territory = nt1 >= nt2 && nt1 >= nt3 ? f1.territory : nt2 >= nt3 ? f2.territory : f3.territory;
        }

        if (borderCount > 0) {
            if (face.coastDist >= 0) {
                --territorySpecs[territory].superCount;
                ++territorySpecs[face.territory].superCount;
            }
            else {
                --territorySpecs[territory].subCount;
                ++territorySpecs[face.territory].subCount;
            }
        }
    }*/

    private void detBorders() {
        borders = new boolean[locations.length];
        byte[] territories = new byte[borders.length];
        boolean[] isLand = new boolean[borders.length];
        Arrays.fill(territories, (byte)-1);
        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            Face face = faces[fi];
            for (int i = 0; i < 3; ++i) {
                int vi = indices[ii + i];
                if (territories[vi] == -1) {
                    territories[vi] = (byte)face.territory;
                    isLand[vi] = face.coastDist >= 0;
                }
                else if (territories[vi] != face.territory || isLand[vi] != face.coastDist >= 0) {
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

    private int nearestCoastFace(int fi) {
        while (true) {
            Face face = faces[fi];
            int dist = glm.abs(face.coastDist);
            if (face.coastDist == 0) {
                return fi;
            }

            int dist1 = glm.abs(faces[face.adjacencies[0]].coastDist);
            if (dist1 < dist) {
                fi = face.adjacencies[0];
                continue;
            }
            int dist2 = glm.abs(faces[face.adjacencies[1]].coastDist);
            if (dist2 < dist) {
                fi = face.adjacencies[1];
                continue;
            }
            int dist3 = glm.abs(faces[face.adjacencies[2]].coastDist);
            if (dist3 < dist) {
                fi = face.adjacencies[2];
                continue;
            }

            if (dist1 == dist) {
                fi = face.adjacencies[0];
                continue;
            }
            if (dist2 == dist) {
                fi = face.adjacencies[1];
                continue;
            }
            if (dist3 == dist) {
                fi = face.adjacencies[2];
                continue;
            }

            break;
        }

        // Should never get here
        return -1;
    }

}
