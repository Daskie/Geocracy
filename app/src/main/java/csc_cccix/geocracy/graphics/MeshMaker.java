package csc_cccix.geocracy.graphics;

import android.util.LongSparseArray;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.VecArrayUtil;
import glm_.vec3.Vec3;

public abstract class MeshMaker {

    private MeshMaker() {}

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
            -1.0f, -1.0f, 0.0f,
             1.0f, -1.0f, 0.0f,
            -1.0f,  1.0f, 0.0f,
             1.0f,  1.0f, 0.0f,
        };
        float[] normals = {
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f
        };
        int[] indices = { 0, 1, 3, 3, 2, 0 };

        return new Mesh(name, locations, normals, indices);
    }

    public static Mesh makeTetrahedron(String name) {
        float[] locations = {
             (float)Math.sqrt(8.0f / 9.0f),                           0.0f, -1.0f / 3.0f,
            -(float)Math.sqrt(2.0f / 9.0f),  (float)Math.sqrt(2.0f / 3.0f), -1.0f / 3.0f,
            -(float)Math.sqrt(2.0f / 9.0f), -(float)Math.sqrt(2.0f / 3.0f), -1.0f / 3.0f,
                                      0.0f,                           0.0f,         1.0f
        };
        float[] normals = locations.clone();

        int[] indices = {
            0, 2, 1,
            0, 1, 3,
            1, 2, 3,
            2, 0, 3
        };

        return new Mesh(name, locations, normals, indices);
    }

    public static Mesh makeCube(String name) {
        float[] locations = {
            -1.0f, -1.0f, -1.0f,
             1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
             1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
             1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
             1.0f,  1.0f,  1.0f
        };
        float[] normals = locations.clone();
        for (int i = 0; i < 8; ++i) VecArrayUtil.normalize(normals, i);
        int[] indices = {
            0, 2, 3, 3, 1, 0,
            0, 1, 5, 5, 4, 0,
            0, 4, 6, 6, 2, 0,
            7, 6, 4, 4, 5, 7,
            7, 5, 1, 1, 3, 7,
            7, 3, 2, 2, 6, 7
        };

        return new Mesh(name, locations, normals, indices);
    }

    public static Mesh makeIcosahedron(String name) {
        int nVertices = 12;
        float[] locations = {
             0.0f,  1.0f,   Util.PHI,
             0.0f,  1.0f,  -Util.PHI,
             0.0f, -1.0f,   Util.PHI,
             0.0f, -1.0f,  -Util.PHI,

              Util.PHI,  0.0f,  1.0f,
             -Util.PHI,  0.0f,  1.0f,
              Util.PHI,  0.0f, -1.0f,
             -Util.PHI,  0.0f, -1.0f,

             1.0f,   Util.PHI,  0.0f,
             1.0f,  -Util.PHI,  0.0f,
            -1.0f,   Util.PHI,  0.0f,
            -1.0f,  -Util.PHI,  0.0f
        };
        for (int i = 0; i < nVertices; ++i) {
            VecArrayUtil.normalize(locations, i);
        }
        float[] normals = locations.clone();

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
        int[] locI = { icoMesh.getNumVertices() };
        int[] indices = icoMesh.getIndices();

        for (int iteration = 0; iteration < degree; ++iteration) {
            indices = sphericallyTessellate(indices, locations, locI);
        }

        float[] normals = locations.clone();
        return new Mesh(name, locations, normals, indices);
    }

    private static int[] sphericallyTessellate(int[] indices, float[] locations, int[] locI) {
        int[] newIndices = new int[indices.length * 4];
        // key: low order int smaller vertex index, higher order int larger vertex index
        // value: vertex index of the edge's mid vertex
        LongSparseArray<Integer> edgeMap = new LongSparseArray<>();

        for (int ii = 0; ii < indices.length; ii += 3) {
            int ai = indices[ii + 0];
            int aci = ai * 3;
            int bi = indices[ii + 1];
            int bci = bi * 3;
            int ci = indices[ii + 2];
            int cci = ci * 3;
            int di;
            int dci;
            int ei;
            int eci;
            int fi;
            int fci;

            float ax = locations[aci + 0];
            float ay = locations[aci + 1];
            float az = locations[aci + 2];
            float bx = locations[bci + 0];
            float by = locations[bci + 1];
            float bz = locations[bci + 2];
            float cx = locations[cci + 0];
            float cy = locations[cci + 1];
            float cz = locations[cci + 2];
            float dx;
            float dy;
            float dz;
            float ex;
            float ey;
            float ez;
            float fx;
            float fy;
            float fz;

            // Obtain edge vertex d
            long key = ai < bi ? Util.toLong(ai, bi) : Util.toLong(bi, ai);
            Integer value = edgeMap.get(key);
            if (value == null) { // Edge vertex d not found. Create it
                dx = (ax + bx) * 0.5f; dy = (ay + by) * 0.5f; dz = (az + bz) * 0.5f;
                float fac = 1.0f / (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
                dx *= fac; dy *= fac; dz *= fac;
                di = locI[0]; dci = di * 3;
                locations[dci + 0] = dx; locations[dci + 1] = dy; locations[dci + 2] = dz;
                ++locI[0];
                edgeMap.put(key, di);
            }
            else { // Edge vertex d already exists
                di = value;
            }
            // Obtain edge vertex e
            key = bi < ci ? Util.toLong(bi, ci) : Util.toLong(ci, bi);
            value = edgeMap.get(key);
            if (value == null) { // Edge vertex e not found. Create it
                ex = (bx + cx) * 0.5f; ey = (by + cy) * 0.5f; ez = (bz + cz) * 0.5f;
                float fac = 1.0f / (float)Math.sqrt(ex * ex + ey * ey + ez * ez);
                ex *= fac; ey *= fac; ez *= fac;
                ei = locI[0]; eci = ei * 3;
                locations[eci + 0] = ex; locations[eci + 1] = ey; locations[eci + 2] = ez;
                ++locI[0];
                edgeMap.put(key, ei);
            }
            else { // Edge vertex e already exists
                ei = value;
            }
            // Obtain edge vertex f
            key = ci < ai ? Util.toLong(ci, ai) : Util.toLong(ai, ci);
            value = edgeMap.get(key);
            if (value == null) { // Edge vertex f not found. Create it
                fx = (cx + ax) * 0.5f; fy = (cy + ay) * 0.5f; fz = (cz + az) * 0.5f;
                float fac = 1.0f / (float)Math.sqrt(fx * fx + fy * fy + fz * fz);
                fx *= fac; fy *= fac; fz *= fac;
                fi = locI[0]; fci = fi * 3;
                locations[fci + 0] = fx; locations[fci + 1] = fy; locations[fci + 2] = fz;
                ++locI[0];
                edgeMap.put(key, fi);
            }
            else { // Edge vertex f already exists
                fi = value;
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

        return newIndices;
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
                int vi = fi * 3;
                int newVI = fi * 12;

                // All points involved
                VecArrayUtil.get(locations, vi + 0, a);
                VecArrayUtil.get(locations, vi + 1, b);
                VecArrayUtil.get(locations, vi + 2, c);
                Util.assign(d, a); d.plusAssign(b); d.timesAssign(0.5f); d.normalizeAssign();
                Util.assign(e, b); e.plusAssign(c); e.timesAssign(0.5f); e.normalizeAssign();
                Util.assign(f, c); f.plusAssign(a); f.timesAssign(0.5f); f.normalizeAssign();

                VecArrayUtil.set(newLocations, newVI +  0, a);
                VecArrayUtil.set(newLocations, newVI +  1, d);
                VecArrayUtil.set(newLocations, newVI +  2, f);
                VecArrayUtil.set(newLocations, newVI +  3, b);
                VecArrayUtil.set(newLocations, newVI +  4, e);
                VecArrayUtil.set(newLocations, newVI +  5, d);
                VecArrayUtil.set(newLocations, newVI +  6, c);
                VecArrayUtil.set(newLocations, newVI +  7, f);
                VecArrayUtil.set(newLocations, newVI +  8, e);
                VecArrayUtil.set(newLocations, newVI +  9, d);
                VecArrayUtil.set(newLocations, newVI + 10, e);
                VecArrayUtil.set(newLocations, newVI + 11, f);
            }

            nFaces = nNewFaces;
            locations = newLocations;
        }

        Mesh mesh = new Mesh(name, locations, new float[nFaces * 3 * 3], null);
        mesh.calcFaceNormals();
        return mesh;
    }

    public static Mesh makeCylinder(String name, int lod) {
        float theta = 2.0f * (float)Math.PI / lod;

        float[] circXs = new float[lod];
        float[] circYs = new float[lod];
        for (int i = 0; i < lod; ++i) {
            circXs[i] = (float)Math.cos(i * theta);
            circYs[i] = (float)Math.sin(i * theta);
        }

        int nCylVerts = lod * 2;
        int nCapVerts = lod * 2 + 2;
        int nVerts = nCylVerts + nCapVerts;

        float[] locs = new float[nVerts * 3];
        float[] norms = new float[nVerts * 3];

        for (int i = 0; i < lod; ++i) {
            int ci = i * 3;
            locs[ci] = circXs[i]; locs[ci + 1] = circYs[i]; locs[ci + 2] = 1.0f;
            norms[ci] = circXs[i]; norms[ci + 1] = circYs[i]; norms[ci + 2] = 0.0f;

            ci = (lod + i) * 3;
            locs[ci] = circXs[i]; locs[ci + 1] = circYs[i]; locs[ci + 2] = -1.0f;
            norms[ci] = circXs[i]; norms[ci + 1] = circYs[i]; norms[ci + 2] = 0.0f;

            ci = (2 * lod + i) * 3;
            locs[ci] = circXs[i]; locs[ci + 1] = circYs[i]; locs[ci + 2] = 1.0f;
            norms[ci] = 0.0f; norms[ci + 1] = 0.0f; norms[ci + 2] = 1.0f;

            ci = (3 * lod + i) * 3;
            locs[ci] = circXs[i]; locs[ci + 1] = circYs[i]; locs[ci + 2] = -1.0f;
            norms[ci] = 0.0f; norms[ci + 1] = 0.0f; norms[ci + 2] = -1.0f;
        }

        int ci = (nVerts - 2) * 3;
        locs[ci] = 0.0f; locs[ci + 1] = 0.0f; locs[ci + 2] = 1.0f;
        norms[ci] = 0.0f; norms[ci + 1] = 0.0f; norms[ci + 2] = 1.0f;
        ci = (nVerts - 1) * 3;
        locs[ci] = 0.0f; locs[ci + 1] = 0.0f; locs[ci + 2] = -1.0f;
        norms[ci] = 0.0f; norms[ci + 1] = 0.0f; norms[ci + 2] = -1.0f;

        int nIndices = lod * 6 + 2 * lod * 3;
        int[] indices = new int[nIndices];

        int ii = 0;
        for (int i = 0; i < lod; ++i) {
            indices[ii++] = i;
            indices[ii++] = i + lod;
            indices[ii++] = (i + 1) % lod;

            indices[ii++] = (i + 1) % lod + lod;
            indices[ii++] = (i + 1) % lod;
            indices[ii++] = i + lod;
        }
        for (int i = 0; i < lod; ++i) {
            indices[ii++] = nCylVerts + i;
            indices[ii++] = nCylVerts + (i + 1) % lod;
            indices[ii++] = nCylVerts + nCapVerts - 2;
        }
        for (int i = 0; i < lod; ++i) {
            indices[ii++] = nCylVerts + (i + 1) % lod + lod;
            indices[ii++] = nCylVerts + i + lod;
            indices[ii++] = nCylVerts + nCapVerts - 1;
        }

        return new Mesh(name, locs, norms, indices);
    }

}
