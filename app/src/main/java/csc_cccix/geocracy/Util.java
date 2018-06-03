package csc_cccix.geocracy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES30;
import android.os.Process;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import glm_.vec2.Vec2;
import glm_.vec3.Vec3;
import glm_.vec4.Vec4;

import static android.opengl.GLU.gluErrorString;
import static glm_.Java.glm;

public abstract class Util {

    private Util() {

    }

    // For use with floating point comparisons
    public static final float EPSILON = 1e-6f;

    // PI
    public static final float PI = glm_.glm.PIf;
    // Golden ratio
    public static final float PHI = (float)((1.0 + Math.sqrt(5.0)) / 2.0);

    // Save Game Save
    public static void saveObjectToSharedPreference(Context context, String preferenceFileName, String serializedObjectKey, Object object) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(object);
        Log.i("SERIALIZED", serializedObject);
        sharedPreferencesEditor.putString(serializedObjectKey, serializedObject);
        sharedPreferencesEditor.apply();
    }

    // Load Game Save
    public static <T> T getSavedObjectFromPreference(Context context, String preferenceFileName, String preferenceKey, Class<T> classType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        if (sharedPreferences.contains(preferenceKey)) {
            final Gson gson = new Gson();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
        }
        return null;
    }

    // Delete Game Save -- need to test
    public static void deleteObjectFromSharedPreference(Context context, String preferenceFileName, String serializedObjectKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.remove(serializedObjectKey);
        sharedPreferencesEditor.apply();
    }

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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Reads image file from assets folder into Bitmap
    public static Bitmap readBitmap(String filename) {
        try {
            return BitmapFactory.decodeStream(Global.getContext().getAssets().open(filename));
        } catch (IOException e) {
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

        if (printError) Log.e("OpenGL", Integer.toHexString(error) + "(" + gluErrorString(error) + ")", new Exception());
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

    // Some base-2 integer functions

    // Returns 2 raised to the power v
    public static int pow2(int v) { return 1 << v; }

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

    public static void assign(Vec3 dst, Vec3 src) {
        dst.x = src.x;
        dst.y = src.y;
        dst.z = src.z;
    }

    public static long toLong(int low, int high) {
        return ((long)low & 0xFFFFFFFFL) | ((long)high << 32);
    }

    public static Pair<Integer, Integer> fromLong(long v) {
        return new Pair<>((int)(v & 0xFFFFFFFFL), (int)(v >> 32));
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    // I hate Java
    public static int toInt(byte lowest, byte low, byte high, byte highest) {
        return ((int)highest << 24) | (((int)high & 0xFF) << 16) | (((int)low & 0xFF) << 8) | ((int)lowest & 0xFF);
    }

    public static int toInt(short low, short high) {
        return ((int)low & 0xFFFF) | ((int)high << 16);
    }

    public static Vec3 cylindricToCartesian(float radius, float theta, float z) {
        return new Vec3(radius * Math.cos(theta), radius * Math.sin(theta), z);
    }

    // Returns the ith of n evenly distributed points on the unit sphere
    public static Vec3 pointOnSphereFibonacci(int i, int n) {
        float z = 1.0f - (float)(2 * i) / (float)(n - 1);
        float theta = 2.0f * PI * (2.0f - PHI) * (float)i;
        return cylindricToCartesian((float)Math.sqrt(1.0f - z * z), theta, z);
    }

    // Returns random point evenly distributed on unit sphere
    public static Vec3 pointOnSphereRandom(Random rand) {
        float z = rand.nextFloat() * 2.0f - 1.0f;
        float theta = rand.nextFloat() * 2.0f * PI;
        return cylindricToCartesian((float)Math.sqrt(1.0f - z * z), theta, z);
    }

    // Get RGB color from Hue Saturation Luminosity
    // All three inputs are between 0.0 and 1.0
    public static Vec3 hsv2rgb(float hue, float sat, float val) {
        int color = Color.HSVToColor(new float[]{ 360.0f * hue, sat, val });
        final float factor = 1.0f / 255.0f;
        return new Vec3(
            ((color >> 16) & 0xFF) * factor,
            ((color >> 8) & 0xFF) * factor,
            (color & 0xFF) * factor
        );
    }

    public static Vec3[] genDistinctColors(int n, float hueOffset) {
        Vec3[] colors = new Vec3[n];
        for(int i = 0; i < n; ++i) {
            colors[i] = hsv2rgb(glm.fract((float)i / n + hueOffset), 1.0f, 1.0f);
        }
        return colors;
    }

    // Returns v rotated 90 degrees CCW
    public static Vec2 ortho(Vec2 v) {
        return new Vec2(-v.y, +v.x);
    }

    // Returns an arbitrary unit vector orthogonal to v
    public static Vec3 ortho(Vec3 v) {
        if (glm.abs(v.z) <= glm.abs(v.y) && glm.abs(v.z) <= glm.abs(v.x)) { // z is smallest
            return new Vec3(-v.y, +v.x, 0.0f).normalizeAssign(); // rotate around z
        }
        else if (glm.abs(v.y) <= glm.abs(v.x)) { // y is smallest
            return new Vec3(+v.z, 0.0f, -v.x).normalizeAssign(); // rotate around y
        }
        else { // x is smallest
            return new Vec3(0.0f, -v.z, +v.y).normalizeAssign(); // rotate around x
        }
    }

    // Sinusoidal approximation between 0 and 1
    public static float smoothstep(float p) {
        float pp = p * p;
        return 3 * pp - 2 * pp * p;
    }

    // Returns a random element of the set. Since HashSets do not offer random access, this is O(n/2)
    public static <T> T pickRandom(Set<T> set, Random rand) {
        int i = rand.nextInt(set.size());
        Iterator<T> it = set.iterator();
        while (i > 0) { it.next(); --i; }
        return it.next();
    }

}
