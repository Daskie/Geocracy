package csc309.geocracy;

import android.opengl.GLES30;
import android.os.Process;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import glm_.vec2.Vec2;
import glm_.vec3.Vec3;
import glm_.vec4.Vec4;

import static android.opengl.GLU.gluErrorString;

public abstract class Util {

    // Reads text file from assets folder into string
    public static String readTextFile(String filename) {

        try {
            InputStream is = Global.getContext().getAssets().open(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder lines = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                lines.append(line);
                lines.append("\n");
            }
            return lines.toString();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Checks the OpenGL state for an error and logs it if found
    public static boolean isGLError(boolean printError) {
        int error = GLES30.glGetError();
        if (error == GLES30.GL_NO_ERROR) {
            return false;
        }

        if (printError) Log.e("Util", "OpenGL error: " + Integer.toHexString(error) + "(" + gluErrorString(error) + ")");
        return true;
    }

    public static boolean isGLError() {
        return isGLError(true);
    }

    // Hard exit application
    public static void exit() {
        Process.killProcess(Process.myPid());
    }

    // Returns a vector rotated 90 degrees CCW
    public static Vec2 orthogonal(Vec2 v) {
        return new Vec2(-v.y, v.x.floatValue());
    }

    // For use with floating point comparisons
    public static final float EPSILON = 1e-6f;

    public static boolean isZero(float v) {
        return Math.abs(v) < EPSILON;
    }

    public static boolean isZero(Vec2 v) {
        return isZero(v.x) && isZero(v.y);
    }

    public static boolean isZero(Vec3 v) {
        return isZero(v.x) && isZero(v.y) && isZero(v.z);
    }

    public static boolean isZero(Vec4 v) {
        return isZero(v.x) && isZero(v.y) && isZero(v.z) && isZero(v.w);
    }

    public static boolean areEqual(float v1, float v2) {
        return isZero(v1 - v2);
    }

    public static boolean areEqual(Vec2 v1, Vec2 v2) {
        return areEqual(v1.x, v2.x) && areEqual(v1.y, v2.y);
    }

    public static boolean areEqual(Vec3 v1, Vec3 v2) {
        return areEqual(v1.x, v2.x) && areEqual(v1.y, v2.y) && areEqual(v1.z, v2.z);
    }

    public static boolean areEqual(Vec4 v1, Vec4 v2) {
        return areEqual(v1.x, v2.x) && areEqual(v1.y, v2.y) && areEqual(v1.z, v2.z) && areEqual(v1.w, v2.w);
    }

    public static void assign(Vec3 dst, Vec3 src) {
        dst.x = src.x;
        dst.y = src.y;
        dst.z = src.z;
    }

    // Some base-2 integer functions

    // Returns 2 raised to the power v
    public static int pow2(int v) {
        return 1 << v;
    }

    // Returns whether or not the v is a power of 2
    public static boolean isPow2(int v) {
        return (v & (v - 1)) == 0;
    }

    // Returns log2 of the largest power of two less than or equal to v
    public static int log2Floor(int v) {
        int log = 0;
        if ((v & 0xFFFF0000) != 0) { v >>>= 16; log += 16; }
        if ((v & 0x0000FF00) != 0) { v >>>=  8; log +=  8; }
        if ((v & 0x000000F0) != 0) { v >>>=  4; log +=  4; }
        if ((v & 0x0000000C) != 0) { v >>>=  2; log +=  2; }
        if ((v & 0x00000002) != 0) {            log +=  1; }

        return log;
    }

    // Returns log2 of the smallest power of two greater than or equal to v
    public static int log2Ceil(int v) {
        return log2Floor(2 * v - 1);
    }

    // Returns the largest power of 2 less than or equal to v
    public static int floor2(int v) {
        return 1 << log2Floor(v);
    }

    // Returns the smallest power of 2 greater than or equal to v
    public static int ceil2(int v) {
        return 1 << log2Ceil(v);
    }

    // Returns a Vec3 from an xyz float array at the given vertex index
    public static Vec3 getVec3(float[] arr, int vertexI) {
        int ci = vertexI * 3;
        return new Vec3(arr[ci + 0], arr[ci + 1], arr[ci + 2]);
    }

    public static void getVec3(float[] arr, int vertexI, Vec3 dst) {
        int ci = vertexI * 3;
        dst.x = arr[ci + 0]; dst.y = arr[ci + 1]; dst.z = arr[ci + 2];
    }

    // Sets xyz at the given vertex index to that of the given Vec3
    public static void setVec3(float[] arr, int vertexI, Vec3 v) {
        int ci = vertexI * 3;
        arr[ci + 0] = v.x; arr[ci + 1] = v.y; arr[ci + 2] = v.z;
    }

    public static void add(float[] arr, int vertexI, float v) {
        int ci = vertexI * 3;
        arr[ci + 0] += v;
        arr[ci + 1] += v;
        arr[ci + 2] += v;
    }

    public static void multiply(float[] arr, int vertexI, float v) {
        int ci = vertexI * 3;
        arr[ci + 0] *= v;
        arr[ci + 1] *= v;
        arr[ci + 2] *= v;
    }

    // Normalizes the xyz at the given vertex index in arr
    public static void normalize(float[] arr, int vertexI) {
        int ci = vertexI * 3;
        float x = arr[ci + 0], y = arr[ci + 1], z = arr[ci + 2];
        float v = x * x + y * y + z * z;
        if (isZero(v)) {
            x = 0.0f; y = 0.0f; z = 0.0f;
        }
        else {
            v = 1.0f / (float)Math.sqrt(v);
            x *= v; y *= v; z *= v;
        }
        arr[ci + 0] = x; arr[ci + 1] = y; arr[ci + 2] = z;
    }

}
