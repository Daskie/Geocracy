package csc309.geocracy;

import glm_.vec3.Vec3;

public abstract class MeshMaker {

    public static Mesh makeIcosahedron(String name) {
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

        return new Mesh(name, locs, norms, indices);
    }

    public static Mesh makeSphereUnindexed(String name, int degree) {
        Mesh icoMesh = makeIcosahedron(name);
        icoMesh.unindex();
        Vec3[] locations = icoMesh.getLocations();

        // Tessellate
        for (int iteration = 0; iteration < degree; ++iteration) {
            Vec3[] newLocations = new Vec3[locations.length * 4];
            // Tessellate each face
            for (int oldI = 0, newI = 0; oldI < locations.length; oldI += 3, newI += 12) {
                // All points involved
                Vec3 a = locations[oldI + 0];
                Vec3 b = locations[oldI + 1];
                Vec3 c = locations[oldI + 2];
                Vec3 d = a.plus(b); d.timesAssign(0.5f); d.normalizeAssign();
                Vec3 e = b.plus(c); e.timesAssign(0.5f); e.normalizeAssign();
                Vec3 f = c.plus(a); f.timesAssign(0.5f); f.normalizeAssign();

                newLocations[newI +  0] = a;
                newLocations[newI +  1] = new Vec3(d);
                newLocations[newI +  2] = new Vec3(f);
                newLocations[newI +  3] = b;
                newLocations[newI +  4] = new Vec3(e);
                newLocations[newI +  5] = new Vec3(d);
                newLocations[newI +  6] = c;
                newLocations[newI +  7] = new Vec3(f);
                newLocations[newI +  8] = new Vec3(e);
                newLocations[newI +  9] = d;
                newLocations[newI + 10] = e;
                newLocations[newI + 11] = f;
            }

            locations = newLocations;
        }

        // Set normals
        Vec3[] normals = new Vec3[locations.length];
        for (int i = 0; i < locations.length; i += 3) {
            Vec3 n = (locations[i + 1].minus(locations[i])).cross(locations[i + 2].minus(locations[i])).normalize();
            normals[i + 0] = new Vec3(n);
            normals[i + 1] = new Vec3(n);
            normals[i + 2] = new Vec3(n);
        }

        return new Mesh(name, locations, normals, null);
    }

}
