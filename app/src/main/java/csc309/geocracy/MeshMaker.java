package csc309.geocracy;

import android.util.LongSparseArray;

import csc309.geocracy.graphics.Mesh;
import glm_.vec3.Vec3;

public abstract class MeshMaker {

    public static Mesh makeTriangle(String name) {
        float[] locations = {
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        };
        float[] normals = {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f
        };
        int[] indices = { 0, 1, 2 };

        return new Mesh(name, locations, normals, indices);
    }

    public static Mesh makeSquare(String name) {
        float[] locations = {
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        };
        float[] normals = {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f
        };
        int[] indices = { 0, 1, 2, 2, 3, 0 };

        return new Mesh(name, locations, normals, indices);
    }

    public static Mesh makeIcosahedron(String name) {
        float phi = (float)((1.0 + Math.sqrt(5.0)) / 2.0);
        int nVertices = 12;
        float[] locations = {
             0.0f,  1.0f,   phi,
             0.0f,  1.0f,  -phi,
             0.0f, -1.0f,   phi,
             0.0f, -1.0f,  -phi,

              phi,  0.0f,  1.0f,
             -phi,  0.0f,  1.0f,
              phi,  0.0f, -1.0f,
             -phi,  0.0f, -1.0f,

             1.0f,   phi,  0.0f,
             1.0f,  -phi,  0.0f,
            -1.0f,   phi,  0.0f,
            -1.0f,  -phi,  0.0f
        };
        for (int i = 0; i < nVertices; ++i) {
            Util.normalizeVec3(locations, i);
        }
        float[] normals = locations.clone();

        int nIndices = 60;
        int[] indices = {
             0,  2,  4,  2,  0,  5,
             3,  1,  6,  1,  3,  7,
             4,  6,  8,  6,  4,  9,
             7,  5, 10,  5,  7, 11,
             8,  10, 0, 10,  8,  1,
            11,  9,  2,  9, 11,  3,
             0,  4,  8,
             1,  8,  6,
             2,  9,  4,
             3,  6,  9,
             0, 10,  5,
             1,  7, 10,
             2,  5, 11,
             3, 11,  7
        };

        return new Mesh(name, locations, normals, indices);
    }

    public static Mesh makeSphereIndexed(String name, int degree) {
        Mesh icoMesh = makeIcosahedron(null);
        if (degree == 0) {
            return icoMesh;
        }

        float[] locations = new float[detIndexedSphereVertexCount(degree) * 3];
        System.arraycopy(icoMesh.getLocations(), 0, locations, 0, icoMesh.getLocations().length);
        int nextI = icoMesh.getNumVertices();
        int[] indices = icoMesh.getIndices();

        for (int iteration = 0; iteration < degree; ++iteration) {
            int[] newIndices = new int[indices.length * 4];
            // key: low order int smaller vertex index, higher order int larger vertex index
            // value: vertex index of the edge's mid vertex
            LongSparseArray<Integer> edgeMap = new LongSparseArray<>();

            for (int ii = 0; ii < indices.length; ii += 3) {
                int ai = indices[ii + 0], aci = ai * 3;
                int bi = indices[ii + 1], bci = bi * 3;
                int ci = indices[ii + 2], cci = ci * 3;
                int di, dci, ei, eci, fi, fci;

                float ax = locations[aci + 0], ay = locations[aci + 1], az = locations[aci + 2];
                float bx = locations[bci + 0], by = locations[bci + 1], bz = locations[bci + 2];
                float cx = locations[cci + 0], cy = locations[cci + 1], cz = locations[cci + 2];
                float dx, dy, dz, ex, ey, ez, fx, fy, fz;

                // Obtain edge vertex d
                long key = ai < bi ? Util.toLong(ai, bi) : Util.toLong(bi, ai);
                Integer value = edgeMap.get(key);
                if (value == null) { // Edge vertex d not found. Create it
                    dx = (ax + bx) * 0.5f; dy = (ay + by) * 0.5f; dz = (az + bz) * 0.5f;
                    float fac = 1.0f / (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
                    dx *= fac; dy *= fac; dz *= fac;
                    di = nextI; dci = di * 3;
                    locations[dci + 0] = dx; locations[dci + 1] = dy; locations[dci + 2] = dz;
                    ++nextI;
                    edgeMap.put(key, di);
                }
                else { // Edge vertex d already exists
                    di = value; dci = di * 3;
                    dx = locations[dci + 0]; dy = locations[dci + 1]; dz = locations[dci + 2];
                }
                // Obtain edge vertex e
                key = bi < ci ? Util.toLong(bi, ci) : Util.toLong(ci, bi);
                value = edgeMap.get(key);
                if (value == null) { // Edge vertex e not found. Create it
                    ex = (bx + cx) * 0.5f; ey = (by + cy) * 0.5f; ez = (bz + cz) * 0.5f;
                    float fac = 1.0f / (float)Math.sqrt(ex * ex + ey * ey + ez * ez);
                    ex *= fac; ey *= fac; ez *= fac;
                    ei = nextI; eci = ei * 3;
                    locations[eci + 0] = ex; locations[eci + 1] = ey; locations[eci + 2] = ez;
                    ++nextI;
                    edgeMap.put(key, ei);
                }
                else { // Edge vertex e already exists
                    ei = value; eci = ei * 3;
                    ex = locations[eci + 0]; ey = locations[eci + 1]; ez = locations[eci + 2];
                }
                // Obtain edge vertex f
                key = ci < ai ? Util.toLong(ci, ai) : Util.toLong(ai, ci);
                value = edgeMap.get(key);
                if (value == null) { // Edge vertex f not found. Create it
                    fx = (cx + ax) * 0.5f; fy = (cy + ay) * 0.5f; fz = (cz + az) * 0.5f;
                    float fac = 1.0f / (float)Math.sqrt(fx * fx + fy * fy + fz * fz);
                    fx *= fac; fy *= fac; fz *= fac;
                    fi = nextI; fci = fi * 3;
                    locations[fci + 0] = fx; locations[fci + 1] = fy; locations[fci + 2] = fz;
                    ++nextI;
                    edgeMap.put(key, fi);
                }
                else { // Edge vertex f already exists
                    fi = value; fci = fi * 3;
                    fx = locations[fci + 0]; fy = locations[fci + 1]; fz = locations[fci + 2];
                }

                int nii = ii * 4;
                // New face 1 : a d f
                newIndices[nii +  0] = ai;
                newIndices[nii +  1] = di;
                newIndices[nii +  2] = fi;
                // New face 2: b e d
                newIndices[nii +  3] = bi;
                newIndices[nii +  4] = ei;
                newIndices[nii +  5] = di;
                // New face 3: c f e
                newIndices[nii +  6] = ci;
                newIndices[nii +  7] = fi;
                newIndices[nii +  8] = ei;
                // New face 4: d e f
                newIndices[nii +  9] = di;
                newIndices[nii + 10] = ei;
                newIndices[nii + 11] = fi;
            }

            indices = newIndices;
        }

        float[] normals = locations.clone();
        return new Mesh(name, locations, normals, indices);
    }

    private static int detIndexedSphereVertexCount(int degree) {
        if (degree == 0) {
            return 20;
        }

        int i = degree + 1;
        int n = ((1 << i) + 2) * ((1 << i) + 4) / 8;
        int e = (1 << (i - 1)) - 1;
        return 20 * n - 30 * e - 12 * 4;
    }

    public static Mesh makeSphereUnindexed(String name, int degree) {
        Mesh icoMesh = makeIcosahedron(null);
        icoMesh.unindex();

        int nFaces = icoMesh.getNumVertices() / 3;
        float[] locations = icoMesh.getLocations();

        Vec3 a = new Vec3();
        Vec3 b = new Vec3();
        Vec3 c = new Vec3();
        Vec3 d = new Vec3();
        Vec3 e = new Vec3();
        Vec3 f = new Vec3();

        // Tessellate
        for (int iteration = 0; iteration < degree; ++iteration) {
            int nNewFaces = nFaces * 4;
            final float[] newLocations = new float[nNewFaces * 3 * 3];

            // Tessellate each face
            for (int fi = 0; fi < nFaces; ++fi) {
                int vi = fi * 3, newVI = fi * 12;

                // All points involved
                Util.getVec3(locations, vi + 0, a);
                Util.getVec3(locations, vi + 1, b);
                Util.getVec3(locations, vi + 2, c);
                Util.assign(d, a); d.plusAssign(b); d.timesAssign(0.5f); d.normalizeAssign();
                Util.assign(e, b); e.plusAssign(c); e.timesAssign(0.5f); e.normalizeAssign();
                Util.assign(f, c); f.plusAssign(a); f.timesAssign(0.5f); f.normalizeAssign();

                Util.setVec3(newLocations, newVI +  0, a);
                Util.setVec3(newLocations, newVI +  1, d);
                Util.setVec3(newLocations, newVI +  2, f);
                Util.setVec3(newLocations, newVI +  3, b);
                Util.setVec3(newLocations, newVI +  4, e);
                Util.setVec3(newLocations, newVI +  5, d);
                Util.setVec3(newLocations, newVI +  6, c);
                Util.setVec3(newLocations, newVI +  7, f);
                Util.setVec3(newLocations, newVI +  8, e);
                Util.setVec3(newLocations, newVI +  9, d);
                Util.setVec3(newLocations, newVI + 10, e);
                Util.setVec3(newLocations, newVI + 11, f);
            }

            nFaces = nNewFaces;
            locations = newLocations;
        }

        Mesh mesh = new Mesh(name, locations, new float[nFaces * 3 * 3], null);
        mesh.calcFaceNormals();
        return mesh;
    }

}
