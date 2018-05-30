package csc309.geocracy.world;

import android.opengl.GLES30;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import csc309.geocracy.Util;
import csc309.geocracy.VecArrayUtil;
import csc309.geocracy.game.Game;
import csc309.geocracy.graphics.Camera;
import csc309.geocracy.graphics.Mesh;
import csc309.geocracy.noise.SimplexNoise;
import glm_.mat3x3.Mat3;
import glm_.vec3.Vec3;

import static glm_.Java.glm;

public class Terrain {

    private class Face {
        int[] adjacencies = new int[3];
        int coastDist;
        int territory;
        int inlandDist;
    }

    private class TerritorySpec {
        HashSet<Integer> landFaces = new HashSet<>();
        HashSet<Integer> oceanFaces = new HashSet<>();
        HashSet<Integer> coastFaces = new HashSet<>();
        SparseArray<HashSet<Integer>> inlandFaces = new SparseArray<>();
        HashSet<Integer> adjacentLandTerrs = new HashSet<>();
        HashSet<Integer> adjacentOceanTerrs = new HashSet<>();
        HashSet<Integer> waterwayTerrs = new HashSet<>();
        int continent;
        Vec3 center;
        int[] armyFaces;
        Vec3[] armyLocations;
        Mat3[] armyOrientations;
    }

    private class ContinentSpec {
        HashSet<Integer> territories = new HashSet<>();
        HashSet<Integer> adjacentLandConts = new HashSet<>();
        HashSet<Integer> adjacentOceanConts = new HashSet<>();
        HashSet<Integer> waterwayConts = new HashSet<>();
    }

    private static final float HIGH_ELEVATION = 1.05f, LOW_ELEVATION = 0.975f;
    private static final int MIN_TERRITORY_LAND_FACES = Game.MAX_ARMIES_PER_TERRITORY * 2;

    private World world;
    private TerrainShader shader;
    private IdentityShader idShader;
    private float[] locations;
    private int[] indices;
    private int vboHandle;
    private int vaoHandle;
    private Face[] faces;
    private float[] faceNormals;
    private int[] landFaces;
    private int[] oceanFaces;
    private int[] coastFaces;
    private int maxCoastDist;
    private TerritorySpec[] territorySpecs;
    private ContinentSpec[] continentSpecs;
    private int[] verticesInfo;
    private float[] vertInlandDists;
    private float continentHueOffset;

    public Terrain(World world, Mesh sphereMesh, long seed, int maxNTerritories, int maxNContinents) {
        this.world = world;
        shader = new TerrainShader();
        idShader = new IdentityShader();
        locations = sphereMesh.getLocations().clone();
        indices = sphereMesh.getIndices().clone();
        faces = new Face[indices.length / 3];
        for (int fi = 0; fi < faces.length; ++fi) faces[fi] = new Face();

        Random rand = new Random(seed);
        terraform(rand);
        detFaceNormals();
        genFaceAdjacencies();
        categorizeFaces();
        createTerritories(maxNTerritories, rand);
        createContinents(maxNContinents);
        detWaterways();
        createVerticesInfo();

        continentHueOffset = rand.nextFloat();
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("Terrain", "Failed to load shader");
            return false;
        }
        shader.setActive();
        shader.setLowElevationFactor(1.0f / (LOW_ELEVATION - 1.0f));
        shader.setHighElevationFactor(1.0f / (HIGH_ELEVATION - 1.0f));
        Vec3[] contColors = new Vec3[world.getContinents().length + 1];
        contColors[0] = new Vec3();
        for (int i = 0; i < world.getContinents().length; ++i) {
            contColors[i + 1] = world.getContinents()[i].getColor();
        }
        shader.setContinentColors(contColors);
        shader.setSelectedTerritory(0);
        shader.setHighlightedTerritories(null);
//        shader.setPlayerColors(world.game.getPlayers());
//        shader.setTerritoryPlayers(world.getTerritories());
        if (Util.isGLError()) {
            Log.e("Terrain", "Failed to initialize shader uniforms");
            return false;
        }

        if (!idShader.load()) {
            Log.e("Terrain", "Failed to load identity shader");
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
        GLES30.glEnableVertexAttribArray(3);
        int vertexSize = 3 * 4 + 3 * 4 + 4 + 4;
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, vertexSize, 0); // locations
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, vertexSize, 3 * 4); // normals
        GLES30.glVertexAttribIPointer(2, 1, GLES30.GL_INT, vertexSize, 3 * 4 + 3 * 4); // info
        GLES30.glVertexAttribPointer(3, 1, GLES30.GL_FLOAT, false, vertexSize, 3 * 4 + 3 * 4 + 4); // inland distance
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
        GLES30.glDisableVertexAttribArray(2);
        GLES30.glDisableVertexAttribArray(3);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("Terrain", "Failed to setup vao");
            return false;
        }

        return true;
    }

    public void render(long t, Camera camera, Vec3 lightDir, boolean selectionChange, boolean highlightChange) {
        shader.setActive();
        shader.setViewMatrix(camera.getViewMatrix());
        shader.setProjectionMatrix(camera.getProjectionMatrix());
        shader.setLightDirection(lightDir);
        shader.setTime((float)glm.fract((double)t * 1.0e-9));
        if (selectionChange) {
            if (world.getSelectedTerritory() != null) {
                shader.setSelectedTerritory(world.getSelectedTerritory().getId());
            }
            else {
                shader.setSelectedTerritory(0);
            }
        }
        if (highlightChange) {
            boolean[] terrsHighlighted = new boolean[territorySpecs.length];
            for (Territory terr : world.getHighlightedTerritories()) terrsHighlighted[terr.getId()] = true;
            shader.setHighlightedTerritories(terrsHighlighted);
        }

        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        GLES30.glBindVertexArray(0);
    }

    public void renderId(Camera camera) {
        idShader.setActive();
        idShader.setViewMatrix(camera.getViewMatrix());
        idShader.setProjectionMatrix(camera.getProjectionMatrix());

        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, indices.length);
        GLES30.glBindVertexArray(0);
    }

    public void unload() {
        shader.unload();

        idShader.unload();

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

    Pair<Territory[], Continent[]> retrieveTerrsConts() {
        Territory[] territories = new Territory[territorySpecs.length - 1];
        Continent[] continents = new Continent[continentSpecs.length - 1];

        Vec3[] contColors = Util.genDistinctColors(continentSpecs.length - 1, continentHueOffset);

        for (int ci = 1; ci < continentSpecs.length; ++ci) {
            continents[ci - 1] = new Continent(ci, world, new HashSet<>(), contColors[ci - 1]);
        }
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            territories[ti - 1] = new Territory(ti, world, continents[terr.continent - 1], new HashSet<>(), terr.center);
        }
        for (int ci = 1; ci < continentSpecs.length; ++ci) {
            HashSet<Territory> contTerrs = continents[ci - 1].getTerritories();
            for (int ti : continentSpecs[ci].territories) {
                contTerrs.add(territories[ti - 1]);
            }
        }
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            HashSet<Territory> adjTerrs = territories[ti - 1].getAdjacentTerritories();
            for (int ati : terr.adjacentLandTerrs) {
                adjTerrs.add(territories[ati - 1]);
            }
            for (int ati : terr.waterwayTerrs) {
                adjTerrs.add(territories[ati - 1]);
            }
        }

        return new Pair<>(territories, continents);
    }

    Waterways createWaterways(int nSegments, float separation) {
        int nWaterways = 0;
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            nWaterways += territorySpecs[ti].waterwayTerrs.size();
        }

        Vec3[] startPoints = new Vec3[nWaterways];
        Vec3[] endPoints = new Vec3[nWaterways];
        int[] originTerrs = new int[nWaterways];
        int[] originConts = new int[nWaterways];
        int wi = 0;
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            for (int wti : terr.waterwayTerrs) {
                Pair<Vec3, Vec3> pair = calcWaterwayPointsBetween(ti, wti);
                startPoints[wi] = pair.first;
                endPoints[wi] = pair.second;
                originTerrs[wi] = ti;
                originConts[wi] = terr.continent;
                ++wi;
            }
        }

        for (int i = 0; i < nWaterways; ++i) {
            Vec3 startP = startPoints[i];
            Vec3 endP = endPoints[i];
            Vec3 lat = endP.minus(startP).cross(startP).normalizeAssign();
            lat.timesAssign(separation * 0.5f);
            startP.plusAssign(lat);
            endP.plusAssign(lat);
        }

        return new Waterways(world, nSegments, startPoints, endPoints, originTerrs, originConts);
    }

    Vec3[] getTerritoryArmyLocations(int ti) {
        return territorySpecs[ti].armyLocations;
    }

    Mat3[] getTerritoryArmyOrientations(int ti) {
        return territorySpecs[ti].armyOrientations;
    }

    private Pair<Vec3, Vec3> calcWaterwayPointsBetween(int t1i, int t2i) {
        SparseArray<Vec3> faces1 = new SparseArray<>();
        SparseArray<Vec3> faces2 = new SparseArray<>();
        for (int cfi : territorySpecs[t1i].coastFaces) {
            for (int fi : faces[cfi].adjacencies) {
                Face face = faces[fi];
                if (face.territory == t1i && face.coastDist > 0) {
                    faces1.put(fi, getFaceCenter(fi));
                }
            }
        }
        for (int cfi : territorySpecs[t2i].coastFaces) {
            for (int fi : faces[cfi].adjacencies) {
                Face face = faces[fi];
                if (face.territory == t2i && face.coastDist > 0) {
                    faces2.put(fi, getFaceCenter(fi));
                }
            }
        }

        float minDist = Float.POSITIVE_INFINITY;
        int minFI1 = -1, minFI2 = -1;
        for (int i1 = 0; i1 < faces1.size(); ++i1) {
            int fi1 = faces1.keyAt(i1);
            Vec3 center1 = faces1.valueAt(i1);
            int penalty1 = distToDifferentLandTerritoryWithin(fi1, 2);
            for (int i2 = 0; i2 < faces2.size(); ++i2) {
                int fi2 = faces2.keyAt(i2);
                Vec3 center2 = faces2.valueAt(i2);
                int penalty2 = distToDifferentLandTerritoryWithin(fi2, 2);

                float dist = center2.minus(center1).getLength2();
                dist *= 1 << (penalty1 + penalty2);
                if (dist < minDist) {
                    minDist = dist;
                    minFI1 = fi1;
                    minFI2 = fi2;
                }
            }
        }

        return new Pair<>(faces1.get(minFI1).normalizeAssign(), faces2.get(minFI2).normalizeAssign());
    }

    private int distToDifferentLandTerritoryWithin(int origFI, int n) {
        Face face = faces[origFI];
        int ti = face.territory;
        HashSet<Integer> checked = new HashSet<>();
        HashSet<Integer> fringe = new HashSet<>();
        HashSet<Integer> nextFringe = new HashSet<>();
        checked.add(origFI);
        fringe.add(origFI);
        for (int i = 0; i < n && !fringe.isEmpty(); ++i) {
            for (int fi : fringe) {
                for (int afi : faces[fi].adjacencies) {
                    if (checked.contains(afi)) {
                        continue;
                    }
                    if (faces[afi].coastDist < 1) {
                        continue;
                    }
                    if (faces[afi].territory != ti) {
                        return i + 1;
                    }
                    nextFringe.add(afi);
                }
            }
            HashSet<Integer> temp = fringe;
            fringe = nextFringe;
            nextFringe = temp;
            nextFringe.clear();
            checked.addAll(fringe);
        }
        return 0;
    }

    private ByteBuffer genVertexBufferData() {
        int vertexSize = 3 * 4 + 3 * 4 + 4 + 4;
        ByteBuffer vertexData = ByteBuffer.allocateDirect(indices.length * vertexSize);
        vertexData.order(ByteOrder.nativeOrder());

        // Interlace vertex data
        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            int vi1 = indices[ii + 0];
            int vi2 = indices[ii + 1];
            int vi3 = indices[ii + 2];
            int ci1 = vi1 * 3;
            int ci2 = vi2 * 3;
            int ci3 = vi3 * 3;

            float nx = faceNormals[ii + 0];
            float ny = faceNormals[ii + 1];
            float nz = faceNormals[ii + 2];

            vertexData.putFloat(locations[ci1 + 0]);
            vertexData.putFloat(locations[ci1 + 1]);
            vertexData.putFloat(locations[ci1 + 2]);
            vertexData.putFloat(nx);
            vertexData.putFloat(ny);
            vertexData.putFloat(nz);
            vertexData.putInt(verticesInfo[ii + 0]);
            vertexData.putFloat(vertInlandDists[vi1]);
            vertexData.putFloat(locations[ci2 + 0]);
            vertexData.putFloat(locations[ci2 + 1]);
            vertexData.putFloat(locations[ci2 + 2]);
            vertexData.putFloat(nx);
            vertexData.putFloat(ny);
            vertexData.putFloat(nz);
            vertexData.putInt(verticesInfo[ii + 1]);
            vertexData.putFloat(vertInlandDists[vi2]);
            vertexData.putFloat(locations[ci3 + 0]);
            vertexData.putFloat(locations[ci3 + 1]);
            vertexData.putFloat(locations[ci3 + 2]);
            vertexData.putFloat(nx);
            vertexData.putFloat(ny);
            vertexData.putFloat(nz);
            vertexData.putInt(verticesInfo[ii + 2]);
            vertexData.putFloat(vertInlandDists[vi3]);
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

    private void terraform(Random rand) {
        final int k_nOctaves = 5;
        final float k_initFrequency = 0.75f;
        final float k_persistence = 0.5f;
        final float k_dropoffHeight = 0.01f;

        SimplexNoise simplex = new SimplexNoise(rand);
        //float maxAmplitude = ((float)(1 << k_nOctaves) - 1) / (float)(1 << (k_nOctaves - 1));
        float superRange = HIGH_ELEVATION - 1.0f;
        float subRange = 1.0f - LOW_ELEVATION;
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
            v = (v - minV) * adjustFactor - 1.0f; // in range [-1, 1]

            v = sinkFactor * (float)Math.pow(v + 1.0f, sinkExp) - 1.0f;

            // Super transformation (make mountainous)
            if (v >= 0.0f) {
                v *= v * v;
            }
            // Sub transformation (make gradual drop-off)
            else {
                v += 1;
                v *= v;
                v -= 1;
            }
            // [-1, 1]

            if (v > 0.0f) {
                v = (1.0f - k_dropoffHeight) * v + k_dropoffHeight; // small offset from sea level to help with z-fighting
                v = 1.0f + v * superRange;
            }
            else if (v < 0.0f) {
                v = (1.0f - k_dropoffHeight) * v - k_dropoffHeight; // small offset from sea level to help with z-fighting
                v = 1.0f + v * subRange; // [lowElevation, highElevation]
            }
            // [lowElevation, highElevation]

            locations[ci + 0] *= v;
            locations[ci + 1] *= v;
            locations[ci + 2] *= v;
        }
    }

    private void detFaceNormals() {
        faceNormals = new float[faces.length * 3];

        Vec3 faceNorm = new Vec3();
        Vec3 v1 = new Vec3();
        Vec3 v12 = new Vec3(), v13 = new Vec3();
        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            int vi1 = indices[ii + 0];
            int vi2 = indices[ii + 1];
            int vi3 = indices[ii + 2];

            VecArrayUtil.get(locations, vi1, v1);
            VecArrayUtil.get(locations, vi2, v12);
            VecArrayUtil.get(locations, vi3, v13);
            v12.minusAssign(v1);
            v13.minusAssign(v1);
            faceNorm.put(v12);
            faceNorm.crossAssign(v13);
            faceNorm.normalizeAssign();
            VecArrayUtil.set(faceNormals, fi, faceNorm);
        }
    }

    private void categorizeFaces() {
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
            }
            for (Integer fi : currFaces) {
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

    private void createTerritories(int maxNTerritories, Random rand) {
        ArrayList<Integer> spawnFaces = new ArrayList<>();

        // Spawn on random land faces
        for (int ti = 0; ti < maxNTerritories; ++ti) spawnFaces.add(landFaces[rand.nextInt(landFaces.length)]);

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
                        terr.adjacentLandTerrs.add(face.territory);
                        tempTerritorySpecs.get(face.territory).adjacentLandTerrs.add(territory);
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
                    if (face.coastDist <= 0 && !subFringes.contains(fi)) {
                        face.territory = territory;
                        terr.oceanFaces.add(fi);
                        subFringes.add(fi);
                        subFringeQueue.addLast(fi);
                    }
                }
                else if (face.territory != territory) {
                    terr.adjacentOceanTerrs.add(face.territory);
                    tempTerritorySpecs.get(face.territory).adjacentOceanTerrs.add(territory);
                }
            }
            subFringeQueue.removeFirst();
            subFringes.remove(origFI);
        }

        handleSmallAndExcessTerritories(tempTerritorySpecs, maxNTerritories);

        territorySpecs = new TerritorySpec[tempTerritorySpecs.size()];
        for (int i = 0; i < territorySpecs.length; ++i) territorySpecs[i] = tempTerritorySpecs.get(i);

        smoothTerritories();

        detInlandDists();
        detTerritoryCenters();
        detArmyFaces();
        detArmyLocationsAndOrientations();
    }

    private void handleSmallAndExcessTerritories(ArrayList<TerritorySpec> tempTerritorySpecs, int maxNTerritories) {
        int nTerritories = tempTerritorySpecs.size() - 1;
        while (nTerritories > maxNTerritories) {
            // Find which two adjacent territories combined is the smallest
            int minN = Integer.MAX_VALUE;
            int minT1I = -1, minT2I = -1;
            boolean doingLand = true;
            for (int t1i = 1; t1i < tempTerritorySpecs.size(); ++t1i) {
                TerritorySpec terr1 = tempTerritorySpecs.get(t1i);
                if (terr1 == null) {
                    continue;
                }
                for (int t2i : doingLand ? terr1.adjacentLandTerrs : terr1.adjacentOceanTerrs) {
                    TerritorySpec terr2 = tempTerritorySpecs.get(t2i);
                    int potN = terr1.landFaces.size() + terr2.landFaces.size();
                    if (potN < minN) {
                        minN = potN;
                        minT1I = t1i;
                        minT2I = t2i;
                    }
                }
            }
            if (minT1I == -1 || minT2I == -1) {
                doingLand = false;
            }
            mergeTerritories(tempTerritorySpecs, minT1I, minT2I);
            --nTerritories;
        }

        while (true) {
            boolean wasMerger = false;
            for (int t1i = 1; t1i < tempTerritorySpecs.size(); ++t1i) {
                TerritorySpec terr1 = tempTerritorySpecs.get(t1i);
                if (terr1 == null) {
                    continue;
                }
                if (terr1.landFaces.size() >= MIN_TERRITORY_LAND_FACES) {
                    continue;
                }
                int minN = Integer.MAX_VALUE;
                int minT2I = -1;
                for (int t2i : terr1.adjacentLandTerrs) {
                    if (tempTerritorySpecs.get(t2i).landFaces.size() < minN) {
                        minT2I = t2i;
                    }
                }
                if (minT2I == -1){
                    for (int t2i : terr1.adjacentOceanTerrs) {
                        if (tempTerritorySpecs.get(t2i).landFaces.size() < minN) {
                            minT2I = t2i;
                        }
                    }
                }
                if (minT2I == -1) {
                    break;
                }
                mergeTerritories(tempTerritorySpecs, t1i, minT2I);
                --nTerritories;
                wasMerger = true;
                break;
            }
            if (!wasMerger) {
                break;
            }
        }

        // Remove gaps
        if (nTerritories < tempTerritorySpecs.size() - 1) {
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
                HashSet<Integer> newLandAdjacents = new HashSet<>();
                for (int ati : terr.adjacentLandTerrs) {
                    newLandAdjacents.add(map.get(ati));
                }
                terr.adjacentLandTerrs = newLandAdjacents;
                HashSet<Integer> newOceanAdjacents = new HashSet<>();
                for (int ati : terr.adjacentOceanTerrs) {
                    newOceanAdjacents.add(map.get(ati));
                }
                terr.adjacentOceanTerrs = newOceanAdjacents;
            }
            for (Face face : faces) {
                face.territory = map.get(face.territory);
            }
        }
    }

    private void mergeTerritories(ArrayList<TerritorySpec> tempTerritorySpecs, int dstTI, int srcTI) {
        TerritorySpec dstTerr = tempTerritorySpecs.get(dstTI);
        TerritorySpec srcTerr = tempTerritorySpecs.get(srcTI);
        for (int fi : srcTerr.landFaces) faces[fi].territory = dstTI;
        for (int fi : srcTerr.oceanFaces) faces[fi].territory = dstTI;
        for (int fi : srcTerr.coastFaces) faces[fi].territory = dstTI;
        dstTerr.landFaces.addAll(srcTerr.landFaces);
        dstTerr.oceanFaces.addAll(srcTerr.oceanFaces);
        dstTerr.coastFaces.addAll(srcTerr.coastFaces);
        for (int ati : srcTerr.adjacentLandTerrs) {
            TerritorySpec adjTerr = tempTerritorySpecs.get(ati);
            adjTerr.adjacentLandTerrs.remove(srcTI);
            adjTerr.adjacentLandTerrs.add(dstTI);
            dstTerr.adjacentLandTerrs.add(ati);
        }
        for (int ati : srcTerr.adjacentOceanTerrs) {
            TerritorySpec adjTerr = tempTerritorySpecs.get(ati);
            adjTerr.adjacentOceanTerrs.remove(srcTI);
            adjTerr.adjacentOceanTerrs.add(dstTI);
            dstTerr.adjacentOceanTerrs.add(ati);
        }
        dstTerr.adjacentLandTerrs.remove(dstTI);
        dstTerr.adjacentOceanTerrs.remove(dstTI);
        tempTerritorySpecs.set(srcTI, null);
    }

    private void smoothTerritories() {
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            for (int fi = 0; fi < faces.length; ++fi) {
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
                if (sign * sign1 < 0 || sign * sign2 < 0) {
                    continue;
                }

                TerritorySpec terrOther = territorySpecs[f1.territory];
                if (sign >= 0) {
                    if ((terrOther.landFaces.size() + terrOther.coastFaces.size()) <= (terr.landFaces.size() + terr.coastFaces.size())) {
                        if (sign > 0) {
                            terr.landFaces.remove(fi);
                            terrOther.landFaces.add(fi);
                        }
                        else {
                            terr.coastFaces.remove(fi);
                            terrOther.coastFaces.add(fi);
                        }
                        face.territory = f1.territory;
                    }
                }
                else {
                    if (terrOther.oceanFaces.size() <= terr.oceanFaces.size()) {
                        terr.oceanFaces.remove(fi);
                        terrOther.oceanFaces.add(fi);
                        face.territory = f1.territory;
                    }
                }
            }
        }
    }

    private void detInlandDists() {
        HashSet<Integer> checked = new HashSet<>();
        HashSet<Integer> fringe = new HashSet<>();
        HashSet<Integer> nextFringe = new HashSet<>();
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            checked.clear();
            fringe.clear();
            nextFringe.clear();
            for (int fi : terr.landFaces) {
                Face face = faces[fi];
                for (int ai = 0; ai < 3; ++ai) {
                    int afi = face.adjacencies[ai];
                    if (!terr.landFaces.contains(afi)) {
                        fringe.add(fi);
                        checked.add(fi);
                        break;
                    }
                }
            }
            int dist = 0;
            while (true) {
                terr.inlandFaces.put(dist, new HashSet<>(fringe));
                for (int fi : fringe) {
                    Face face = faces[fi];
                    for (int ai = 0; ai < 3; ++ai) {
                        int afi = face.adjacencies[ai];
                        if (!checked.contains(afi) && terr.landFaces.contains(afi)) {
                            faces[afi].inlandDist = dist + 1;
                            nextFringe.add(afi);
                            checked.add(afi);
                        }
                    }
                }
                if (nextFringe.isEmpty()) {
                    break;
                }
                HashSet<Integer> temp = fringe;
                fringe = nextFringe;
                nextFringe = temp;
                nextFringe.clear();
                ++dist;
            }
        }
    }

    private void detTerritoryCenters() {
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            terr.center = new Vec3();
            for (int fi : terr.inlandFaces.valueAt(terr.inlandFaces.size() - 1)) {
                terr.center.plusAssign(getFaceCenter(fi));
            }
            terr.center.normalizeAssign();
        }
    }

    private void detArmyFaces() {
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            terr.armyFaces = new int[Game.MAX_ARMIES_PER_TERRITORY];

            int armyI = 0;
            int dist = terr.inlandFaces.size() - 1;
            HashSet<Integer> checked = new HashSet<>();
            while (dist >= 0) {
                for (int fi : terr.inlandFaces.valueAt(dist)) {
                    if (armyI >= terr.armyFaces.length) {
                        break;
                    }

                    if (checked.contains(fi)) {
                        continue;
                    }

                    terr.armyFaces[armyI] = fi;
                    checked.add(fi);
                    checked.add(faces[fi].adjacencies[0]);
                    checked.add(faces[fi].adjacencies[1]);
                    checked.add(faces[fi].adjacencies[2]);
                    ++armyI;
                }
                if (armyI >= terr.armyFaces.length) {
                    break;
                }

                --dist;
            }

            // Not enough faces for armies
            if (armyI < terr.armyFaces.length) {
                Log.e("Terrain", "Not enough faces in territory for armies");
            }
        }
    }

    private void detArmyLocationsAndOrientations() {
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            int nArmies = terr.armyFaces.length;
            terr.armyLocations = new Vec3[nArmies];
            terr.armyOrientations = new Mat3[nArmies];

            for (int ai = 0; ai < nArmies; ++ai) {
                terr.armyLocations[ai] = getFaceCenter(terr.armyFaces[ai]);

                Vec3 w = VecArrayUtil.get(faceNormals, terr.armyFaces[ai]);
                Vec3 u = terr.armyLocations[ai].minus(terr.center);
                Vec3 v = w.cross(u);
                if (Util.isZero(v)) v = Util.ortho(w);
                v.normalizeAssign();
                u = v.cross(w);
                terr.armyOrientations[ai] = new Mat3(u, v, w);
            }
        }
    }

    private void createContinents(int maxNContinents) {
        ArrayList<ArrayList<Integer>> terrsByInterconnections = new ArrayList<>();
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            int nInterconnections = terr.adjacentLandTerrs.size();
            while (terrsByInterconnections.size() < nInterconnections + 1) {
                terrsByInterconnections.add(new ArrayList<>());
            }
            terrsByInterconnections.get(nInterconnections).add(ti);
        }

        ArrayList<ContinentSpec> tempContinentSpecs = new ArrayList<>();
        tempContinentSpecs.add(null);
        for (int nic = terrsByInterconnections.size() - 1; nic >= 0; --nic) {
            for (int ti : terrsByInterconnections.get(nic)) {
                TerritorySpec terr = territorySpecs[ti];
                if (terr.continent != 0) {
                    continue;
                }
                int ci = getTerritoryAdjacentLandContinentMajority(ti);
                if (ci <= 0) {
                    ContinentSpec cont = new ContinentSpec();
                    cont.territories.add(ti);
                    terr.continent = tempContinentSpecs.size();
                    for (int ati : terr.adjacentLandTerrs) {
                        TerritorySpec adjTerr = territorySpecs[ati];
                        if (adjTerr.continent == 0) {
                            cont.territories.add(ati);
                            adjTerr.continent = terr.continent;
                        }
                    }
                    tempContinentSpecs.add(cont);
                }
                else {
                    terr.continent = ci;
                }
            }
        }

        detContinentAdjacencies(tempContinentSpecs);

        handleSmallAndExcessContinents(tempContinentSpecs, maxNContinents);

        continentSpecs = new ContinentSpec[tempContinentSpecs.size()];
        for (int ci = 0; ci < continentSpecs.length; ++ci) {
            continentSpecs[ci] = tempContinentSpecs.get(ci);
        }
    }

    private int getTerritoryAdjacentLandContinentMajority(int ti) {
        TerritorySpec terr = territorySpecs[ti];
        SparseIntArray continentCounts = new SparseIntArray();
        for (int ati : terr.adjacentLandTerrs) {
            TerritorySpec adjTerr = territorySpecs[ati];
            continentCounts.put(adjTerr.continent, continentCounts.get(adjTerr.continent, 1));
        }
        int maxCount = 0;
        int maxCI = -1;
        for (int i = 0; i < continentCounts.size(); ++i) {
            int count = continentCounts.valueAt(i);
            if (count > maxCount) {
                maxCount = count;
                maxCI = continentCounts.keyAt(i);
            }
        }
        return maxCI;
    }

    private void detContinentAdjacencies(ArrayList<ContinentSpec> tempContinentSpecs) {
        for (int ti = 1; ti < territorySpecs.length; ++ti) {
            TerritorySpec terr = territorySpecs[ti];
            for (int ati : terr.adjacentLandTerrs) {
                TerritorySpec adjTerr = territorySpecs[ati];
                if (terr.continent != adjTerr.continent) {
                    tempContinentSpecs.get(terr.continent).adjacentLandConts.add(adjTerr.continent);
                    tempContinentSpecs.get(adjTerr.continent).adjacentLandConts.add(terr.continent);
                }
            }
            for (int ati : terr.adjacentOceanTerrs) {
                TerritorySpec adjTerr = territorySpecs[ati];
                if (terr.continent != adjTerr.continent) {
                    tempContinentSpecs.get(terr.continent).adjacentOceanConts.add(adjTerr.continent);
                    tempContinentSpecs.get(adjTerr.continent).adjacentOceanConts.add(terr.continent);
                }
            }
        }
    }

    private void handleSmallAndExcessContinents(ArrayList<ContinentSpec> tempContinentSpecs, int maxNContinents) {
        int nContinents = tempContinentSpecs.size() - 1;

        // Handle excess continents
        // Try to merge land connected continents
        while (nContinents > maxNContinents) {
            // Find two adjacent continents that combined are smallest
            int minN = Integer.MAX_VALUE;
            int minC1I = -1, minC2I = -1;
            for (int c1i = 1; c1i < tempContinentSpecs.size(); ++c1i) {
                ContinentSpec cont = tempContinentSpecs.get(c1i);
                if (cont == null) {
                    continue;
                }
                for (int c2i : cont.adjacentLandConts) {
                    ContinentSpec adjCont = tempContinentSpecs.get(c2i);
                    int potN = cont.territories.size() + adjCont.territories.size();
                    if (potN < minN) {
                        minN = potN;
                        minC1I = c1i;
                        minC2I = c2i;
                    }
                }
            }
            if (minC1I == -1 || minC2I == -1) {
                break;
            }
            mergeContinents(tempContinentSpecs, minC1I, minC2I);
            --nContinents;
        }
        // Try to merge islands
        while (nContinents > maxNContinents) {
            // Find smallest island continent
            int minN = Integer.MAX_VALUE;
            int minC1I = -1;
            for (int ci = 1; ci < tempContinentSpecs.size(); ++ci) {
                ContinentSpec cont = tempContinentSpecs.get(ci);
                if (cont == null) {
                    continue;
                }
                if (!cont.adjacentLandConts.isEmpty()) {
                    continue;
                }
                if (cont.territories.size() < minN) {
                    minN = cont.territories.size();
                    minC1I = ci;
                }
            }
            // Find nearest continent
            int minC2I = detNearestContinentAccrossOcean(minC1I, tempContinentSpecs.get(minC1I));
            mergeContinents(tempContinentSpecs, minC1I, minC2I);
            --nContinents;
        }

        // Handle small continents
        for (int ci = 1; ci < tempContinentSpecs.size(); ++ci) {
            ContinentSpec cont = tempContinentSpecs.get(ci);
            if (cont == null) {
                continue;
            }
            if (cont.territories.size() <= 1) {
                int minN = Integer.MAX_VALUE;
                int minCI = -1;
                int minSingleCI = -1;
                for (int aci : cont.adjacentLandConts) {
                    ContinentSpec adjCont = tempContinentSpecs.get(aci);
                    int potN = adjCont.territories.size();
                    if (potN < minN) {
                        if (potN > 1) {
                            minN = potN;
                            minCI = aci;
                        }
                        else {
                            minSingleCI = aci;
                        }
                    }
                }
                if (minCI == -1 && minSingleCI == -1) {
                    minCI = detNearestContinentAccrossOcean(ci, cont);
                }
                mergeContinents(tempContinentSpecs, minCI != -1 ? minCI : minSingleCI, ci);
                --nContinents;
            }
        }

        // Remove gaps
        if (nContinents < tempContinentSpecs.size() - 1) {
            SparseIntArray map = new SparseIntArray();
            for (int srcCI = 1, dstCI = 1; srcCI < tempContinentSpecs.size(); ++srcCI) {
                if (tempContinentSpecs.get(srcCI) != null) {
                    map.put(srcCI, dstCI);
                    ++dstCI;
                }
            }
            for (int i = 1; i < tempContinentSpecs.size(); ++i) {
                if (tempContinentSpecs.get(i) == null) {
                    tempContinentSpecs.remove(i);
                    --i;
                }
            }
            for (int ci = 1; ci < tempContinentSpecs.size(); ++ci) {
                ContinentSpec cont = tempContinentSpecs.get(ci);
                HashSet<Integer> newLandAdjacents = new HashSet<>();
                HashSet<Integer> newOceanAdjacents = new HashSet<>();
                for (int aci : cont.adjacentLandConts) newLandAdjacents.add(map.get(aci));
                for (int aci : cont.adjacentOceanConts) newOceanAdjacents.add(map.get(aci));
                cont.adjacentLandConts = newLandAdjacents;
                cont.adjacentOceanConts = newOceanAdjacents;
            }
            for (int ti = 1; ti < territorySpecs.length; ++ti) {
                TerritorySpec terr = territorySpecs[ti];
                terr.continent = map.get(terr.continent);
            }
        }
    }

    private void mergeContinents(ArrayList<ContinentSpec> tempContinentSpecs, int dstCI, int srcCI) {
        ContinentSpec dstCont = tempContinentSpecs.get(dstCI);
        ContinentSpec srcCont = tempContinentSpecs.get(srcCI);
        for (int ti : srcCont.territories) territorySpecs[ti].continent = dstCI;
        dstCont.territories.addAll(srcCont.territories);
        for (int aci : srcCont.adjacentLandConts) {
            ContinentSpec adjCont = tempContinentSpecs.get(aci);
            adjCont.adjacentLandConts.remove(srcCI);
            adjCont.adjacentLandConts.add(dstCI);
            dstCont.adjacentLandConts.add(aci);
        }
        for (int aci : srcCont.adjacentOceanConts) {
            ContinentSpec adjCont = tempContinentSpecs.get(aci);
            adjCont.adjacentOceanConts.remove(srcCI);
            adjCont.adjacentOceanConts.add(dstCI);
            dstCont.adjacentOceanConts.add(aci);
        }
        dstCont.adjacentLandConts.remove(dstCI);
        dstCont.adjacentOceanConts.remove(dstCI);
        tempContinentSpecs.set(srcCI, null);
    }

    private int detNearestContinentAccrossOcean(int ci, ContinentSpec cont) {
        HashSet<Integer> currFringe = new HashSet<>();
        for (int ti : cont.territories) currFringe.addAll(territorySpecs[ti].coastFaces);
        HashSet<Integer> nextFringe = new HashSet<>();
        HashSet<Integer> checked = new HashSet<>(currFringe);
        while (true) {
            if (currFringe.isEmpty()) {
                return -1;
            }
            for (int fi : currFringe) {
                Face face = faces[fi];
                for (int afi : face.adjacencies) {
                    if (checked.contains(afi)) {
                        continue;
                    }
                    int fci = territorySpecs[faces[afi].territory].continent;
                    if (fci != ci && !cont.adjacentOceanConts.contains(fci)) {
                        continue;
                    }
                    if (faces[afi].coastDist < 0) {
                        nextFringe.add(afi);
                        checked.add(afi);
                    }
                    else if (fci != ci) {
                        return fci;
                    }
                }
            }
            currFringe.clear();
            HashSet<Integer> temp = currFringe;
            currFringe = nextFringe;
            nextFringe = temp;
        }
    }

    private void detWaterways() {
        // Connect portions of continent separated by ocean
        for (int ci = 1; ci < continentSpecs.length; ++ci) {
            ArrayList<HashSet<Integer>> lands = detContinentLands(ci);
            if (lands.size() <= 1) {
                continue;
            }
            for (int l1i = 0; l1i < lands.size() - 1; ++l1i) {
                for (int l2i = l1i + 1; l2i < lands.size(); ++l2i) {
                    Pair<Integer, Integer> pair = detMainWaterway(lands.get(l1i), lands.get(l2i));
                    if (pair == null) {
                        continue;
                    }
                    int t1i = pair.first, t2i = pair.second;
                    territorySpecs[t1i].waterwayTerrs.add(t2i);
                    territorySpecs[t2i].waterwayTerrs.add(t1i);
                }
            }
        }
        // Connect adjacent continents not connected by land
        for (int c1i = 1; c1i < continentSpecs.length; ++c1i) {
            ContinentSpec cont1 = continentSpecs[c1i];
            for (int c2i : cont1.adjacentOceanConts) {
                ContinentSpec cont2 = continentSpecs[c2i];
                if (!cont1.adjacentLandConts.contains(c2i) && !cont1.waterwayConts.contains(c2i)) {
                    Pair<Integer, Integer> pair = detMainWaterway(cont1.territories, cont2.territories);
                    if (pair == null) {
                        continue;
                    }
                    int t1i = pair.first, t2i = pair.second;
                    territorySpecs[t1i].waterwayTerrs.add(t2i);
                    territorySpecs[t2i].waterwayTerrs.add(t1i);
                    cont1.waterwayConts.add(c2i);
                    cont2.waterwayConts.add(c1i);
                }
            }
        }
    }

    private ArrayList<HashSet<Integer>> detContinentLands(int ci) {
        ContinentSpec cont = continentSpecs[ci];
        ArrayList<HashSet<Integer>> lands = new ArrayList<>();
        HashSet<Integer> land = new HashSet<>();
        HashSet<Integer> terrsLeft = new HashSet<>(cont.territories);
        HashSet<Integer> fringe = new HashSet<>();
        HashSet<Integer> nextFringe = new HashSet<>();
        while (!terrsLeft.isEmpty()) {
            int startTI = terrsLeft.iterator().next();
            land.add(startTI);
            terrsLeft.remove(startTI);
            fringe.add(startTI);
            while (!fringe.isEmpty()) {
                for (int ti : fringe) {
                    TerritorySpec terr = territorySpecs[ti];
                    for (int ati : terr.adjacentLandTerrs) {
                        if (territorySpecs[ati].continent == ci && !land.contains(ati)) {
                            land.add(ati);
                            terrsLeft.remove(ati);
                            nextFringe.add(ati);
                        }
                    }
                }
                HashSet<Integer> temp = fringe;
                fringe = nextFringe;
                nextFringe = temp;
                nextFringe.clear();
            }
            lands.add(land);
            land = new HashSet<>();
        }
        return lands;
    }

    private Pair<Integer, Integer> detMainWaterway(HashSet<Integer> terrs1, HashSet<Integer> terrs2) {
        LongSparseArray<Integer> counts = new LongSparseArray<>();
        for (int ti : terrs1) {
            for (int fi : territorySpecs[ti].oceanFaces) {
                for (int afi : faces[fi].adjacencies) {
                    int ati = faces[afi].territory;
                    if (ati != ti && terrs2.contains(ati)) {
                        long key = Util.toLong(ti, ati);
                        counts.put(key, counts.get(key, 0) + 1);
                    }
                }
            }
        }
        int maxCount = 0;
        int maxTI1 = -1, maxTI2 = -1;
        for (int i = 0; i < counts.size(); ++i) {
            if (counts.valueAt(i) > maxCount) {
                maxCount = counts.valueAt(i);
                Pair<Integer, Integer> pair = Util.fromLong(counts.keyAt(i));
                maxTI1 = pair.first;
                maxTI2 = pair.second;
            }
        }

        if (maxTI1 == -1 || maxTI2 == -1) {
            return null;
        }

        return new Pair<>(maxTI1, maxTI2);
    }

    private int detOceanDistanceBetweenTerritories(int t1i, int t2i) {
        TerritorySpec terr1 = territorySpecs[t1i];
        TerritorySpec terr2 = territorySpecs[t2i];
        if (!terr1.adjacentOceanTerrs.contains(t2i)) {
            return -1;
        }
        HashSet<Integer> currFringe1 = new HashSet<>(terr1.coastFaces);
        HashSet<Integer> currFringe2 = new HashSet<>(terr2.coastFaces);
        HashSet<Integer> nextFringe1 = new HashSet<>();
        HashSet<Integer> nextFringe2 = new HashSet<>();
        HashSet<Integer> checked1 = new HashSet<>(currFringe1);
        HashSet<Integer> checked2 = new HashSet<>(currFringe2);
        int count1 = 0, count2 = 0;
        while (true) {
            if (currFringe1.isEmpty() || currFringe2.isEmpty()) {
                return -1;
            }
            for (int fi : currFringe1) {
                Face face = faces[fi];
                for (int afi : face.adjacencies) {
                    if (checked1.contains(afi)) {
                        continue;
                    }
                    if (checked2.contains(afi)) {
                        return count1 + count2;
                    }
                    if (faces[afi].coastDist < 0) {
                        nextFringe1.add(afi);
                        checked1.add(afi);
                    }
                }
            }
            ++count1;
            for (int fi : currFringe2) {
                Face face = faces[fi];
                for (int afi : face.adjacencies) {
                    if (checked2.contains(afi)) {
                        continue;
                    }
                    if (checked1.contains(afi)) {
                        return count1 + count2;
                    }
                    if (faces[afi].coastDist < 0) {
                        nextFringe2.add(afi);
                        checked2.add(afi);
                    }
                }
            }
            ++count2;
            currFringe1.clear();
            currFringe2.clear();
            HashSet<Integer> temp = currFringe1;
            currFringe1 = nextFringe1;
            nextFringe1 = temp;
            temp = currFringe2;
            currFringe2 = nextFringe2;
            nextFringe2 = temp;
        }
    }

    private void createVerticesInfo() {
        verticesInfo = new int[indices.length];
        int nVerts = locations.length;
        boolean[] vertBorders = new boolean[nVerts];
        byte[] vertTerrs = new byte[nVerts];
        byte[] vertSigns = new byte[nVerts];
        vertInlandDists = new float[nVerts];
        int[] vertCounts = new int[nVerts];
        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            Face face = faces[fi];
            for (int i = 0; i < 3; ++i) {
                int vi = indices[ii + i];
                ++vertCounts[vi];
                vertInlandDists[vi] += face.inlandDist;
                if (vertTerrs[vi] == 0) {
                    vertTerrs[vi] = (byte)face.territory;
                    vertSigns[vi] = (byte)glm.sign(face.coastDist);
                }
                else if (vertTerrs[vi] != face.territory || vertSigns[vi] != glm.sign(face.coastDist)) {
                    vertBorders[vi] = true;
                }
            }
        }
        for (int vi = 0; vi < nVerts; ++vi) {
            vertInlandDists[vi] /= vertCounts[vi];
        }

        for (int fi = 0; fi < faces.length; ++fi) {
            int ii = fi * 3;
            Face face = faces[fi];
            Face af1 = faces[face.adjacencies[0]];
            Face af2 = faces[face.adjacencies[1]];
            Face af3 = faces[face.adjacencies[2]];
            int sign = glm.sign(face.coastDist);
            boolean e12 = (face.territory != af1.territory || glm.sign(af1.coastDist) != sign);
            boolean e23 = (face.territory != af2.territory || glm.sign(af2.coastDist) != sign);
            boolean e31 = (face.territory != af3.territory || glm.sign(af3.coastDist) != sign);

            int info = Util.toInt((byte)face.coastDist, (byte)face.territory, (byte)(territorySpecs[face.territory].continent >= 16 ? 0 : territorySpecs[face.territory].continent & 0xF), (byte)face.inlandDist);
            verticesInfo[ii + 0] = info | (((vertBorders[indices[ii + 0]] ? 1 : 0) | (e12 ? 2 : 0)                 | (e31 ? 8 : 0)) << 20);
            verticesInfo[ii + 1] = info | (((vertBorders[indices[ii + 1]] ? 1 : 0) | (e12 ? 2 : 0) | (e23 ? 4 : 0)                ) << 20);
            verticesInfo[ii + 2] = info | (((vertBorders[indices[ii + 2]] ? 1 : 0)                 | (e23 ? 4 : 0) | (e31 ? 8 : 0)) << 20);
        }
    }

    private Vec3 getFaceCenter(int fi) {
        int ii = fi * 3;
        Vec3 v = VecArrayUtil.get(locations, indices[ii + 0]);
        v.plusAssign(VecArrayUtil.get(locations, indices[ii + 1]));
        v.plusAssign(VecArrayUtil.get(locations, indices[ii + 2]));
        v.timesAssign(1.0f / 3.0f);
        return v;
    }

}
