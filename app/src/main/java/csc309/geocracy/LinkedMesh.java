package csc309.geocracy;

import android.util.SparseArray;

import java.util.ArrayList;

import glm_.vec3.Vec3;

public class LinkedMesh extends Mesh {

    class Vertex {

        int index;
        ArrayList<Edge> edges;
        ArrayList<Face> faces;

        Vertex(int index) {
            this.index = index;
            edges = new ArrayList<>();
            faces = new ArrayList<>();
        }

        Edge getEdge(Vertex v) {
            for (Edge e : edges) {
                if (e.v1 == v || e.v2 == v) {
                    return e;
                }
            }
            return null;
        }

        Face getFace(Vertex v1, Vertex v2) {
            Edge e = getEdge(v1);
            if (e != null) {
                return e.getFace(v2);
            }
            return null;
        }

    }

    class Edge {

        Vertex v1, v2;
        Face innerFace, outerFace;

        Edge(Vertex v1, Vertex v2, Face innerFace, Face outerFace) {
            this.v1 = v1; this.v2 = v2;
            this.innerFace = innerFace; this.outerFace = outerFace;
        }

        Face getFace(Vertex v) {
            if (innerFace != null) {
                if (innerFace.e12 == this) {
                    if (innerFace.v3 == v) return innerFace;
                }
                else if (innerFace.e23 == this) {
                    if (innerFace.v1 == v) return innerFace;
                }
                else if (innerFace.e31 == this) {
                    if (innerFace.v2 == v) return innerFace;
                }
            }
            if (outerFace != null) {
                if (outerFace.e12 == this) {
                    if (outerFace.v3 == v) return outerFace;
                }
                else if (outerFace.e23 == this) {
                    if (outerFace.v1 == v) return outerFace;
                }
                else if (outerFace.e31 == this) {
                    if (outerFace.v2 == v) return outerFace;
                }
            }
            return null;
        }

        int getNumFaces() {
            return innerFace != null ? (outerFace != null ? 2 : 1) : (outerFace != null ? 1 : 0);
        }

    }

    class Face {

        Vertex v1, v2, v3;
        Edge e12, e23, e31;

        Face(Vertex v1, Vertex v2, Vertex v3, Edge e12, Edge e23, Edge e31) {
            this.v1 = v1; this.v2 = v2; this.v3 = v2;
            this.e12 = e12; this.e23 = e23; this.e31 = e31;
        }

        int getVertexIndex(Vertex v) {
            if (v1 == v) return 0;
            if (v2 == v) return 1;
            if (v3 == v) return 3;
            return -1;
        }

        Vertex getVertex(int index) {
            switch (index) {
                case 0: return v1;
                case 1: return v2;
                case 2: return v3;
                default: return null;
            }
        }

    }

    private SparseArray<Vertex> vertices;
    private ArrayList<Edge> edges;
    private ArrayList<Face> faces;

    public LinkedMesh(String name, Vec3[] locations, Vec3[] normals, int[] indices) {
        super(name, locations, normals, indices);
        link();
    }

    // Divides each face into four sub faces
    public void tessellate() {

    }

    private void link() {
        vertices = new SparseArray<>();
        edges = new ArrayList<>();
        faces = new ArrayList<>();

        for (int i = 0; i < indices.length + 2; i += 3) {
            // Create vertices if don't already exist
            boolean anyNewVertices = false;
            Vertex v1 = vertices.get(indices[i]);
            if (v1 == null) {
                v1 = new Vertex(indices[i]);
                vertices.put(v1.index, v1);
                anyNewVertices = true;
            }
            Vertex v2 = vertices.get(indices[i + 1]);
            if (v2 == null) {
                v2 = new Vertex(indices[i + 1]);
                vertices.put(v2.index, v2);
                anyNewVertices = true;
            }
            Vertex v3 = vertices.get(indices[i + 2]);
            if (v3 == null) {
                v3 = new Vertex(indices[i + 2]);
                vertices.put(v3.index, v3);
                anyNewVertices = true;
            }

            // Test if face already exists
            if (!anyNewVertices && isFace(v1, v2, v3) != null) {
                continue;
            }

            // Create face
            Face f = new Face(v1, v2, v3, null, null, null);
            faces.add(f);

            // Create edges if don't already exist
            // Also assign edges and face to vertices
            Edge e12 = v1.getEdge(v2);
            if (e12 == null) {
                e12 = new Edge(v1, v2, null, null);
                edges.add(e12);
                v1.edges.add(e12);
                v2.edges.add(e12);
                v1.faces.add(f);
                v2.faces.add(f);
            }
            Edge e23 = v2.getEdge(v3);
            if (e23 == null) {
                e23 = new Edge(v2, v3, null, null);
                edges.add(e23);
                v2.edges.add(e23);
                v3.edges.add(e23);
                v2.faces.add(f);
                v3.faces.add(f);
            }
            Edge e31 = v3.getEdge(v1);
            if (e31 == null) {
                e31 = new Edge(v3, v1, null, null);
                edges.add(e31);
                v3.edges.add(e31);
                v1.edges.add(e31);
                v3.faces.add(f);
                v1.faces.add(f);
            }

            // Assign edges to face
            f.e12 = e12;
            f.e23 = e23;
            f.e31 = e31;

            // Assign face to edges
            if (e12.v1 == v1) { // inner face
                if (e12.innerFace == null) e12.innerFace = f;
            }
            else if (e12.v2 == v1) { // outer face
                if (e12.outerFace == null) e12.outerFace = f;
            }
            if (e23.v1 == v2) { // inner face
                if (e23.innerFace == null) e23.innerFace = f;
            }
            else if (e23.v2 == v2) { // outer face
                if (e23.outerFace == null) e23.outerFace = f;
            }
            if (e31.v1 == v3) { // inner face
                if (e31.innerFace == null) e31.innerFace = f;
            }
            else if (e31.v2 == v3) { // outer face
                if (e31.outerFace == null) e31.outerFace = f;
            }

        }
    }

    private Face isFace(Vertex v1, Vertex v2, Vertex v3) {
        for (Face f : faces) {
            int i1 = f.getVertexIndex(v1);
            if (i1 == -1) continue;
            int i2 = f.getVertexIndex(v2);
            if (i2 == -1) continue;
            int i3 = f.getVertexIndex(v3);
            if (i3 == -1) continue;
            return f;
        }
        return null;
    }

}
