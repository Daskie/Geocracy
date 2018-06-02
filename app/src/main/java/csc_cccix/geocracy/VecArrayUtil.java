package csc_cccix.geocracy;

import glm_.vec3.Vec3;

public class VecArrayUtil {

    private VecArrayUtil() {

    }

    public static void assign(float[] dst, int dstI, float[] src, int srcI) {
        int dci = dstI * 3;
        int sci = srcI * 3;
        dst[dci + 0] = src[sci + 0];
        dst[dci + 1] = src[sci + 1];
        dst[dci + 2] = src[sci + 2];
    }

    // Returns a Vec3 from an xyz float array at the given vertex index
    public static Vec3 get(float[] arr, int vertexI) {
        int ci = vertexI * 3;
        return new Vec3(arr[ci + 0], arr[ci + 1], arr[ci + 2]);
    }

    public static void get(float[] arr, int vertexI, Vec3 dst) {
        int ci = vertexI * 3;
        dst.x = arr[ci + 0]; dst.y = arr[ci + 1]; dst.z = arr[ci + 2];
    }

    // Sets xyz at the given vertex index to that of the given Vec3
    public static void set(float[] arr, int vertexI, Vec3 v) {
        int ci = vertexI * 3;
        arr[ci + 0] = v.x; arr[ci + 1] = v.y; arr[ci + 2] = v.z;
    }

    public static void add(float[] arr, int vertexI, float v) {
        int ci = vertexI * 3;
        arr[ci + 0] += v;
        arr[ci + 1] += v;
        arr[ci + 2] += v;
    }

    public static void add(float[] arr, int vertexI, Vec3 v) {
        int ci = vertexI * 3;
        arr[ci + 0] += v.x;
        arr[ci + 1] += v.y;
        arr[ci + 2] += v.z;
    }

    public static void multiply(float[] arr, int vertexI, float v) {
        int ci = vertexI * 3;
        arr[ci + 0] *= v;
        arr[ci + 1] *= v;
        arr[ci + 2] *= v;
    }

    public static void multiply(float[] arr, int vertexI, Vec3 v) {
        int ci = vertexI * 3;
        arr[ci + 0] *= v.x;
        arr[ci + 1] *= v.y;
        arr[ci + 2] *= v.z;
    }

    // Normalizes the xyz at the given vertex index in arr
    public static void normalize(float[] arr, int vertexI) {
        int ci = vertexI * 3;
        float x = arr[ci + 0];
        float y = arr[ci + 1];
        float z = arr[ci + 2];
        float v = x * x + y * y + z * z;
        if (Util.isZero(v)) {
            x = 0.0f; y = 0.0f; z = 0.0f;
        }
        else {
            v = 1.0f / (float)Math.sqrt(v);
            x *= v; y *= v; z *= v;
        }
        arr[ci + 0] = x; arr[ci + 1] = y; arr[ci + 2] = z;
    }

    public static float length2(float[] arr, int vertexI) {
        int ci = vertexI * 3;
        float x = arr[ci + 0];
        float y = arr[ci + 1];
        float z = arr[ci + 2];
        return x * x + y * y + z * z;
    }

    public static float length(float[] arr, int vertexI) {
        return (float)Math.sqrt(length2(arr, vertexI));
    }
}
