package csc309.geocracy;

import glm_.vec3.Vec3;

public abstract class MeshMaker {

    public static Mesh unindex(String name, Vec3[] locs, int[] indices) {
        Vec3[] newLocs = new Vec3[indices.length];
        Vec3[] newNorms = new Vec3[indices.length];

        for (int i = 0; i < indices.length; ++i) {
            newLocs[i] = locs[indices[i]];
        }

        for (int i = 0; i < indices.length; i += 3) {
            Vec3 norm = (newLocs[i + 1].minus(newLocs[i])).cross(newLocs[i + 2].minus(newLocs[i])).normalize();
            newNorms[i + 0] = norm;
            newNorms[i + 1] = norm;
            newNorms[i + 2] = norm;
        }

        return new Mesh(name, newLocs, newNorms, null);
    }

    public static Mesh makeIcosahedron(String name, boolean faces) {
        float phi = (float)((1.0 + Math.sqrt(5.0)) / 2.0);
        int nVerts = 12;
        Vec3[] locs = {
            (new Vec3( 0.0f,  1.0f,   phi)).normalize(),
            (new Vec3( 0.0f,  1.0f,  -phi)).normalize(),
            (new Vec3( 0.0f, -1.0f,   phi)).normalize(),
            (new Vec3( 0.0f, -1.0f,  -phi)).normalize(),

            (new Vec3(  phi,  0.0f,  1.0f)).normalize(),
            (new Vec3( -phi,  0.0f,  1.0f)).normalize(),
            (new Vec3(  phi,  0.0f, -1.0f)).normalize(),
            (new Vec3( -phi,  0.0f, -1.0f)).normalize(),

            (new Vec3( 1.0f,   phi,  0.0f)).normalize(),
            (new Vec3( 1.0f,  -phi,  0.0f)).normalize(),
            (new Vec3(-1.0f,   phi,  0.0f)).normalize(),
            (new Vec3(-1.0f,  -phi,  0.0f)).normalize(),
        };
        Vec3[] norms = {
            locs[ 0], locs[ 1], locs[ 2],
            locs[ 3], locs[ 4], locs[ 5],
            locs[ 6], locs[ 7], locs[ 8],
            locs[ 9], locs[10], locs[11]
        };

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

        if (faces) {
            return unindex(name, locs, indices);
        }

        return new Mesh(name, locs, norms, indices);
    }

}
