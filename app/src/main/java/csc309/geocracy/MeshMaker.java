package csc309.geocracy;

import glm_.vec3.Vec3;

public abstract class MeshMaker {

    public static Mesh makeIcosahedron(String name) {
        float phi = (float)((1.0 + Math.sqrt(5.0)) / 2.0);
        int nVerts = 12;
        float[] locs = {
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
        for (int i = 0; i < nVerts; ++i) {
            Util.normalize(locs, i);
        }
        float[] norms = locs.clone();

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

        return new Mesh(name, locs, norms, indices);
    }

    public static Mesh makeSphereUnindexed(String name, int degree, boolean allowMultithreading) {
        Mesh icoMesh = makeIcosahedron(name);
        icoMesh.unindex();
        int nThreads = allowMultithreading ? Runtime.getRuntime().availableProcessors() : 0;

        int nFaces = icoMesh.getNumVertices() / 3;
        float[] locations = icoMesh.getLocations();

        // Tessellate
        for (int iteration = 0; iteration < degree; ++iteration) {
            int nNewFaces = nFaces * 4;
            final float[] newLocations = new float[nNewFaces * 3 * 3];

            // Tessellate each face
            if (iteration >= 5 && nThreads >= 2) {
                tessellateSphericalMultithreaded(locations, newLocations, 0, nFaces, nThreads);
            }
            else {
                tessellateSpherical(locations, newLocations, 0, nFaces);
            }

            nFaces = nNewFaces;
            locations = newLocations;
        }

        Mesh mesh = new Mesh(name, locations, new float[nFaces * 3 * 3], null);
        mesh.calcFaceNormals();
        return mesh;
    }

    private static void tessellateSpherical(float[] locations, float[] newLocations, int startFaceI, int endFaceI) {
        Vec3 a = new Vec3();
        Vec3 b = new Vec3();
        Vec3 c = new Vec3();
        Vec3 d = new Vec3();
        Vec3 e = new Vec3();
        Vec3 f = new Vec3();

        for (int fi = startFaceI; fi < endFaceI; ++fi) {
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
    }

    private static void tessellateSphericalMultithreaded(final float[] locations, final float[] newLocations, int startFaceI, int endFaceI, int nThreads) {
        int nFaces = endFaceI - startFaceI;
        int nFacesPerThread = nFaces / nThreads;
        Thread[] threads = new Thread[nThreads];
        for (int t = 0; t < nThreads; ++t) {
            final int threadStartFaceI = startFaceI + nFacesPerThread * t;
            final int threadEndFaceI = t == nThreads - 1 ? endFaceI : threadStartFaceI + nFacesPerThread;
            threads[t] = new Thread(new Runnable() {
                @Override
                public void run() {
                    tessellateSpherical(locations, newLocations, threadStartFaceI, threadEndFaceI);
                }
            });
            threads[t].start();
        }
        for (int t = 0; t < nThreads; ++t) {
            try {
                threads[t].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
